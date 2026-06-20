package users;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class UserFactory {
    private UserFactory() { }

    /**
     *
     * @param node
     * @param mapper
     * @return
     * @throws JsonProcessingException
     */
    public static User createUser(final JsonNode node, final ObjectMapper mapper)
            throws JsonProcessingException {
        String username = node.get("username").asText();
        String email = node.get("email").asText();
        UserRole role = UserRole.valueOf(node.get("role").asText());

        return switch (role) {
            case REPORTER -> new Reporter(username, email);
            case DEVELOPER ->  new Developer(username, email,
                    LocalDate.parse(node.get("hireDate").asText()),
                    UserSeniority.valueOf(node.get("seniority").asText()),
                    UserExpertise.valueOf(node.get("expertiseArea").asText()));
            case MANAGER ->  new Manager(username, email,
                    LocalDate.parse(node.get("hireDate").asText()),
                    parseSubordinates(node.get("subordinates")));
        };
    }

    private static List<String> parseSubordinates(final JsonNode subordinatesNode) {
        List<String> subordinates = new ArrayList<>();

        if (subordinatesNode != null && subordinatesNode.isArray()) {
            for (JsonNode sub : subordinatesNode) {
                subordinates.add(sub.asText());
            }
        }

        return subordinates;
    }
}
