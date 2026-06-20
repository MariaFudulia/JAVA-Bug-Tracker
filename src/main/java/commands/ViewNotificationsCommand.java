package commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import context.AppContext;
import users.Developer;
import users.User;
import users.UserRole;

public class ViewNotificationsCommand extends Command {
    private AppContext context;

    public ViewNotificationsCommand(final AppContext context) {
        this.context = context;
    }

    @Override
    public ObjectNode  execute() {
        ObjectMapper mapper = context.getMapper();
        JsonNode input = context.getInput();
        ObjectNode node = mapper.createObjectNode();
        node.put("command", "viewNotifications");
        node.put("username", input.get("username").asText());
        node.put("timestamp", input.get("timestamp").asText());

        User user = context.getUserDatabase().getUser(input.get("username").asText());

        if (!user.getRole().equals(UserRole.DEVELOPER)) {
            node.put("error", "The user does not have permission to execute this command:"
                    + " required role DEVELOPER; user role " + user.getRole() + ".");
            return node;
        }

        Developer dev = (Developer) user;
        ArrayNode notifs = mapper.createArrayNode();
        for (String msg : dev.consumeNotifications()) {
            notifs.add(msg);
        }

        node.set("notifications", notifs);
        return node;
    }
}
