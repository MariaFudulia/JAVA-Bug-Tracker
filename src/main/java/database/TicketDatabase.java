package database;

import tickets.Ticket;
import java.util.LinkedHashMap;
import java.util.Map;

public class TicketDatabase {
    private int totalTickets = 0;
    private Map<Integer, Ticket> tickets = new LinkedHashMap<>();

    /**
     * increase number of tickets
     */
    public void incTickets() {
        totalTickets++;
    }

    /**
     * decrease number of tickets
     */
    public void decTickets() {
        totalTickets--;
    }

    /**
     *
     * @param ticket
     */
    public void addTicket(final Ticket ticket) {
        tickets.put(ticket.getId(), ticket);
    }

    /**
     *
     * @return total number of tickets
     */
    public int getTotalTickets() {
        return totalTickets;
    }

    /**
     *
     * @return tickets
     */
    public Map<Integer, Ticket> getTickets() {
        return tickets;
    }

    /**
     *
     * @param ticketId
     * @return ticket with that id
     */
    public Ticket getTicket(final int ticketId) {
        return tickets.get(ticketId);
    }
}
