package commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import context.AppContext;
import services.AppStabilityService;

import java.util.Map;

public class GenerateAppStabilityReportCommand extends Command {

    private final AppContext context;
    private final AppStabilityService appStabilityService;

    public GenerateAppStabilityReportCommand(
            final AppContext context,
            final AppStabilityService appStabilityService) {
        this.context = context;
        this.appStabilityService = appStabilityService;
    }

    @Override
    public ObjectNode execute() {
        ObjectMapper mapper = context.getMapper();
        ObjectNode node = mapper.createObjectNode();
        JsonNode input = context.getInput();

        node.put("command", "appStabilityReport");
        node.put("username", input.get("username").asText());
        node.put("timestamp", input.get("timestamp").asText());

        Map<String, Object> report =
                appStabilityService.generateAppStabilityReport(context);

        node.set("report", mapper.valueToTree(report));

        return node;
    }
}
