package tickets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import users.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Ticket {
    int id;
    TicketType type;
    String title;
    LocalDate createdAt;
    TicketPriority businessPriority;
    TicketStatus status;
    Expertise expertiseArea;

    LocalDate assignedAt;
    LocalDate solvedAt;
    String assignedTo;

    String description;
    String reportedBy;
    String expectedBehavior;
    String actualBehavior;
    BugFrequency frequency;
    BugSeverity severity;
    String environment;
    int errorCode;
    Impact businessValue;
    Demand customerDemand;
    String uiElementId;
    int usabilityScore;
    String screenshotUrl;
    String suggestedFix;

    private boolean assignedToAMilestone = false;
    private String belongsToMilestone = null;

    private List<String> comments = new ArrayList<>();

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

    public TicketPriority getBusinessPriority() {
        return businessPriority;
    }

    public TicketType getType() {
        return type;
    }
    public String getTitle() {
        return title;
    }

    public Expertise getExpertiseArea() {
        return expertiseArea;
    }

    public int getId() {
        return id;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public ObjectNode ticketToJson(ObjectMapper mapper) {
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

        node.set("comments", mapper.valueToTree(comments));

        return node;
    }

    public void setBusinessPriority(TicketPriority businessPriority) {
        this.businessPriority = businessPriority;
    }

    public void setIsAssignedToAMilestone(boolean assignedToAMilestone) {
        this.assignedToAMilestone = assignedToAMilestone;
    }

    public boolean isAssignedToAMilestone() {
        return assignedToAMilestone;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setBelongsToMilestone(String belongsToMilestone) {
        this.belongsToMilestone = belongsToMilestone;
    }
    public String getBelongsToMilestone() {
        return belongsToMilestone;
    }

    public void updatePriority() {
        switch (businessPriority) {
            case LOW -> businessPriority = TicketPriority.MEDIUM;
            case MEDIUM -> businessPriority = TicketPriority.HIGH;
            case HIGH -> businessPriority = TicketPriority.CRITICAL;
            case CRITICAL -> businessPriority = TicketPriority.CRITICAL;
        }
    }

    public void increasePriorityBy(int steps) {
        for (int i = 1; i <= steps; i++) {
            updatePriority();
        }
    }

}
