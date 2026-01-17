package milestones;

import services.TicketService;
import tickets.Ticket;
import tickets.TicketStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Milestone {
    private final String name;
    private final List<Integer> tickets;
    private final List<String> assignedDevs;
    private final List<String> blockingFor;

    private final String createdBy;
    private final LocalDate createdAt;
    private final LocalDate dueDate;

    private boolean isBlocked = false;
    private LocalDate lastUpdatedAt;
    private MilestoneStatus status;
    private float completionPercentage = 0.0f;
    private Map<String, List<Integer>> repartition;

    private int daysUntilDue;
    private int overdueBy;

    Milestone(String name,
              LocalDate createdAt,
              LocalDate dueDate,
              String createdBy,
              List<Integer> tickets,
              List<String> assignedDevs,
              List<String> blockingFor) {

        this.name = name;
        this.createdAt = createdAt;
        this.dueDate = dueDate;
        this.createdBy = createdBy;
        this.tickets = tickets;
        this.assignedDevs = assignedDevs;
        this.blockingFor = blockingFor;
        this.status = MilestoneStatus.ACTIVE;
        this.isBlocked = false;
        this.lastUpdatedAt = createdAt;
        repartition = new HashMap<>();
        for (String dev : assignedDevs) {
            repartition.put(dev, new ArrayList<>());
        }
    }

    public List<Integer> getTickets() {
        return tickets;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public String getName() {
        return name;
    }
    public String getCreatedBy() {
        return createdBy;
    }

    public List<String> getBlockingFor() {
        return blockingFor;
    }

    public List<String> getAssignedDevs() {
        return assignedDevs;
    }

    public int getDaysUntilDue() {
        return daysUntilDue;
    }

    public int getOverdueBy() {
        return overdueBy;
    }

    public Map<String, List<Integer>> getRepartition() {
        return repartition;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public MilestoneStatus getStatus() {
        return status;
    }

    public void setDaysUntilDue(int daysUntilDue) {
        this.daysUntilDue = daysUntilDue;
    }

    public void setOverdueBy(int overdueBy) {
        this.overdueBy = overdueBy;
    }

    public void setIsBlocked(boolean isBlocked) {
        this.isBlocked = isBlocked;
    }

    public List<Integer> getOpenTickets(TicketService ticketService) {
        List<Integer> openTickets = new ArrayList<>();
        for (Integer ticketId : tickets) {
            Ticket ticket = ticketService.getTicketById(ticketId);
            if (ticket.getStatus() == TicketStatus.OPEN)
                openTickets.add(ticketId);
        }
        return openTickets;
    }

    public List<Integer> getClosedTickets(TicketService ticketService) {
        List<Integer> closedTickets = new ArrayList<>();
        for (Integer ticketId : tickets) {
            Ticket ticket = ticketService.getTicketById(ticketId);
            if (ticket.getStatus() == TicketStatus.CLOSED)
                closedTickets.add(ticketId);
        }
        return closedTickets;
    }

    public double getCompletionPercentage(TicketService ticketService) {
        int closedCount = getClosedTickets(ticketService).size();
        int total = tickets.size();
        return total == 0
                ? 0.0
                : Math.round((double) closedCount * 10000 / total) / 100.0;

    }
}
