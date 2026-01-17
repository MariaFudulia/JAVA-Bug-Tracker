package users;

import milestones.Milestone;
import tickets.Ticket;
import tickets.TicketStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Reporter extends User{
    public Reporter(String username, String email) {
        super(username, email, UserRole.REPORTER);
    }

    @Override
    public List<Ticket> getVisibleTickets(Map<Integer, Ticket> tickets) {
        return tickets.values().stream()
                .filter(t -> username.equals(t.getReportedBy())).toList();
    }

    @Override
    public boolean canCreateMilestone() {
        return false;
    }

    @Override
    public boolean canViewMilestone() {
        return false;
    }

    @Override
    public List<Milestone> getVisibleMilestones(Map<String, Milestone> milestones) {
        return new ArrayList<>();
    }
}
