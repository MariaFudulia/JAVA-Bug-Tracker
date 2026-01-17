package database;

import users.User;

import java.util.HashMap;
import java.util.Map;

public class UserDatabase {
    private final Map<String, User> users = new HashMap<>();

    public UserDatabase() {}

    public void add(User user) {
        users.put(user.getUsername(), user);
    }

    public boolean userExists(String username) {
        return users.containsKey(username);
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public User getUser(String username) {
        return users.get(username);
    }
}
