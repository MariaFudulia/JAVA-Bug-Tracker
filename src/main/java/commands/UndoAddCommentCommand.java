package commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import context.AppContext;
import database.UserDatabase;
import services.TicketService;
import tickets.Ticket;
import users.User;

public class UndoAddCommentCommand extends Command {
    private TicketService ticketService;
    private AppContext context;

    public UndoAddCommentCommand(final TicketService ticketService,
                                 final AppContext appContext) {
        this.ticketService = ticketService;
        this.context = appContext;
    }

    /**
     *
     * @return output
     */
    @Override
    public ObjectNode  execute() {
        ObjectMapper mapper = context.getMapper();
        JsonNode input = context.getInput();
        ObjectNode node = mapper.createObjectNode();
        node.put("command", "undoAddComment");
        node.put("username", input.get("username").asText());
        node.put("timestamp", input.get("timestamp").asText());
        UserDatabase userDatabase = context.getUserDatabase();
        User user = userDatabase.getUser(input.get("username").asText());

        if (!user.canAddComment()) {
            node.put("error", "The user does not have permission to execute this command:"
                    + " required role DEVELOPER, REPORTER; user role MANAGER.");
        }

        int id = input.get("ticketID").asInt();
        Ticket ticket = ticketService.getTicketById(id);
        if (ticketService.getTicketById(id) == null) {
            return null;
        }

        if (ticket.getReportedBy().isEmpty()) {
            node.put("error", "Comments are not allowed on anonymous tickets.");
            return node;
        }

        ticket.removeLastCommentAddedBy(user.getUsername());
        return null;
    }
}
