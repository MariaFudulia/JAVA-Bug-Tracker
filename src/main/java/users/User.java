package users;

import milestones.Milestone;
import tickets.Ticket;

import java.util.List;
import java.util.Map;

public abstract class User {
    protected String username;
    protected String email;
    protected UserRole role;

    public User(String username, String email, UserRole role) {
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public UserRole getRole() {
        return role;
    }

    public abstract List<Ticket> getVisibleTickets(Map<Integer, Ticket> tickets);
    public abstract List<Milestone> getVisibleMilestones(Map<String, Milestone> milestones);
    public abstract boolean canCreateMilestone();
    public abstract boolean canViewMilestone();

}
