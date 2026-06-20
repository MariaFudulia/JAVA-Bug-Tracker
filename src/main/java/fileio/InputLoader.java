package fileio;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class InputLoader {
    private final List<JsonNode> commands;

    public InputLoader(final String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File(filePath));

        this.commands = new ArrayList<>();
        if (root.isArray()) {
            for (JsonNode node : root) {
                this.commands.add(node);
            }
        }
    }

    public List<JsonNode> getCommands() {
        return commands;
    }
}
