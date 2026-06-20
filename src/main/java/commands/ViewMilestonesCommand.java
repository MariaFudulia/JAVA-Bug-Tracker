package commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import context.AppContext;
import database.MilestoneDatabase;
import milestones.Milestone;
import services.MilestoneService;
import services.TicketService;
import users.User;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ViewMilestonesCommand extends Command {
    private MilestoneService milestoneService;
    private MilestoneDatabase milestoneDatabase;
    private AppContext context;
    private TicketService ticketService;

    public ViewMilestonesCommand(final MilestoneService milestoneService,
                                 final AppContext context,
                                 final TicketService ticketService,
                                 final MilestoneDatabase milestoneDatabase) {
        this.milestoneService = milestoneService;
        this.context = context;
        this.ticketService = ticketService;
        this.milestoneDatabase = milestoneDatabase;
    }

    /**
     *
     * @return output
     */
    @Override
    public ObjectNode execute() {
        ObjectMapper mapper = context.getMapper();
        ObjectNode node = mapper.createObjectNode();

        node.put("command", "viewMilestones");
        node.put("username", context.getInput().get("username").asText());
        node.put("timestamp", context.getInput().get("timestamp").asText());

        User user = context.getUserDatabase().getUser(context.getInput().get("username").asText());
        List<Milestone> visibleMilestones = user.getVisibleMilestones(context
                .getMilestoneDatabase().getMilestones());
        visibleMilestones = milestoneDatabase
                .getMilestonesSortedByDueDateAndName(visibleMilestones);

        ArrayNode milestonesArray = mapper.createArrayNode();

        for (Milestone milestone : visibleMilestones) {
            ObjectNode milestoneNode = mapper.createObjectNode();

            milestoneNode.put("name", milestone.getName());
            milestoneNode.put("name", milestone.getName());
            milestoneNode.set("blockingFor", mapper.valueToTree(milestone.getBlockingFor()));
            milestoneNode.put("dueDate", milestone.getDueDate().toString());
            milestoneNode.put("createdAt", milestone.getCreatedAt().toString());
            milestoneNode.set("tickets", mapper.valueToTree(milestone.getTickets()));
            milestoneNode.set("assignedDevs", mapper.valueToTree(milestone.getAssignedDevs()));
            milestoneNode.put("createdBy", milestone.getCreatedBy());

            milestoneNode.put("status", milestone.getStatus().name());
            milestoneNode.put("isBlocked", milestone.isBlocked());
            milestoneNode.put("daysUntilDue", milestone.getDaysUntilDue());
            milestoneNode.put("overdueBy", milestone.getOverdueBy());
            milestoneNode.set("openTickets",
                    mapper.valueToTree(milestone.getOpenTickets(ticketService)));
            milestoneNode.set("closedTickets",
                    mapper.valueToTree(milestone.getClosedTickets(ticketService)));
            milestoneNode.put("completionPercentage",
                    milestone.getCompletionPercentage(ticketService));

            ArrayNode repartitionNode = mapper.createArrayNode();
            Map<String, List<Integer>> repartition = milestone.getRepartition();
            repartition.entrySet().stream().sorted(Comparator.<Map.Entry<String,
                    List<Integer>>>comparingInt(e -> e.getValue().size()
            ).thenComparing(Map.Entry::getKey)).forEach(entry -> {
                ObjectNode rep = mapper.createObjectNode();
                rep.put("developer", entry.getKey());
                rep.set("assignedTickets",
                        mapper.valueToTree(entry.getValue()));
                repartitionNode.add(rep); });

            milestoneNode.set("repartition", repartitionNode);
            milestonesArray.add(milestoneNode);
        }

        node.set("milestones", milestonesArray);
        return node;
    }
}
