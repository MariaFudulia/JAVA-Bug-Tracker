package milestones;

import notifications.Observer;
import services.TicketService;
import tickets.Ticket;
import tickets.TicketStatus;
import notifications.Subject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Milestone implements Subject {
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
    private double completionPercentage = 0.0;
    private Map<String, List<Integer>> repartition;

    private int daysUntilDue;
    private int overdueBy;

    private List<Observer> observers = new ArrayList<>();
    private int lastClosedTicketId;
    static final int HUNDRED = 100;
    static final double SECOND = 100.0;

    Milestone(final String name, final LocalDate createdAt,
              final LocalDate dueDate, final String createdBy,
              final List<Integer> tickets, final List<String> assignedDevs,
              final List<String> blockingFor) {

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

    /**
     *
     * @return tickets
     */
    public List<Integer> getTickets() {
        return tickets;
    }

    /**
     *
     * @return created at
     */
    public LocalDate getCreatedAt() {
        return createdAt;
    }

    /**
     *
     * @returndue date
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /**
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return creator
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     *
     * @return blocking for
     */
    public List<String> getBlockingFor() {
        return blockingFor;
    }

    /**
     *
     * @return list og assigned devs
     */
    public List<String> getAssignedDevs() {
        return assignedDevs;
    }

    /**
     *
     * @return days until due
     */
    public int getDaysUntilDue() {
        return daysUntilDue;
    }

    /**
     *
     * @return overdue days
     */
    public int getOverdueBy() {
        return overdueBy;
    }

    /**
     *
     * @return repartition
     */
    public Map<String, List<Integer>> getRepartition() {
        return repartition;
    }

    /**
     * @param status
     */
    public void setStatus(final MilestoneStatus status) {
        this.status = status;
    }

    /**
     *
     * @param dev
     * @param id
     */
    public void addNewTicketToDevRepartition(final String dev, final int id) {
        List<Integer> ticketsList = repartition.get(dev);
        ticketsList.add(id);
    }

    /**
     *
     * @param dev
     * @param id
     */
    public void deleteTicketFromDevRepartition(final String dev, final int id) {
        List<Integer> ticketsList = repartition.get(dev);
        ticketsList.remove(id);
    }

    /**
     *
     * @return true if blocked
     */
    public boolean isBlocked() {
        return isBlocked;
    }

    /**
     *
     * @return status
     */
    public MilestoneStatus getStatus() {
        return status;
    }

    /**
     *
     * @param daysUntilDue
     */
    public void setDaysUntilDue(final int daysUntilDue) {
        this.daysUntilDue = daysUntilDue;
    }

    /**
     *
     * @param overdueBy
     */
    public void setOverdueBy(final int overdueBy) {
        this.overdueBy = overdueBy;
    }

    /**
     *
     * @param isBlocked
     */
    public void setIsBlocked(final boolean isBlocked) {
        this.isBlocked = isBlocked;

    }

    /**
     *
     * @param ticketService
     * @return list of open tickets
     */
    public List<Integer> getOpenTickets(final TicketService ticketService) {
        List<Integer> openTickets = new ArrayList<>();
        for (Integer ticketId : tickets) {
            Ticket ticket = ticketService.getTicketById(ticketId);
            if (ticket.getStatus() != TicketStatus.CLOSED) {
                openTickets.add(ticketId);
            }
        }
        return openTickets;
    }

    /**
     *
     * @param ticketService
     * @return list of closed tickets
     */
    public List<Integer> getClosedTickets(final TicketService ticketService) {
        List<Integer> closedTickets = new ArrayList<>();
        for (Integer ticketId : tickets) {
            Ticket ticket = ticketService.getTicketById(ticketId);
            if (ticket.getStatus() == TicketStatus.CLOSED) {
                closedTickets.add(ticketId);
            }
        }
        return closedTickets;
    }

    /**
     *
     * @param ticketService
     * @return completion percentage
     */
    public double getCompletionPercentage(final TicketService ticketService) {
        int closedCount = getClosedTickets(ticketService).size();
        int total = tickets.size();
        return total == 0
                ? 0.0
                : Math.round((double) closedCount * HUNDRED / total) / SECOND;

    }

    /**
     *
     * @param completionPercentage
     */
    public void setCompletionPercentage(final double completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    /**
     *
     * @param observer
     */
    @Override
    public void addObserver(final Observer observer) {
        observers.add(observer);
    }

    /**
     *
     * @param observer
     */
    @Override
    public void removeObserver(final Observer observer) {
        observers.remove(observer);
    }

    /**
     *
     * @param message
     */
    @Override
    public void notifyObservers(final String message) {
        for (Observer observer : observers) {
            observer.receiveNotification(message);
        }
    }

    /**
     *
     * @param id
     */
    public void setLastClosedTicketId(final int id) {
        this.lastClosedTicketId = id;
    }

    /**
     *
     * @return last closed ticket id
     */
    public int getLastClosedTicketId() {
        return lastClosedTicketId;
    }

}
