package commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import context.AppContext;
import database.UserDatabase;
import services.TicketService;
import tickets.Ticket;
import tickets.TicketAction;
import tickets.TicketStatus;
import users.Developer;
import users.User;
import users.UserRole;

import java.time.LocalDate;

public class UndoChangeStatusCommand extends Command {
    private AppContext context;
    private TicketService ticketService;

    public UndoChangeStatusCommand(final AppContext context,
                                   final TicketService ticketService) {
        this.context = context;
        this.ticketService = ticketService;
    }

    /**
     *
     * @return output
     */
    @Override
    public ObjectNode execute() {
        ObjectMapper mapper = context.getMapper();
        JsonNode input = context.getInput();
        ObjectNode node = mapper.createObjectNode();
        node.put("command", "undoChangeStatus");
        node.put("username", input.get("username").asText());
        node.put("timestamp", input.get("timestamp").asText());

        UserDatabase userDatabase = context.getUserDatabase();
        User user = userDatabase.getUser(input.get("username").asText());
        if (!user.canViewAssignedTickets()) {
            node.put("error", "The user does not have permission to execute this command:"
                    + " required role DEVELOPER; user role " + user.getRole() + ".");
            return node;
        }

        int id = input.get("ticketID").asInt();
        Ticket ticket = ticketService.getTicketById(id);

        if (user.getRole().equals(UserRole.DEVELOPER)) {
            Developer dev = (Developer) user;
            if (!dev.getAssignedTickets().contains(id)) {
                node.put("error", "Ticket " + id + " is not assigned to developer "
                        + dev.getUsername() + ".");
                return node;
            }
        }

        if (ticket.getStatus() == TicketStatus.IN_PROGRESS) {
            return null;
        }

        ticket.undoStatus();
        String timestamp = input.get("timestamp").asText();
        LocalDate now = LocalDate.parse(timestamp);
        ticket.getActions().add(TicketAction.statusChanged(ticket.getPreviousStatus(),
                ticket.getStatus(), user.getUsername(), now));
        return null;
    }
}
