package tickets;

import java.time.LocalDate;

public abstract class TicketBuilder {
    int id;
    TicketType type;
    String title;
    LocalDate createdAt;
    TicketPriority businessPriority;
    TicketStatus status;
    Expertise expertiseArea;

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

    public TicketBuilder id(final int id) {
        this.id = id;
        return this;
    }

    public TicketBuilder description(final String description) {
        this.description = description;
        return this;
    }

    public TicketBuilder title(final String title) {
        this.title = title;
        return this;
    }

    public TicketBuilder businessPriority(final TicketPriority businessPriority) {
        this.businessPriority = businessPriority;
        return this;
    }

    public TicketBuilder status(final TicketStatus status) {
        this.status = status;
        return this;
    }

    public TicketBuilder reportedBy(final String reportedBy) {
        this.reportedBy = reportedBy;
        return this;
    }

    public TicketBuilder expertiseArea(final Expertise expertiseArea) {
        this.expertiseArea = expertiseArea;
        return this;
    }

    public TicketBuilder createdAt(final LocalDate createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Ticket build() {
        return new Ticket(this);
    }
}
