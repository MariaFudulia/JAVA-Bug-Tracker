package database;

import tickets.Ticket;
import users.User;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class TicketDatabase {
    int totalTickets = 0;
    private Map<Integer, Ticket> tickets = new LinkedHashMap<>();

    public void incTickets() {
        totalTickets++;
    }

    public void decTickets() {
        totalTickets--;
    }

    public void addTicket(final Ticket ticket) {
        tickets.put(ticket.getId(), ticket);
    }

    public int getTotalTickets() {
        return totalTickets;
    }

    public Map<Integer, Ticket> getTickets() {
        return tickets;
    }

    public Ticket getTicket(int ticketId) {
        return tickets.get(ticketId);
    }
}
