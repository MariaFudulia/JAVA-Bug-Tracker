package commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import context.AppContext;
import database.MilestoneDatabase;
import database.TicketDatabase;
import database.UserDatabase;
import services.TicketService;
import tickets.Ticket;
import tickets.TicketActionType;
import users.Developer;
import users.Manager;
import users.User;
import users.UserRole;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.HashSet;
import java.util.Objects;

public class ViewTicketHistory extends Command {
    private AppContext context;
    private MilestoneDatabase milestoneDatabase;
    private TicketDatabase ticketDatabase;
    private TicketService ticketService;

    public ViewTicketHistory(final AppContext context,
                             final MilestoneDatabase db,
                             final TicketDatabase tdb,
                             final TicketService ts) {
        this.context = context;
        this.milestoneDatabase = db;
        this.ticketDatabase = tdb;
        this.ticketService = ts;
    }

    /**
     *
     * @return output
     */
    @Override
    public ObjectNode  execute() {
        ObjectMapper mapper = context.getMapper();
        JsonNode input = context.getInput();
        UserDatabase userDatabase = context.getUserDatabase();
        User user = userDatabase.getUser(input.get("username").asText());
        ObjectNode node = mapper.createObjectNode();
        node.put("command", "viewTicketHistory");
        node.put("username", input.get("username").asText());
        node.put("timestamp", input.get("timestamp").asText());

        List<Ticket> visibleTickets;

        if (user.getRole().equals(UserRole.DEVELOPER)) {
            visibleTickets = getTicketsForDev((Developer) user);
        } else {
            visibleTickets = getTicketsForManager((Manager) user);
        }

        visibleTickets.sort(Comparator.comparing(Ticket::getCreatedAt)
                .thenComparing(Ticket::getId));

        ArrayNode historyNode = mapper.createArrayNode();
        for (Ticket ticket : visibleTickets) {
            historyNode.add(ticket.ticketToJsonForHistory(mapper, user));
        }

        node.set("ticketHistory", historyNode);
        return node;
    }

    private List<Ticket> getTicketsForDev(final Developer dev) {
        List<Ticket> visible = new ArrayList<>();

        for (Ticket ticket : ticketDatabase.getTickets().values()) {
            boolean isVisible = ticket.getActions().stream()
                    .anyMatch(a -> a.getAction() == TicketActionType.ASSIGNED
                    && dev.getUsername().equals(a.getBy()));

            if (isVisible) {
                visible.add(ticket);
            }
        }
        return visible;
    }

    private List<Ticket> getTicketsForManager(final Manager  manager) {
        Set<Integer> ticketIds = new HashSet<>();

        for (String milestone : manager.getMilestonesCreated()) {
            ticketIds.addAll(milestoneDatabase.getMilestone(milestone).getTickets());
        }

        return ticketIds.stream().map(ticketService::getTicketById)
                .filter(Objects::nonNull).collect(Collectors.toList());
    }
}
