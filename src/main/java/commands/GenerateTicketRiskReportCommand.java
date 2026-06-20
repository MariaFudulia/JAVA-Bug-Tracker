package commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import context.AppContext;
import services.TicketRiskService;

import java.util.Map;

public class GenerateTicketRiskReportCommand extends Command {
    private AppContext context;
    private TicketRiskService ticketRiskService;

    public GenerateTicketRiskReportCommand(final AppContext context,
                                           final TicketRiskService ticketRiskService) {
        this.context = context;
        this.ticketRiskService = ticketRiskService;
    }

    @Override
    public ObjectNode execute() {
        ObjectMapper mapper = context.getMapper();
        ObjectNode node = mapper.createObjectNode();
        JsonNode input = context.getInput();

        node.put("command", "generateTicketRiskReport");
        node.put("username", input.get("username").asText());
        node.put("timestamp", input.get("timestamp").asText());

        Map<String, Object> report = ticketRiskService.generateTicketRiskReport(context);
        node.set("report", mapper.valueToTree(report));
        return node;
    }
}
