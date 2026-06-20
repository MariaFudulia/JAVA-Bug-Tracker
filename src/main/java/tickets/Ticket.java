package tickets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import comments.Comment;
import database.MilestoneDatabase;
import milestones.Milestone;
import users.Developer;
import users.User;
import users.UserRole;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Represents a Ticket in the system.
 * This class is designed to be a base for specific ticket types.
 */
public class Ticket {
    private int id;
    private TicketType type;
    private String title;
    private LocalDate createdAt;
    private TicketPriority businessPriority;
    private TicketStatus status;
    private Expertise expertiseArea;

    private LocalDate assignedAt;
    private LocalDate solvedAt;
    private String assignedTo;

    private String description;
    private String reportedBy;
    private String expectedBehavior;
    private String actualBehavior;
    private BugFrequency frequency;
    private BugSeverity severity;
    private String environment;
    private int errorCode;
    private Impact businessValue;
    private Demand customerDemand;
    private String uiElementId;
    private int usabilityScore;
    private String screenshotUrl;
    private String suggestedFix;

    private boolean assignedToAMilestone = false;
    private String belongsToMilestone = null;

    private List<Comment> comments = new ArrayList<>();
    private List<TicketAction> actions = new ArrayList<>();
    private TicketStatus previousStatus;

    Ticket(final TicketBuilder builder) {
        this.id = builder.id;
        this.type = builder.type;
        this.title = builder.title;
        this.businessPriority = builder.businessPriority;
        this.status = builder.status;
        this.expertiseArea = builder.expertiseArea;
        this.description = builder.description;
        this.reportedBy = builder.reportedBy;
        this.expectedBehavior = builder.expectedBehavior;
        this.actualBehavior = builder.actualBehavior;
        this.frequency = builder.frequency;
        this.severity = builder.severity;
        this.environment = builder.environment;
        this.errorCode = builder.errorCode;
        this.businessValue = builder.businessValue;
        this.customerDemand = builder.customerDemand;
        this.uiElementId = builder.uiElementId;
        this.usabilityScore = builder.usabilityScore;
        this.screenshotUrl = builder.screenshotUrl;
        this.suggestedFix = builder.suggestedFix;
        this.createdAt = builder.createdAt;
    }

    /** @return current business priority */
    public final TicketPriority getBusinessPriority() {
        return businessPriority;
    }

    public final BugFrequency getBugFrequency() {
        return frequency;
    }

    public final BugSeverity getSeverity() {
        return severity;
    }

    public final Impact getBusinessValue() {
        return businessValue;
    }

    public final int getUsabilityScore() {
        return usabilityScore;
    }

    /**
     *
     * @param solvedAt
     */
    public void setSolvedAt(final LocalDate solvedAt) {
        this.solvedAt = solvedAt;
    }

    /**
     * Updates status to the next logical step.
     */
    public final void nextStatus(final LocalDate now) {
        switch (this.status) {
            case OPEN -> {
                previousStatus = TicketStatus.OPEN;
                status = TicketStatus.IN_PROGRESS;
            }
            case IN_PROGRESS -> {
                previousStatus = TicketStatus.IN_PROGRESS;
                status = TicketStatus.RESOLVED;
                if (solvedAt == null) {
                    setSolvedAt(now);
                }
            }
            case RESOLVED -> {
                previousStatus = TicketStatus.RESOLVED;
                status = TicketStatus.CLOSED;
            }
            default -> { }
        }
    }

    /**
     * Reverts to previous status.
     */
    public final void undoStatus() {
        if (previousStatus != null) {
            TicketStatus tmpStatus = status;
            status = previousStatus;
            previousStatus = tmpStatus;
        }
    }

    /** @return customer demand */
    public final Demand getCustomerDemand() {
        return customerDemand;
    }

    /** @return ticket type */
    public final TicketType getType() {
        return type;
    }

    /** @return ticket title */
    public final String getTitle() {
        return title;
    }

    /** @return expertise area */
    public final Expertise getExpertiseArea() {
        return expertiseArea;
    }

    /** @return ticket ID */
    public final int getId() {
        return id;
    }

    /** @return current status */
    public final TicketStatus getStatus() {
        return status;
    }

    /** @return author of report */
    public final String getReportedBy() {
        return reportedBy;
    }

    /** @return creation date */
    public final LocalDate getCreatedAt() {
        return createdAt;
    }

    /** @param status the status to set */
    public final void setStatus(final TicketStatus status) {
        this.status = status;
    }

    /**
     * @param mapper Jackson mapper
     * @return ObjectNode for JSON output
     */
    public final ObjectNode ticketToJson(final ObjectMapper mapper) {
        ObjectNode node = mapper.createObjectNode();
        node.put("id", id);
        node.put("type", type.name());
        node.put("title", title);
        node.put("businessPriority", businessPriority.name());
        node.put("status", status.name());
        node.put("createdAt", createdAt.toString());
        node.put("solvedAt", solvedAt != null ? solvedAt.toString() : "");
        node.put("assignedAt", assignedAt != null ? assignedAt.toString() : "");
        node.put("assignedTo", assignedTo != null ? assignedTo : "");
        node.put("reportedBy", reportedBy);
        ArrayNode commentsNode = mapper.createArrayNode();
        for (Comment comment : comments) {
            commentsNode.add(comment.toJson(mapper));
        }
        node.set("comments", commentsNode);
        return node;
    }

    /**
     * @param mapper Jackson mapper
     * @return output node for assigned tickets
     */
    public final ObjectNode ticketToJsonForAssigned(final ObjectMapper mapper) {
        ObjectNode node = mapper.createObjectNode();
        node.put("id", id);
        node.put("type", type.name());
        node.put("title", title);
        node.put("businessPriority", businessPriority.name());
        node.put("status", status.name());
        node.put("createdAt", createdAt.toString());
        node.put("assignedAt", assignedAt != null ? assignedAt.toString() : "");
        node.put("reportedBy", reportedBy);
        ArrayNode commentsNode = mapper.createArrayNode();
        for (Comment comment : comments) {
            commentsNode.add(comment.toJson(mapper));
        }
        node.set("comments", commentsNode);
        return node;
    }

    /**
     * Converts ticket history to JSON.
     * @param mapper Jackson mapper
     * @param user user requesting history
     * @return JSON node
     */
    public final ObjectNode ticketToJsonForHistory(final ObjectMapper mapper, final User user) {
        ObjectNode node = mapper.createObjectNode();
        node.put("id", id);
        node.put("title", title);
        node.put("status", status.name());

        ArrayNode actionsNode = mapper.createArrayNode();
        for (TicketAction action : filterActions(user)) {
            actionsNode.add(action.toJson());
        }
        node.set("actions", actionsNode);

        ArrayNode commentsNode = mapper.createArrayNode();
        for (Comment comment : comments) {
            commentsNode.add(comment.toJson(mapper));
        }
        node.set("comments", commentsNode);
        return node;
    }

    /**
     * Filters actions based on user seniority.
     * @param user user to filter for
     * @return filtered list
     */
    public final List<TicketAction> filterActions(final User user) {
        if (!user.getRole().equals(UserRole.DEVELOPER)) {
            return actions;
        }

        Developer dev = (Developer) user;
        Optional<TicketAction> deAssign = actions.stream()
                .filter(a -> a.getAction() == TicketActionType.DEASSIGNED
                        && dev.getUsername().equals(a.getBy())).findFirst();

        if (deAssign.isEmpty()) {
            return actions;
        }

        LocalDate past = deAssign.get().getTimestamp();
        return actions.stream().filter(a -> !a.getTimestamp().isAfter(past))
                .collect(Collectors.toList());
    }

    /** @param comment comment to add */
    public final void addComment(final Comment comment) {
        comments.add(comment);
    }

    /** @param username author of comment */
    public final void removeLastCommentAddedBy(final String username) {
        for (int i = comments.size() - 1; i >= 0; i--) {
            if (comments.get(i).getAuthor().equals(username)) {
                comments.remove(i);
                return;
            }
        }
    }

    /** @return list of comments */
    public final List<Comment> getComments() {
        return comments;
    }

    /** @param businessPriority new priority */
    public final void setBusinessPriority(final TicketPriority businessPriority) {
        this.businessPriority = businessPriority;
    }

    /** @param assignedToMilestone flag */
    public final void setIsAssignedToAMilestone(final boolean assignedToMilestone) {
        this.assignedToAMilestone = assignedToMilestone;
    }

    /** @return assignment status */
    public final boolean isAssignedToAMilestone() {
        return assignedToAMilestone;
    }

    /** @param assignedTo developer username */
    public final void setAssignedTo(final String assignedTo) {
        this.assignedTo = assignedTo;
    }

    /** @return assigned developer */
    public final String getAssignedTo() {
        return assignedTo;
    }

    /** @return assignment date */
    public final LocalDate getAssignedAt() {
        return assignedAt;
    }

    /** @param assignedAt assignment date */
    public final void setAssignedAt(final LocalDate assignedAt) {
        this.assignedAt = assignedAt;
    }

    /** @param belongsToMilestone milestone name */
    public final void setBelongsToMilestone(final String belongsToMilestone) {
        this.belongsToMilestone = belongsToMilestone;
    }

    /** @return milestone name */
    public final String getBelongsToMilestone() {
        return belongsToMilestone;
    }

    /**
     * Increases priority by one level.
     */
    public final void updatePriority() {
        switch (businessPriority) {
            case LOW -> businessPriority = TicketPriority.MEDIUM;
            case MEDIUM -> businessPriority = TicketPriority.HIGH;
            case HIGH -> businessPriority = TicketPriority.CRITICAL;
            case CRITICAL -> businessPriority = TicketPriority.CRITICAL;
            default -> { }
        }
    }

    /**
     * Increases priority by multiple levels.
     * @param steps number of levels
     */
    public final void increasePriorityBy(final int steps) {
        for (int i = 1; i <= steps; i++) {
            updatePriority();
        }
    }

    /**
     * Finds the milestone this ticket belongs to.
     * @param db milestone database
     * @return Milestone object or null
     */
    public final Milestone getMilestoneTicketIsAssignedTo(final MilestoneDatabase db) {
        Map<String, Milestone> milestones = db.getMilestones();
        for (Milestone m : milestones.values()) {
            if (m.getTickets().contains(id)) {
                return m;
            }
        }
        return null;
    }

    /** @return list of actions */
    public final List<TicketAction> getActions() {
        return actions;
    }

    /** @return previous status */
    public final TicketStatus getPreviousStatus() {
        return previousStatus;
    }

    /** @return ticket description */
    public final String getDescription() {
        return description;
    }

    /** @return solved date */
    public final LocalDate getSolvedAt() {
        return solvedAt;
    }

    /**
     * Gets assignment date for a specific developer.
     * @param developerUsername developer name
     * @return assignment date
     */
    public final LocalDate getAssignedAtToDev(final String developerUsername) {
        return actions.stream()
                .filter(a -> a.getAction().equals(TicketActionType.ASSIGNED)
                        && a.getBy().equals(developerUsername))
                .map(TicketAction::getTimestamp)
                .findFirst()
                .orElse(null);
    }
}
