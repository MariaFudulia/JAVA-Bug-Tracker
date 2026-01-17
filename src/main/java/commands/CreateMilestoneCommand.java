package commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import context.AppContext;
import database.UserDatabase;
import milestones.Milestone;
import services.MilestoneService;
import services.TicketService;
import tickets.Ticket;
import users.User;

public class CreateMilestoneCommand extends Command {
    MilestoneService milestoneService;
    AppContext context;
    TicketService ticketService;

    public CreateMilestoneCommand(final MilestoneService milestoneService,
                                  final AppContext context,
                                  final TicketService ticketService) {
        this.milestoneService = milestoneService;
        this.context = context;
        this.ticketService = ticketService;
    }

    @Override
    public ObjectNode execute() {
        JsonNode input = context.getInput();
        JsonNode params = input.get("params");
        if (context.isInTesting()) {
            ObjectNode node = new ObjectMapper().createObjectNode();
            node.put("command", input.get("command").asText());
            node.put("username", input.get("username").asText());
            node.put("timestamp", input.get("timestamp").asText());
            node.put("error", "Can not create milestone in Testing Phase.");
            return node;
        }

        String username = input.get("username").asText();
        UserDatabase userDatabase = context.getUserDatabase();

        if (userDatabase.userExists(username)
                && !userDatabase.getUser(username).canCreateMilestone()) {
            User user = userDatabase.getUser(username);
            ObjectNode node = new ObjectMapper().createObjectNode();
            node.put("command", input.get("command").asText());
            node.put("username", username);
            node.put("timestamp", input.get("timestamp").asText());
            node.put("error", "The user does not have permission to execute this command: " +
                    "required role MANAGER; user role " + user.getRole() + ".");
            return node;
        }

        if (milestoneService.TicketsAlreadyAssigned(context, ticketService) != -1) {
            int id = milestoneService.TicketsAlreadyAssigned(context, ticketService);
            Ticket ticket = ticketService.getTicketById(id);
            ObjectNode node = new ObjectMapper().createObjectNode();
            node.put("command", input.get("command").asText());
            node.put("username", username);
            node.put("timestamp", input.get("timestamp").asText());
            node.put("error", "Tickets " + id
                    + " already assigned to milestone "
                    + ticket.getBelongsToMilestone() + ".");
            return node;
        }

        Milestone milestone = milestoneService.createMilestone(context);
        milestoneService.assignTicketsToMilestone(context, ticketService);
        milestoneService.blockOtherMilestones(context);
        milestoneService.assignDevsToMilestone(context);
        milestoneService.appointManagerToMilestone(context);
        return null;
    }

}
