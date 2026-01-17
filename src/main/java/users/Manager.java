package users;

import milestones.Milestone;
import tickets.Ticket;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Manager extends User {
    private LocalDate hireDate;
    private List<String> subordinates;
    public List<String> milestonesCreated = new ArrayList<>();

    public Manager(String username, String email, LocalDate hireDate,
                   List<String> subordinates) {
        super(username, email, UserRole.MANAGER);
        this.hireDate = hireDate;
        this.subordinates = subordinates;
    }

    public void setSubordinates(final List<String> subordinates) {
        this.subordinates = subordinates;
    }

    public void setHireDate(final LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public List<String> getSubordinates() {
        return subordinates;
    }

    public boolean hasSubordinate(final String username) {
        return subordinates.contains(username);
    }

    public void addToMilestoneList(final String milestone) {
        milestonesCreated.add(milestone);
    }

    public boolean managesMilestone(final String milestone) {
        return milestonesCreated.contains(milestone);
    }

    @Override
    public List<Ticket> getVisibleTickets(Map<Integer, Ticket> tickets) {
        List<Ticket> visibleTickets = new ArrayList<>();

        for (Ticket ticket : tickets.values()) {
            visibleTickets.add(ticket);
        }

        return visibleTickets;
    }

    @Override
    public boolean canCreateMilestone() {
        return true;
    }

    @Override
    public boolean canViewMilestone() {
        return true;
    }

    @Override
    public List<Milestone> getVisibleMilestones(Map<String, Milestone> milestones) {
        List<Milestone> visibleMilestones = new ArrayList<>();
        for (Milestone milestone : milestones.values()) {
            if (managesMilestone(milestone.getName())) {
                visibleMilestones.add(milestone);
            }
        }

        return visibleMilestones;
    }
}
