package database;

import users.User;

import java.util.HashMap;
import java.util.Map;

public class UserDatabase {
    private final Map<String, User> users = new HashMap<>();

    public UserDatabase() { }

    /**
     *
     * @param user
     */
    public void add(final User user) {
        users.put(user.getUsername(), user);
    }

    /**
     *
     * @param username
     * @return true if user exists
     */
    public boolean userExists(final String username) {
        return users.containsKey(username);
    }

    /**
     *
     * @return users map
     */
    public Map<String, User> getUsers() {
        return users;
    }

    /**
     *
     * @param username
     * @return user with that username
     */
    public User getUser(final String username) {
        return users.get(username);
    }
}
