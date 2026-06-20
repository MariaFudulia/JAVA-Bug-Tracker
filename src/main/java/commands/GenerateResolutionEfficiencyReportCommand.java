package commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import context.AppContext;
import services.ResolutionEfficiencyService;

import java.util.Map;

public class GenerateResolutionEfficiencyReportCommand extends Command {
    private AppContext context;
    private ResolutionEfficiencyService resolutionEfficiencyService;

    public GenerateResolutionEfficiencyReportCommand(
            final AppContext context, final ResolutionEfficiencyService efficiencyService) {
        this.context = context;
        this.resolutionEfficiencyService = efficiencyService;
    }

    @Override
    public ObjectNode execute() {
        ObjectMapper mapper = context.getMapper();
        ObjectNode node = mapper.createObjectNode();
        JsonNode input = context.getInput();

        node.put("command", "generateResolutionEfficiencyReport");
        node.put("username", input.get("username").asText());
        node.put("timestamp", input.get("timestamp").asText());

        Map<String, Object> report = resolutionEfficiencyService
                .generateResolutionEfficiencyReport(context);
        node.set("report", mapper.valueToTree(report));
        return node;
    }
}
