package commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import context.AppContext;
import services.TicketService;
import tickets.Ticket;
import users.User;

import java.util.List;

public class ViewTicketsCommand extends Command {
    private final TicketService ticketService;
    private AppContext context;

    public ViewTicketsCommand(final TicketService ticketService,
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
        ObjectNode node = mapper.createObjectNode();
        JsonNode input = context.getInput();

        User user = context.getUserDatabase().getUser(input.get("username").asText());
        List<Ticket> tickets = ticketService.getVisibleTicketsForUser(user, context);

        node.put("command", "viewTickets");
        node.put("username", input.get("username").asText());
        node.put("timestamp", input.get("timestamp").asText());

        ArrayNode ticketsNode = mapper.createArrayNode();

        for (Ticket ticket : tickets) {
            ticketsNode.add(ticket.ticketToJson(mapper));
        }

        node.set("tickets", ticketsNode);
        return node;
    }
}
