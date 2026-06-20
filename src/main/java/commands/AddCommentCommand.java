package commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import comments.Comment;
import context.AppContext;
import database.UserDatabase;
import services.TicketService;
import tickets.Ticket;
import tickets.TicketStatus;
import users.User;
import users.Developer;
import users.UserRole;

import java.time.LocalDate;

public class AddCommentCommand extends  Command {
    private AppContext context;
    private TicketService ticketService;

    public AddCommentCommand(final AppContext appContext,
                             final TicketService ticketService) {
        this.context = appContext;
        this.ticketService = ticketService;
    }

    @Override
    public ObjectNode execute() {
        ObjectMapper mapper = context.getMapper();
        JsonNode input = context.getInput();
        ObjectNode node = mapper.createObjectNode();
        node.put("command", "addComment");
        node.put("username", input.get("username").asText());
        node.put("timestamp", input.get("timestamp").asText());

        UserDatabase userDatabase = context.getUserDatabase();
        User user = userDatabase.getUser(input.get("username").asText());

        if (!user.canAddComment()) {
            node.put("error", "The user does not have permission to execute this command:"
                    + " required role DEVELOPER, REPORTER; user role MANAGER.");
        }

        int id = input.get("ticketID").asInt();
        if (ticketService.getTicketById(id) == null) {
            return null;
        }

        Ticket ticket = ticketService.getTicketById(id);
        if (ticket.getReportedBy().isEmpty()) {
            node.put("error", "Comments are not allowed on anonymous tickets.");
            return node;
        }

        if (user.getRole().equals(UserRole.REPORTER)
        && ticket.getStatus().equals(TicketStatus.CLOSED)) {
            node.put("error", "Reporters cannot comment on CLOSED tickets.");
            return node;
        }

        if (input.get("comment").asText().length() < 10) {
            node.put("error", "Comment must be at least 10 characters long.");
            return node;
        }

        if (user.getRole().equals(UserRole.DEVELOPER)) {
            Developer dev = (Developer) user;
            if (!dev.getAssignedTickets().contains(id)) {
                node.put("error", "Ticket " + id + " is not assigned to the developer "
                        + dev.getUsername() + ".");
                return node;
            }
        }

        if (user.getRole().equals(UserRole.REPORTER)
        && !ticket.getReportedBy().equals(input.get("username").asText())) {
            node.put("error", "Reporter " + user.getUsername() + " cannot comment on ticket "
                    + id + ".");
            return node;
        }

        String timestamp = input.get("timestamp").asText();
        LocalDate date = LocalDate.parse(timestamp);
        ticket.addComment(new Comment(user.getUsername(),
                input.get("comment").asText(), date));

        return null;
    }
}
