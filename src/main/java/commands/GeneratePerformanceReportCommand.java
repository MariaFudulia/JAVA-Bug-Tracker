package commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import context.AppContext;
import services.PerformanceService;
import services.TicketService;

import java.util.List;
import java.util.Map;

public class GeneratePerformanceReportCommand extends Command {
    private AppContext context;
    private PerformanceService performanceService;
    private TicketService ticketService;

    public GeneratePerformanceReportCommand(final PerformanceService performanceService,
                                            final AppContext context,
                                            final TicketService ticketService) {
        this.performanceService = performanceService;
        this.context = context;
        this.ticketService = ticketService;
    }

    @Override
    public ObjectNode execute() {
        ObjectMapper mapper = context.getMapper();
        JsonNode input = context.getInput();
        ObjectNode node = mapper.createObjectNode();

        node.put("command", "generatePerformanceReport");
        node.put("username", input.get("username").asText());
        node.put("timestamp", input.get("timestamp").asText());

        List<Map<String, Object>> report = performanceService.generatePerformanceReport(context, context.getTicketDatabase());
        node.set("report", mapper.convertValue(report, JsonNode.class));
        return node;
    }
}
