package fileio;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import database.UserDatabase;
import users.User;
import users.UserFactory;

import java.io.File;
import java.io.IOException;

public class UserLoader {
    private UserDatabase userDatabase;

    public UserLoader(final String filePath) throws IOException {
        userDatabase = new UserDatabase();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File(filePath));

        for (JsonNode node : root) {
            User user = UserFactory.createUser(node, mapper);
            userDatabase.add(user);
        }
    }

    /**
     *
     * @return user db
     */
    public UserDatabase getUserDatabase() {
        return userDatabase;
    }

}
