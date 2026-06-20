package commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import context.AppContext;
import database.MilestoneDatabase;
import database.UserDatabase;
import milestones.Milestone;
import services.TicketService;
import tickets.Ticket;
import tickets.TicketStatus;
import users.Developer;
import users.User;
import users.UserRole;
import tickets.TicketAction;

import java.time.LocalDate;

public class ChangeStatusCommand extends Command {
    private AppContext context;
    private TicketService ticketService;
    private MilestoneDatabase milestoneDatabase;

    public ChangeStatusCommand(final AppContext appContext,
                               final TicketService ticketService,
                               final MilestoneDatabase milestoneDatabase) {
        this.context = appContext;
        this.ticketService = ticketService;
        this.milestoneDatabase = milestoneDatabase;
    }

    @Override
    public ObjectNode execute() {
        ObjectMapper mapper = context.getMapper();
        JsonNode input = context.getInput();
        ObjectNode node = mapper.createObjectNode();
        node.put("command", "changeStatus");
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

        if (ticket.getStatus() == TicketStatus.CLOSED) {
            return null;
        }

        String timestamp = input.get("timestamp").asText();
        LocalDate now = LocalDate.parse(timestamp);
        ticket.nextStatus(now);
        ticket.getActions().add(TicketAction.statusChanged(ticket.getPreviousStatus(),
                ticket.getStatus(), user.getUsername(), now));

        if (ticket.getStatus() == TicketStatus.CLOSED) {
            Milestone milestone = ticket.getMilestoneTicketIsAssignedTo(milestoneDatabase);
            if (milestone != null) {
                milestone.setLastClosedTicketId(ticket.getId());
            }
        }
        return null;
    }
}
