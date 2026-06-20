package users;

import database.MilestoneDatabase;
import milestones.Milestone;
import services.TicketService;
import tickets.Ticket;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents a manager user in the system.
 *
 * <p>
 * This class is not intended to be extended outside the current design.
 * Subclasses should preserve the existing behavior of all public methods.
 * </p>
 */
public class Manager extends User {

    private LocalDate hireDate;
    private List<String> subordinates;
    private List<String> milestonesCreated = new ArrayList<>();

    /**
     * Creates a new Manager instance.
     *
     * @param username     manager username
     * @param email        manager email
     * @param hireDate     date when the manager was hired
     * @param subordinates list of usernames managed by this manager
     */
    public Manager(final String username, final String email, final LocalDate hireDate,
                   final List<String> subordinates) {
        super(username, email, UserRole.MANAGER);
        this.hireDate = hireDate;
        this.subordinates = subordinates;
    }

    /**
     * Sets the list of subordinates managed by this manager.
     *
     * @param subordinates list of subordinate usernames
     */
    public void setSubordinates(final List<String> subordinates) {
        this.subordinates = subordinates;
    }

    /**
     * Sets the hire date of the manager.
     *
     * @param hireDate hire date
     */
    public void setHireDate(final LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    /**
     * Returns the hire date of the manager.
     *
     * @return hire date
     */
    public LocalDate getHireDate() {
        return hireDate;
    }

    /**
     * Returns the list of subordinates managed by this manager.
     *
     * @return list of subordinate usernames
     */
    public List<String> getSubordinates() {
        return subordinates;
    }

    /**
     * Returns the list of milestone names created by this manager.
     *
     * @return list of milestone names
     */
    public List<String> getMilestonesCreated() {
        return milestonesCreated;
    }

    /**
     * Checks whether the given username is a subordinate of this manager.
     *
     * @param username username to check
     * @return true if the user is a subordinate
     */
    public boolean hasSubordinate(final String username) {
        return subordinates.contains(username);
    }

    /**
     * Adds a milestone to the list of milestones created by this manager.
     *
     * @param milestone milestone name
     */
    public void addToMilestoneList(final String milestone) {
        milestonesCreated.add(milestone);
    }

    /**
     * Checks whether this manager manages the given milestone.
     *
     * @param milestone milestone name
     * @return true if the manager manages the milestone
     */
    public boolean managesMilestone(final String milestone) {
        return milestonesCreated.contains(milestone);
    }

    /**
     * Returns all tickets visible to the manager.
     *
     * @param tickets             all system tickets
     * @param milestoneDatabase   milestone database
     * @param ticketService       ticket service
     * @return list of visible tickets
     */
    @Override
    public List<Ticket> getVisibleTickets(final Map<Integer, Ticket> tickets,
                                          final MilestoneDatabase milestoneDatabase,
                                          final TicketService ticketService) {
        List<Ticket> visibleTickets = new ArrayList<>();

        for (Ticket ticket : tickets.values()) {
            visibleTickets.add(ticket);
        }

        return visibleTickets;
    }

    /**
     * Indicates whether the manager can create milestones.
     *
     * @return true
     */
    @Override
    public boolean canCreateMilestone() {
        return true;
    }

    /**
     * Indicates whether the manager can view milestones.
     *
     * @return true
     */
    @Override
    public boolean canViewMilestone() {
        return true;
    }

    /**
     * Returns the milestones visible to this manager.
     *
     * @param milestones all system milestones
     * @return list of visible milestones
     */
    @Override
    public List<Milestone> getVisibleMilestones(final Map<String, Milestone> milestones) {
        List<Milestone> visibleMilestones = new ArrayList<>();

        for (Milestone milestone : milestones.values()) {
            if (managesMilestone(milestone.getName())) {
                visibleMilestones.add(milestone);
            }
        }

        return visibleMilestones;
    }

    /**
     * Indicates whether the manager can assign tickets.
     *
     * @return false
     */
    @Override
    public boolean canAssignTicket() {
        return false;
    }

    /**
     * Indicates whether the manager can view assigned tickets.
     *
     * @return false
     */
    @Override
    public boolean canViewAssignedTickets() {
        return false;
    }

    /**
     * Indicates whether the manager can undo ticket assignment.
     *
     * @return false
     */
    @Override
    public boolean canUndoAssignTicket() {
        return false;
    }

    /**
     * Indicates whether the manager can add comments.
     *
     * @return false
     */
    @Override
    public boolean canAddComment() {
        return false;
    }
}
