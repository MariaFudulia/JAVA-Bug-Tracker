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
import tickets.TicketAction;
import tickets.TicketStatus;
import users.Developer;
import users.User;

import java.time.LocalDate;
import java.util.List;

public class AssignTicketCommand extends Command {
    private TicketService ticketService;
    private AppContext context;
    private MilestoneDatabase milestoneDatabase;

    public AssignTicketCommand(final TicketService ticketService,
                               final AppContext context,
                               final MilestoneDatabase milestoneDatabase) {
        this.ticketService = ticketService;
        this.context = context;
        this.milestoneDatabase = milestoneDatabase;
    }

    /**
     *
     * @return object node with output
     */
    @Override
    public ObjectNode execute() {
        ObjectMapper mapper = context.getMapper();
        JsonNode input = context.getInput();
        UserDatabase userDatabase = context.getUserDatabase();
        ObjectNode node = mapper.createObjectNode();
        node.put("command", "assignTicket");
        node.put("username", input.get("username").asText());
        node.put("timestamp", input.get("timestamp").asText());

        String username = input.get("username").asText();
        User user = userDatabase.getUser(username);
        if (!user.canAssignTicket()) {
            node.put("error", "The user does not have permission to execute this command: "
                    + "required role DEVELOPER; user role " + user.getRole() + ".");
            return node;
        }

        int id = input.get("ticketID").asInt();
        Ticket ticket = ticketService.getTicketById(id);
        Milestone milestone = ticket.getMilestoneTicketIsAssignedTo(milestoneDatabase);
        if (!milestone.getAssignedDevs().contains(username)) {
            node.put("error", "Developer " +  username + " is not assigned to milestone "
                    + milestone.getName() + ".");
            return node;
        }

        if (ticket.getStatus() != TicketStatus.OPEN) {
            node.put("error", "Only OPEN tickets can be assigned.");
            return node;
        }

        if (milestone.isBlocked()) {
            node.put("error", "Cannot assign ticket " + id + " from blocked milestone "
            + milestone.getName() + ".");
            return node;
        }

        Developer dev = (Developer) user;
        if (!dev.hasNecessaryExpertise(ticket.getExpertiseArea())) {
            List<String> requiredExpertise = dev.getRequiredExpertiseForTicket(ticket)
                            .stream().map(Enum::name).sorted().toList();
            node.put("error", "Developer " + username + " cannot assign ticket " + id + " due to "
                    + "expertise area. Required: " + String.join(", ", requiredExpertise)
                    + "; Current: " + dev.getExpertiseArea() + ".");
            return node;
        }

        if (!dev.hasAccessPriority(ticket.getBusinessPriority())
                || !dev.hasTicketTypeAccess(ticket.getType())) {
            List<String> requiredSeniority = dev.getRequiredSeniorityForTicket(ticket)
                    .stream().map(Enum::name).sorted().toList();
            node.put("error", "Developer " + username + " cannot assign ticket " + id
                    + " due to seniority level. Required: " + String.join(", ", requiredSeniority)
                    + "; Current: " + dev.getSeniority() + ".");
            return node;
        }

        String timestamp = input.get("timestamp").asText();
        LocalDate now = LocalDate.parse(timestamp);

        dev.getAssignedTickets().add(id);
        ticket.setStatus(TicketStatus.IN_PROGRESS);
        ticket.setAssignedTo(username);
        ticket.setAssignedAt(now);
        milestone.addNewTicketToDevRepartition(username, id);
        ticket.getActions().add(TicketAction.assigned(dev.getUsername(), now));
        ticket.getActions().add(TicketAction.statusChanged(TicketStatus.OPEN,
                ticket.getStatus(), user.getUsername(), now));
        return null;
    }
}
