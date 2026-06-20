package commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import context.AppContext;
import services.CustomerImpactService;

import java.util.Map;

public class GenerateCustomerImpactReportCommand extends Command {
    private AppContext context;
    private CustomerImpactService customerImpactService;

    public GenerateCustomerImpactReportCommand(final AppContext context,
                                               final CustomerImpactService customerImpactService) {
        this.context = context;
        this.customerImpactService = customerImpactService;
    }

    @Override
    public ObjectNode  execute() {
        ObjectMapper mapper = context.getMapper();
        ObjectNode node = mapper.createObjectNode();
        JsonNode input = context.getInput();

        node.put("command", "generateCustomerImpactReport");
        node.put("username", input.get("username").asText());
        node.put("timestamp", input.get("timestamp").asText());

        Map<String, Object> report = customerImpactService.generateCustomerImpactReport(context);
        node.set("report", mapper.valueToTree(report));
        return node;
    }
}
