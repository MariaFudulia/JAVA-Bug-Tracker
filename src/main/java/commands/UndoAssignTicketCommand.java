package commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import context.AppContext;
import database.MilestoneDatabase;
import database.UserDatabase;
import services.TicketService;
import tickets.Ticket;
import tickets.TicketAction;
import tickets.TicketStatus;
import users.Developer;
import users.User;

import java.time.LocalDate;

public class UndoAssignTicketCommand extends Command {
    private TicketService ticketService;
    private AppContext context;
    private MilestoneDatabase milestoneDatabase;

    public UndoAssignTicketCommand(final TicketService ticketService,
                                   final AppContext context,
                                   final MilestoneDatabase milestoneDatabase) {
        this.ticketService = ticketService;
        this.context = context;
        this.milestoneDatabase = milestoneDatabase;
    }

    /**
     *
     * @return output
     */
    @Override
    public ObjectNode execute() {
        ObjectMapper mapper = context.getMapper();
        JsonNode input = context.getInput();
        UserDatabase userDatabase = context.getUserDatabase();
        ObjectNode node = mapper.createObjectNode();
        node.put("command", "undoAssignTicket");
        node.put("username", input.get("username").asText());
        node.put("timestamp", input.get("timestamp").asText());

        String username = input.get("username").asText();
        User user = userDatabase.getUser(username);
        if (!user.canAssignTicket()) {
            node.put("error", "The user does not have permission to execute this command: "
                    + "required role DEVELOPER; user role " + user.getRole() + ".");
            return node;
        }

        String timestamp = input.get("timestamp").asText();
        LocalDate now = LocalDate.parse(timestamp);
        int id = input.get("ticketID").asInt();
        Ticket ticket = ticketService.getTicketById(id);
        Developer dev = (Developer) user;
        dev.getAssignedTickets().remove(Integer.valueOf(id));
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setAssignedAt(null);
        ticket.setAssignedTo(null);
        ticket.getActions().add(TicketAction.deAssigned(dev.getUsername(), now));
        return null;
    }
}
