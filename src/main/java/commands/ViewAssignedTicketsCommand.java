package commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import context.AppContext;
import database.UserDatabase;
import services.TicketService;
import tickets.Ticket;
import users.Developer;
import users.User;

import java.util.List;

public class ViewAssignedTicketsCommand extends  Command {
    private TicketService ticketService;
    private AppContext context;

    public ViewAssignedTicketsCommand(final TicketService ticketService,
                                      final AppContext context) {
        this.ticketService = ticketService;
        this.context = context;
    }

    /**
     *
     * @return output
     */
    @Override
    public ObjectNode execute() {
        ObjectMapper mapper = context.getMapper();
        JsonNode input = context.getInput();
        UserDatabase userDatabase = context.getUserDatabase();
        ObjectNode node = mapper.createObjectNode();
        node.put("command", "viewAssignedTickets");
        node.put("username", input.get("username").asText());
        node.put("timestamp", input.get("timestamp").asText());

        String username = input.get("username").asText();
        User user = userDatabase.getUser(username);
        if (!user.canViewAssignedTickets())  {
            node.put("message", "The user does not have permission to execute this command:"
                    + " required role DEVELOPER; user role " + user.getRole() + ".");
            return node;
        }

        Developer dev = (Developer) user;
        List<Ticket> tickets = dev.getAssignedTicketsSorted(ticketService);

        ArrayNode ticketsArray = mapper.createArrayNode();
        for (Ticket ticket : tickets) {
            ticketsArray.add(ticket.ticketToJsonForAssigned(mapper));
        }
        node.set("assignedTickets", ticketsArray);
        return node;
    }
}
