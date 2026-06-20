package users;

import database.MilestoneDatabase;
import milestones.Milestone;
import services.TicketService;
import tickets.Expertise;
import tickets.Ticket;
import tickets.TicketPriority;
import tickets.TicketStatus;
import tickets.TicketType;
import notifications.Observer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Represents a developer user in the system.
 *
 * <p>
 * This class is not intended for unsafe extension.
 * Subclasses should preserve the existing behavior.
 * </p>
 */
public class Developer extends User implements Observer {

    private static final int CRITICAL_PRIORITY_RANK = 4;
    private static final int HIGH_PRIORITY_RANK = 3;
    private static final int MEDIUM_PRIORITY_RANK = 2;
    private static final int LOW_PRIORITY_RANK = 1;

    private LocalDate hireDate;
    private UserExpertise expertiseArea;
    private UserSeniority seniority;
    private List<String> assignedToMilestones = new ArrayList<>();
    private List<Integer> assignedTickets = new ArrayList<>();
    private List<String> inbox = new ArrayList<>();

    /**
     * Creates a new Developer instance.
     *
     * @param username      developer username
     * @param email         developer email
     * @param hireDate      hire date
     * @param seniority     developer seniority
     * @param expertiseArea developer expertise area
     */
    public Developer(final String username, final String email,
                     final LocalDate hireDate, final UserSeniority seniority,
                     final UserExpertise expertiseArea) {
        super(username, email, UserRole.DEVELOPER);
        this.hireDate = hireDate;
        this.seniority = seniority;
        this.expertiseArea = expertiseArea;
    }

    /**
     * Returns the developer expertise area.
     *
     * @return expertise area
     */
    public UserExpertise getExpertiseArea() {
        return expertiseArea;
    }

    /**
     * Returns the developer seniority.
     *
     * @return seniority
     */
    public UserSeniority getSeniority() {
        return seniority;
    }

    /**
     * Returns the list of assigned ticket IDs.
     *
     * @return assigned ticket IDs
     */
    public List<Integer> getAssignedTickets() {
        return assignedTickets;
    }

    /**
     * Checks if the developer has access to the given ticket.
     *
     * @param ticket ticket to check
     * @return true if access is allowed
     */
    public boolean hasTicketAccess(final Ticket ticket) {
        return hasAccessPriority(ticket.getBusinessPriority())
                && hasTicketTypeAccess(ticket.getType())
                && hasNecessaryExpertise(ticket.getExpertiseArea());
    }

    /**
     * Checks if the developer can access a ticket based on priority.
     *
     * @param priority ticket priority
     * @return true if access is allowed
     */
    public boolean hasAccessPriority(final TicketPriority priority) {
        return switch (seniority) {
            case JUNIOR -> priority == TicketPriority.LOW
                    || priority == TicketPriority.MEDIUM;
            case MID -> priority != TicketPriority.CRITICAL;
            default -> true;
        };
    }

    /**
     * Checks if the developer can access a ticket based on type.
     *
     * @param type ticket type
     * @return true if access is allowed
     */
    public boolean hasTicketTypeAccess(final TicketType type) {
        return switch (seniority) {
            case JUNIOR -> type != TicketType.FEATURE_REQUEST;
            default -> true;
        };
    }

    /**
     * Checks if the developer has the required expertise.
     *
     * @param expertise required expertise
     * @return true if expertise matches
     */
    public boolean hasNecessaryExpertise(final Expertise expertise) {
        return switch (expertiseArea) {
            case FRONTEND -> expertise == Expertise.FRONTEND
                    || expertise == Expertise.DESIGN;
            case BACKEND -> expertise == Expertise.BACKEND
                    || expertise == Expertise.DB;
            case FULLSTACK -> expertise == Expertise.FRONTEND
                    || expertise == Expertise.BACKEND
                    || expertise == Expertise.DEVOPS
                    || expertise == Expertise.DESIGN
                    || expertise == Expertise.DB;
            case DEVOPS -> expertise == Expertise.DEVOPS;
            case DESIGN -> expertise == Expertise.DESIGN
                    || expertise == Expertise.FRONTEND;
            case DB -> expertise == Expertise.DB;
        };
    }

    /**
     * Assigns the developer to a milestone.
     *
     * @param milestone milestone name
     */
    public void addToMilestoneList(final String milestone) {
        assignedToMilestones.add(milestone);
    }

    /**
     * Checks if the developer is assigned to a milestone.
     *
     * @param milestone milestone name
     * @return true if assigned
     */
    public boolean isAssignedToMilestone(final String milestone) {
        return assignedToMilestones.contains(milestone);
    }

    /**
     * Returns the required expertise for a ticket.
     *
     * @param ticket ticket
     * @return list of required expertise areas
     */
    public List<UserExpertise> getRequiredExpertiseForTicket(final Ticket ticket) {
        return switch (ticket.getExpertiseArea()) {
            case FRONTEND -> List.of(UserExpertise.FULLSTACK,
                    UserExpertise.FRONTEND,
                    UserExpertise.DESIGN);
            case BACKEND -> List.of(UserExpertise.BACKEND,
                    UserExpertise.FULLSTACK);
            case DEVOPS -> List.of(UserExpertise.DEVOPS,
                    UserExpertise.FULLSTACK);
            case DESIGN -> List.of(UserExpertise.DESIGN,
                    UserExpertise.FRONTEND,
                    UserExpertise.FULLSTACK);
            case DB -> List.of(UserExpertise.DB,
                    UserExpertise.FULLSTACK,
                    UserExpertise.BACKEND);
        };
    }

    /**
     * Returns the required seniority for a ticket.
     *
     * @param ticket ticket
     * @return list of allowed seniority levels
     */
    public List<UserSeniority> getRequiredSeniorityForTicket(final Ticket ticket) {
        return switch (ticket.getBusinessPriority()) {
            case LOW, MEDIUM ->
                    List.of(UserSeniority.JUNIOR,
                            UserSeniority.MID,
                            UserSeniority.SENIOR);
            case HIGH ->
                    List.of(UserSeniority.MID,
                            UserSeniority.SENIOR);
            case CRITICAL ->
                    List.of(UserSeniority.SENIOR);
        };
    }

    /**
     * Returns tickets visible to the developer.
     */
    @Override
    public List<Ticket> getVisibleTickets(final Map<Integer, Ticket> tickets,
                                          final MilestoneDatabase milestoneDatabase,
                                          final TicketService ticketService) {
        List<Ticket> visibleTickets = new ArrayList<>();

        for (String m : assignedToMilestones) {
            Milestone milestone = milestoneDatabase.getMilestone(m);
            for (Integer id : milestone.getTickets()) {
                Ticket ticket = ticketService.getTicketById(id);
                if (ticket.getStatus() != TicketStatus.OPEN) {
                    continue;
                }
                visibleTickets.add(ticket);
            }
        }
        return visibleTickets;
    }

    private int priorityRank(final TicketPriority priority) {
        return switch (priority) {
            case CRITICAL -> CRITICAL_PRIORITY_RANK;
            case HIGH -> HIGH_PRIORITY_RANK;
            case MEDIUM -> MEDIUM_PRIORITY_RANK;
            case LOW -> LOW_PRIORITY_RANK;
        };
    }

    /**
     * Returns assigned tickets sorted by priority and creation date.
     */
    public List<Ticket> getAssignedTicketsSorted(final TicketService ticketService) {
        List<Ticket> tickets = new ArrayList<>();

        for (Integer id : assignedTickets) {
            tickets.add(ticketService.getTicketById(id));
        }

        tickets.sort(Comparator
                .comparingInt((Ticket t) -> priorityRank(t.getBusinessPriority()))
                .reversed()
                .thenComparing(Ticket::getCreatedAt)
                .thenComparingInt(Ticket::getId));

        return tickets;
    }

    /**
     *
     * @return false
     */
    @Override
    public boolean canCreateMilestone() {
        return false;
    }

    /**
     *
     * @return true
     */
    @Override
    public boolean canViewMilestone() {
        return true;
    }

    /**
     *
     * @param milestones
     * @return visible milestones
     */
    @Override
    public List<Milestone> getVisibleMilestones(final Map<String, Milestone> milestones) {
        List<Milestone> visibleMilestones = new ArrayList<>();

        for (Milestone milestone : milestones.values()) {
            if (isAssignedToMilestone(milestone.getName())) {
                visibleMilestones.add(milestone);
            }
        }
        return visibleMilestones;
    }

    /**
     *
     * @return cand assign
     */
    @Override
    public boolean canAssignTicket() {
        return true;
    }

    /**
     *
     * @return can view assigned
     */
    @Override
    public boolean canViewAssignedTickets() {
        return true;
    }

    /**
     *
     * @return dev can undo assign
     */
    @Override
    public boolean canUndoAssignTicket() {
        return true;
    }

    /**
     *
     * @return dev can add comment
     */
    @Override
    public boolean canAddComment() {
        return true;
    }

    /**
     * Receives a notification.
     *
     * @param notification message
     */
    @Override
    public void receiveNotification(final String notification) {
        inbox.add(notification);
    }

    /**
     * Consumes and clears notifications.
     *
     * @return list of notifications
     */
    public List<String> consumeNotifications() {
        List<String> notifs = new ArrayList<>(inbox);
        inbox.clear();
        return notifs;
    }

    /**
     * Returns the milestones assigned to this developer.
     *
     * @return list of milestone names
     */
    public List<String> getAssignedToMilestones() {
        return assignedToMilestones;
    }
}
