package notifications;

import users.Developer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service responsible for managing and distributing notifications to developers.
 * This class follows the Observer pattern principles for message delivery.
 */
public final class Notification {
    private Map<String, List<String>> notifications = new HashMap<>();

    /**
     * Default constructor for Notification service.
     */
    public Notification() { }

    /**
     * Registers a developer into the notification system.
     *
     * @param developer the developer to be registered
     */
    public void registerDeveloper(final Developer developer) {
        notifications.putIfAbsent(developer.getUsername(), new ArrayList<>());
    }

    /**
     * Adds a new notification message for a specific developer.
     *
     * @param username the username of the developer to notify
     * @param message  the notification message content
     */
    public void notifyDeveloper(final String username, final String message) {
        notifications.computeIfAbsent(username, k -> new ArrayList<>()).add(message);
    }

    /**
     * Retrieves the list of notifications for a specific developer.
     *
     * @param username the username of the developer
     * @return a list of notification strings
     */
    public List<String> viewNotifications(final String username) {
        List<String> notifs = notifications.getOrDefault(username, new ArrayList<>());
        notifications.putIfAbsent(username, notifs);
        return notifs;
    }
}