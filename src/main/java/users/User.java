package users;

import database.MilestoneDatabase;
import milestones.Milestone;
import services.TicketService;
import tickets.Ticket;

import java.util.List;
import java.util.Map;

public abstract class User {
    protected String username;
    protected String email;
    protected UserRole role;

    public User(final String username, final String email, final UserRole role) {
        this.username = username;
        this.email = email;
        this.role = role;
    }

    /**
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @return role
     */
    public UserRole getRole() {
        return role;
    }

    /**
     *
     * @param tickets
     * @param milestoneDatabase
     * @param ticketService
     * @return visible tickets to user
     */
    public abstract List<Ticket> getVisibleTickets(Map<Integer, Ticket> tickets,
                                                   MilestoneDatabase milestoneDatabase,
                                                   TicketService ticketService);

    /**
     *
     * @param milestones
     * @return visible milestones
     */
    public abstract List<Milestone> getVisibleMilestones(Map<String, Milestone> milestones);

    /**
     *
     * @return true if user can create a milestone
     */
    public abstract boolean canCreateMilestone();

    /**
     *
     * @return true if user can view a milestone
     */
    public abstract boolean canViewMilestone();

    /**
     *
     * @return true if user can assign a ticket
     */
    public abstract boolean canAssignTicket();

    /**
     *
     * @return true if user can view assigned tickets
     */
    public abstract boolean canViewAssignedTickets();

    /**
     *
     * @return true if user can undo assigned tickets
     */
    public abstract boolean canUndoAssignTicket();

    /**
     *
     * @return true if user can add comment
     */
    public abstract boolean canAddComment();
}
