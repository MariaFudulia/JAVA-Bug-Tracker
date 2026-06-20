package users;

import database.MilestoneDatabase;
import milestones.Milestone;
import services.TicketService;
import tickets.Ticket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Reporter extends User {
    public Reporter(final String username, final String email) {
        super(username, email, UserRole.REPORTER);
    }

    /**
     *
     * @param tickets
     * @param milestoneDatabase
     * @param ticketService
     * @return visible tickets to user
     */
    @Override
    public List<Ticket> getVisibleTickets(final Map<Integer, Ticket> tickets,
                                          final MilestoneDatabase milestoneDatabase,
                                          final TicketService ticketService) {
        return tickets.values().stream()
                .filter(t -> username.equals(t.getReportedBy())).toList();
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
     * @return false
     */
    @Override
    public boolean canViewMilestone() {
        return false;
    }

    /**
     *
     * @param milestones
     * @return new array
     */
    @Override
    public List<Milestone> getVisibleMilestones(final Map<String, Milestone> milestones) {
        return new ArrayList<>();
    }

    /**
     *
     * @return false
     */
    @Override
    public boolean canAssignTicket() {
        return false;
    }

    /**
     *
     * @return false
     */
    @Override
    public boolean canViewAssignedTickets() {
        return false;
    }

    /**
     *
     * @return false
     */
    @Override
    public boolean canUndoAssignTicket() {
        return false;
    }

    /**
     *
     * @return true if user can add comment
     */
    @Override
    public boolean canAddComment() {
        return true;
    }
}
