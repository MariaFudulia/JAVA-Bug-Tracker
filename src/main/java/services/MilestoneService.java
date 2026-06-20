package services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import context.AppContext;
import database.MilestoneDatabase;
import milestones.Milestone;
import milestones.MilestoneBuilder;
import milestones.MilestoneStatus;
import tickets.Ticket;
import tickets.TicketAction;
import tickets.TicketPriority;
import tickets.TicketStatus;
import users.Developer;
import users.Manager;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

/**
 * Service responsible for managing the lifecycle of milestones, including creation,
 * dependency management, and priority updates based on time progression.
 */
public final class MilestoneService {
    private static final int PRIORITY_STEP_DAYS = 3;
    private static final int DAYS_BEFORE_DUE_CRITICAL = 1;
    private static final int DAYS_BEFORE_DUE_NOTIFY = 2;
    private static final int OVERDUE_OFFSET = 1;

    private MilestoneDatabase milestoneDatabase;

    public MilestoneService(final MilestoneDatabase milestoneDatabase) {
        this.milestoneDatabase = milestoneDatabase;
    }

    /**
     * Creates a new milestone from the input context.
     * @param context the application context
     * @return the newly created milestone
     */
    public Milestone createMilestone(final AppContext context) {
        JsonNode input = context.getInput();
        ObjectMapper mapper = new ObjectMapper();

        Milestone milestone = new MilestoneBuilder()
                .nameB(input.get("name").asText())
                .createdAtB(LocalDate.parse(input.get("timestamp").asText()))
                .dueDateB(LocalDate.parse(input.get("dueDate").asText()))
                .createdByB(input.get("username").asText())
                .ticketsB(mapper.convertValue(input.get("tickets"),
                        new TypeReference<List<Integer>>() { }))
                .assignedDevsB(mapper.convertValue(
                        input.get("assignedDevs"),
                        new TypeReference<List<String>>() { }))
                .blockingForB(mapper.convertValue(
                        input.get("blockingFor"),
                        new TypeReference<List<String>>() { }))
                .build();

        String name = input.get("name").asText();
        milestoneDatabase.addMilestone(name, milestone);
        return milestone;
    }

    /**
     * Checks if tickets are already assigned to other milestones.
     * @param context the application context
     * @param ticketService service for ticket operations
     * @return the ID of the first already assigned ticket, or -1
     */
    public int ticketsAlreadyAssigned(final AppContext context,
                                      final TicketService ticketService) {
        JsonNode input = context.getInput();
        ObjectMapper mapper = new ObjectMapper();

        List<Integer> ticketIds = mapper.convertValue(input.get("tickets"),
                new TypeReference<List<Integer>>() { });

        for (Integer ticketId : ticketIds) {
            if (ticketService.getTicketById(ticketId).isAssignedToAMilestone()) {
                return ticketId;
            }
        }

        return -1;
    }

    /**
     * Links tickets to a specific milestone and adds history actions.
     * @param context the application context
     * @param ticketService service for ticket operations
     */
    public void assignTicketsToMilestone(final AppContext context,
                                         final TicketService ticketService) {
        JsonNode input = context.getInput();
        List<Integer> ticketIds = new ObjectMapper().convertValue(input.get("tickets"),
                new TypeReference<List<Integer>>() { });

        String timestamp = input.get("timestamp").asText();
        LocalDate now = LocalDate.parse(timestamp);

        for (Integer ticketId : ticketIds) {
            Ticket t = ticketService.getTicketById(ticketId);
            t.getActions().add(TicketAction.addedToMilestone(input.get("name").asText(),
                    input.get("username").asText(), now));
            t.setIsAssignedToAMilestone(true);
            t.setBelongsToMilestone(input.get("name").asText());
        }
    }

    /**
     * Assigns developers to a milestone and updates their personal lists.
     * @param context the application context
     */
    public void assignDevsToMilestone(final AppContext context) {
        JsonNode input = context.getInput();
        List<String> assignedDevs = new ObjectMapper().convertValue(
                input.get("assignedDevs"),
                new TypeReference<List<String>>() { });

        Milestone milestone = milestoneDatabase.getMilestone(input.get("name").asText());
        for (String dev : assignedDevs) {
            Developer user = (Developer) context.getUserDatabase().getUser(dev);
            milestone.addObserver(user);
            user.addToMilestoneList(input.get("name").asText());
        }
    }

    /**
     * Links a manager to a milestone.
     * @param context the application context
     */
    public void appointManagerToMilestone(final AppContext context) {
        JsonNode input = context.getInput();
        Manager user = (Manager) context.getUserDatabase().getUser(input.get("username").asText());
        user.addToMilestoneList(input.get("name").asText());
    }

    /**
     * Marks milestones as blocked based on dependencies.
     * @param context the application context
     */
    public void blockOtherMilestones(final AppContext context) {
        JsonNode input = context.getInput();
        List<String> blockingFor = new ObjectMapper().convertValue(
                input.get("blockingFor"),
                new TypeReference<List<String>>() { });

        for (String name : blockingFor) {
            milestoneDatabase.getMilestone(name).setIsBlocked(true);
        }
    }

    /**
     * Refreshes all milestone states.
     * @param context the application context
     * @param ticketService service for ticket operations
     */
    public void refreshMilestone(final AppContext context,
                                 final TicketService ticketService) {
        applyPriorityUpdates(context, ticketService);
        updateMetadata(context, ticketService);
    }

    /**
     * Updates ticket priorities based on time elapsed since milestone creation.
     * @param context the application context
     * @param ticketService service for ticket operations
     */
    public void applyPriorityUpdates(final AppContext context,
                                     final TicketService ticketService) {
        JsonNode input = context.getInput();
        Map<String, Milestone> milestones = milestoneDatabase.getMilestones();
        LocalDate now = LocalDate.parse(input.get("timestamp").asText());

        for (Milestone milestone : milestones.values()) {
            if (milestone.isBlocked()) {
                continue;
            }
            long daysSinceCreation = ChronoUnit.DAYS.between(milestone.getCreatedAt(), now) + 1;
            long daysUntilDue = ChronoUnit.DAYS.between(now, milestone.getDueDate()) + 1;

            for (Integer ticketId : milestone.getTickets()) {
                Ticket ticket = ticketService.getTicketById(ticketId);
                if (ticket.getStatus() == TicketStatus.CLOSED) {
                    continue;
                }
                if (daysUntilDue <= DAYS_BEFORE_DUE_CRITICAL) {
                    ticket.setBusinessPriority(TicketPriority.CRITICAL);
                } else if (daysSinceCreation >= PRIORITY_STEP_DAYS) {
                    int steps = (int) (daysSinceCreation / PRIORITY_STEP_DAYS);
                    ticket.increasePriorityBy(steps);
                }
            }
            if (daysUntilDue == DAYS_BEFORE_DUE_NOTIFY) {
                String msg = "Milestone " + milestone.getName() + " is due tomorrow. "
                        + "All unresolved tickets are now CRITICAL.";
                milestone.notifyObservers(msg);
            }
        }
    }

    /**
     * Updates milestone metadata including completion and overdue status.
     * @param context the application context
     * @param ticketService service for ticket operations
     */
    public void updateMetadata(final AppContext context,
                               final TicketService ticketService) {
        JsonNode input = context.getInput();
        Map<String, Milestone> milestones = milestoneDatabase.getMilestones();
        LocalDate now = LocalDate.parse(input.get("timestamp").asText());

        for (Milestone milestone : milestones.values()) {
            updateMilestoneStatus(milestone, ticketService, now);
            milestone.setCompletionPercentage(milestone.getCompletionPercentage(ticketService));
            long days = ChronoUnit.DAYS.between(now, milestone.getDueDate()) + 1;
            long overdue = ChronoUnit.DAYS.between(now, milestone.getDueDate());

            if (milestone.getStatus() != MilestoneStatus.COMPLETED) {
                milestone.setDaysUntilDue((int) Math.max(0, days));
                milestone.setOverdueBy((int) (overdue < 0 ? Math.abs(overdue) + 1 : 0));
            }
        }
    }

    /**
     * Updates the status of a milestone and unblocks dependent milestones if completed.
     * @param milestone the milestone to update
     * @param ticketService service for ticket operations
     * @param now current date
     */
    public void updateMilestoneStatus(final Milestone milestone,
                                      final TicketService ticketService,
                                      final LocalDate now) {
        if (milestone.getOpenTickets(ticketService).isEmpty()) {
            milestone.setStatus(MilestoneStatus.COMPLETED);
            for (String milestoneName : milestone.getBlockingFor()) {
                Milestone blockedMilestone = milestoneDatabase.getMilestone(milestoneName);
                blockedMilestone.setIsBlocked(false);

                if (now.isAfter(blockedMilestone.getDueDate())) {
                    String mg = "Milestone " + blockedMilestone.getName()
                            + " was unblocked after due date. "
                            + "All active tickets are now CRITICAL.";
                    blockedMilestone.notifyObservers(mg);
                } else {
                    String msg = "Milestone " + blockedMilestone.getName()
                            + " is now unblocked as ticket "
                            + milestone.getLastClosedTicketId() + " has been CLOSED";
                    blockedMilestone.notifyObservers(msg);
                }
            }
        }
    }
}
