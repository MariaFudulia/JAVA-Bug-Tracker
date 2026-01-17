package commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import context.AppContext;
import database.UserDatabase;
import services.TicketService;
import tickets.*;
import workflow.DevelopmentPhase;
import workflow.WorkflowPhase;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ReportTicketCommand extends Command {
    private TicketService ticketService;
    private AppContext context;

    public ReportTicketCommand(TicketService ticketService, AppContext context) {
        this.ticketService = ticketService;
        this.context = context;
    }

    @Override
    public ObjectNode execute() {
        ObjectMapper mapper = context.getMapper();
        ObjectNode node = mapper.createObjectNode();
        JsonNode input = context.getInput();

        WorkflowPhase workflowPhase = context.getWorkflowPhase();
        String timestamp = input.get("timestamp").asText();
        LocalDate now = LocalDate.parse(timestamp);
        LocalDate startTestingDate = context.getStartedTesting();

        if ((int) ChronoUnit.DAYS.between(startTestingDate, now) >= 12) {
            workflowPhase.setCurrentPhase(new DevelopmentPhase());
        }

        if (!workflowPhase.getCurrentPhase().canReportTicket()) {
            node.put("command", "reportTicket");
            node.put("username", input.get("username").asText());
            node.put("timestamp", input.get("timestamp").asText());
            node.put("error", "Tickets can only be reported during " +
                    "testing phases.");
            return node;
        }

        UserDatabase userDatabase = context.getUserDatabase();
        if (!userDatabase.userExists(input.get("username").asText())) {
            node.put("command", "reportTicket");
            node.put("username", input.get("username").asText());
            node.put("timestamp", input.get("timestamp").asText());
            node.put("error", "The user " + input.get("username").asText() + " does not exist.");
            return node;
        }


        Ticket ticket = ticketService.createTicket(context);

        if (ticket == null) {
            node.put("command", "reportTicket");
            node.put("username", input.get("username").asText());
            node.put("timestamp", input.get("timestamp").asText());
            node.put("error", "Anonymous reports are only allowed for " +
                    "tickets of type BUG.");
            return node;
        }

        return null;
    }
}
