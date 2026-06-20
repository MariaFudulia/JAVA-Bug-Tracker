package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import context.AppContext;
import database.TicketDatabase;

import tickets.Ticket;
import tickets.TicketBuilder;
import tickets.TicketBuilderFactory;
import tickets.TicketType;
import tickets.TicketPriority;
import tickets.Expertise;
import tickets.TicketStatus;
import tickets.BugTicketBuilder;
import tickets.BugFrequency;
import tickets.BugSeverity;
import tickets.FeatureRequestTicketBuilder;
import tickets.Impact;
import tickets.Demand;
import tickets.UiFeedbackTicketBuilder;
import users.User;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public final class TicketService {
    private final TicketDatabase ticketDatabase;

    public TicketService(final TicketDatabase ticketDatabase) {
        this.ticketDatabase = ticketDatabase;
    }

    /**
     *
     * @param appContext
     * @return ticket
     */
    public Ticket createTicket(final AppContext appContext) {
        ObjectMapper mapper = appContext.getMapper();
        ObjectNode node = mapper.createObjectNode();
        JsonNode input = appContext.getInput();
        JsonNode params =  input.get("params");
        TicketType type = TicketType.valueOf(params.get("type").asText());
        String reporter = params.get("reportedBy").asText();

        if (reporter.isEmpty() && type != TicketType.BUG) {
            return null;
        }

        TicketBuilder builder = TicketBuilderFactory.create(type);

        String timestamp = input.get("timestamp").asText();
        LocalDate createdAt = LocalDate.parse(timestamp);

        builder.id(ticketDatabase.getTotalTickets())
                .title(params.get("title").asText())
                .businessPriority(TicketPriority.valueOf(params
                        .get("businessPriority").asText()))
                .expertiseArea(Expertise.valueOf(params
                        .get("expertiseArea").asText()))
                .reportedBy(reporter).status(TicketStatus.OPEN)
                .createdAt(createdAt);

        if (params.has("description")) {
            builder.description(params.get("description").asText());
        }

        if (params.get("reportedBy").asText().equals("")) {
            builder.businessPriority(TicketPriority.LOW);
        }

        switch (type) {
            case BUG -> {
                ((BugTicketBuilder) builder).frequency(BugFrequency
                                .valueOf(params.get("frequency").asText()))
                        .severity(BugSeverity.valueOf(params.get("severity").asText()));
            }

            case FEATURE_REQUEST -> {
                ((FeatureRequestTicketBuilder) builder).businessValue(Impact.valueOf(params
                        .get("businessValue").asText())).customerDemand(Demand.valueOf(params
                        .get("customerDemand").asText()));
            }

            case UI_FEEDBACK -> {
                ((UiFeedbackTicketBuilder) builder).businessValue(Impact.valueOf(params
                                .get("businessValue").asText()))
                        .usabilityScore(params.get("usabilityScore").asInt());
            }
        }

        Ticket ticket = builder.build();
        ticketDatabase.addTicket(ticket);
        ticketDatabase.incTickets();
        return ticket;
    }

    /**
     *
     * @param user
     * @return visible tickets to user
     */
    public List<Ticket> getVisibleTicketsForUser(final User user,
                                                 final AppContext appContext) {
        Map<Integer, Ticket> tickets = ticketDatabase.getTickets();
        List<Ticket> ticketList = user.getVisibleTickets(tickets,
                appContext.getMilestoneDatabase(), this);

        return sort(ticketList);
    }

    /**
     *
     * @param ticketList
     * @return ticketList
     */
    private List<Ticket> sort(final List<Ticket> ticketList) {
        return ticketList.stream().sorted(
                Comparator.comparing(Ticket::getCreatedAt).thenComparing(Ticket::getId)).toList();
    }

    /**
     *
     * @param ticketId
     * @return ticket with that id
     */
    public Ticket getTicketById(final int ticketId) {
        return ticketDatabase.getTicket(ticketId);
    }
}
