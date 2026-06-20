package tickets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.LocalDate;

/**
 * Represents an action performed on a ticket.
 *
 * <p>
 * Instances of this class are immutable and are created using
 * static factory methods corresponding to specific ticket actions.
 * </p>
 */
public final class TicketAction {

    private TicketActionType action;
    private String by;
    private LocalDate timestamp;

    private TicketStatus to;
    private TicketStatus from;
    private String removedFromDev;
    private String milestone;

    /**
     * Private constructor used by factory methods.
     *
     * @param action action type
     * @param by user who performed the action
     * @param timestamp action date
     */
    private TicketAction(final TicketActionType action,
                         final String by,
                         final LocalDate timestamp) {
        this.action = action;
        this.by = by;
        this.timestamp = timestamp;
    }

    /**
     * Creates an ASSIGNED action.
     *
     * @param developer assigned developer
     * @param date assignment date
     * @return ticket action instance
     */
    public static TicketAction assigned(final String developer,
                                        final LocalDate date) {
        return new TicketAction(TicketActionType.ASSIGNED, developer, date);
    }

    /**
     * Creates a DEASSIGNED action.
     *
     * @param developer removed developer
     * @param date action date
     * @return ticket action instance
     */
    public static TicketAction deAssigned(final String developer,
                                          final LocalDate date) {
        return new TicketAction(TicketActionType.DEASSIGNED, developer, date);
    }

    /**
     * Creates a STATUS_CHANGED action.
     *
     * @param from previous status
     * @param to new status
     * @param by user who performed the change
     * @param date action date
     * @return ticket action instance
     */
    public static TicketAction statusChanged(final TicketStatus from,
                                             final TicketStatus to,
                                             final String by,
                                             final LocalDate date) {
        TicketAction ticketAction =
                new TicketAction(TicketActionType.STATUS_CHANGED, by, date);
        ticketAction.from = from;
        ticketAction.to = to;
        return ticketAction;
    }

    /**
     * Creates an ADDED_TO_MILESTONE action.
     *
     * @param milestone milestone name
     * @param manager manager who added the ticket
     * @param date action date
     * @return ticket action instance
     */
    public static TicketAction addedToMilestone(final String milestone,
                                                final String manager,
                                                final LocalDate date) {
        TicketAction ticketAction =
                new TicketAction(TicketActionType.ADDED_TO_MILESTONE, manager, date);
        ticketAction.milestone = milestone;
        return ticketAction;
    }

    /**
     * Creates a REMOVED_FROM_DEV action.
     *
     * @param developer removed developer
     * @param date action date
     * @return ticket action instance
     */
    public static TicketAction removedFromDev(final String developer,
                                              final LocalDate date) {
        TicketAction ticketAction =
                new TicketAction(TicketActionType.REMOVED_FROM_DEV, "system", date);
        ticketAction.removedFromDev = developer;
        return ticketAction;
    }

    /** @return action type */
    public TicketActionType getAction() {
        return action;
    }

    /** @return user who performed the action */
    public String getBy() {
        return by;
    }

    /** @return action timestamp */
    public LocalDate getTimestamp() {
        return timestamp;
    }

    /** @return new status */
    public TicketStatus getTo() {
        return to;
    }

    /** @return previous status */
    public TicketStatus getFrom() {
        return from;
    }

    /** @return removed developer */
    public String getRemovedFromDev() {
        return removedFromDev;
    }

    /** @return milestone name */
    public String getMilestone() {
        return milestone;
    }

    /**
     * Converts this action to a JSON representation.
     *
     * @return JSON object describing the action
     */
    public ObjectNode toJson() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        switch (action) {
            case ASSIGNED:
            case DEASSIGNED:
                node.put("by", by);
                node.put("timestamp", timestamp.toString());
                break;
            case STATUS_CHANGED:
                node.put("from", from.toString());
                node.put("to", to.toString());
                node.put("by", by);
                node.put("timestamp", timestamp.toString());
                break;
            case ADDED_TO_MILESTONE:
                node.put("milestone", milestone);
                node.put("by", by);
                node.put("timestamp", timestamp.toString());
                break;
            default:
                break;
        }

        if (action == TicketActionType.DEASSIGNED) {
            node.put("action", "DE-ASSIGNED");
        } else {
            node.put("action", action.toString());
        }

        return node;
    }
}
