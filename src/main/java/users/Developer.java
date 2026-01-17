package users;

import milestones.Milestone;
import tickets.Expertise;
import tickets.Ticket;
import tickets.TicketPriority;
import tickets.TicketType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Developer extends User{
    LocalDate hireDate;
    UserExpertise expertiseArea;
    UserSeniority seniority;
    List<String> assignedToMilestones =  new ArrayList<>();

    public Developer(String username, String email,
                     LocalDate hireDate, UserSeniority seniority,
                     UserExpertise expertiseArea) {
        super(username, email, UserRole.DEVELOPER);
        this.hireDate = hireDate;
        this.seniority = seniority;
        this.expertiseArea = expertiseArea;
    }

    public boolean hasTicketAccess(Ticket ticket){
        return hasAccessPriority(ticket.getBusinessPriority()) &&
                hasTicketTypeAccess(ticket.getType()) &&
                hasNecessaryExpertise(ticket.getExpertiseArea());
    }

    private boolean hasAccessPriority(TicketPriority priority){
        return switch (seniority) {
            case JUNIOR -> priority == TicketPriority.LOW || priority == TicketPriority.MEDIUM;
            case MID -> priority != TicketPriority.CRITICAL;
            default -> true;
        };
    }

    private boolean hasTicketTypeAccess(TicketType type){
        return switch (seniority) {
            case JUNIOR -> type != TicketType.FEATURE_REQUEST;
            default -> true;
        };
    }

    private boolean hasNecessaryExpertise(Expertise expertise){
        return switch (expertiseArea) {
            case FRONTEND ->  expertise == Expertise.FRONTEND || expertise == Expertise.DESIGN;
            case BACKEND -> expertise == Expertise.BACKEND || expertise == Expertise.DB;
            case FULLSTACK -> expertise == Expertise.FRONTEND ||
                    expertise == Expertise.BACKEND ||
                    expertise == Expertise.DEVOPS ||
                    expertise == Expertise.DESIGN ||
                    expertise == Expertise.DB;
            case DEVOPS -> expertise == Expertise.DEVOPS;
            case DESIGN -> expertise == Expertise.DESIGN || expertise == Expertise.FRONTEND;
            case DB -> expertise == Expertise.DB;
        };
    }

    public void addToMilestoneList(final String milestone){
        assignedToMilestones.add(milestone);
    }

    public boolean isAssignedToMilestone(final String milestone){
        return assignedToMilestones.contains(milestone);
    }

    @Override
    public List<Ticket> getVisibleTickets(Map<Integer, Ticket> tickets) {
        return new ArrayList<>();
    }

    @Override
    public boolean canCreateMilestone() {
        return false;
    }

    @Override
    public boolean canViewMilestone() {
        return true;
    }

    @Override
    public List<Milestone> getVisibleMilestones(Map<String, Milestone> milestones) {
        List<Milestone> visibleMilestones = new ArrayList<>();
        for (Milestone milestone : milestones.values()) {
            if (isAssignedToMilestone(milestone.getName())) {
                visibleMilestones.add(milestone);
            }
        }
        return visibleMilestones;
    }
}
