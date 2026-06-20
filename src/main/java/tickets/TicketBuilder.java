package tickets;

import java.time.LocalDate;

/**
 * Abstract builder used to construct {@link Ticket} objects.
 *
 * <p>
 * This builder follows the fluent interface pattern.
 * Subclasses may extend it to add additional ticket attributes.
 * </p>
 */
public abstract class TicketBuilder {
    protected int id;
    protected TicketType type;
    protected String title;
    protected LocalDate createdAt;
    protected TicketPriority businessPriority;
    protected TicketStatus status;
    protected Expertise expertiseArea;
    protected String description;
    protected String reportedBy;
    protected String expectedBehavior;
    protected String actualBehavior;
    protected BugFrequency frequency;
    protected BugSeverity severity;
    protected String environment;
    protected int errorCode;
    protected Impact businessValue;
    protected Demand customerDemand;
    protected String uiElementId;
    protected int usabilityScore;
    protected String screenshotUrl;

    /** Suggested fix description */
    protected String suggestedFix;

    /**
     * Sets the ticket identifier.
     *
     * @param idParam ticket id
     * @return this builder instance
     */
    public final TicketBuilder id(final int idParam) {
        this.id = idParam;
        return this;
    }

    /**
     * Sets the ticket description.
     *
     * @param descriptionParam description text
     * @return this builder instance
     */
    public final TicketBuilder description(final String descriptionParam) {
        this.description = descriptionParam;
        return this;
    }

    /**
     * Sets the ticket title.
     *
     * @param titleParam ticket title
     * @return this builder instance
     */
    public final TicketBuilder title(final String titleParam) {
        this.title = titleParam;
        return this;
    }

    /**
     * Sets the business priority.
     *
     * @param businessPriorityParam priority value
     * @return this builder instance
     */
    public final TicketBuilder businessPriority(final TicketPriority businessPriorityParam) {
        this.businessPriority = businessPriorityParam;
        return this;
    }

    /**
     * Sets the ticket status.
     *
     * @param statusParam ticket status
     * @return this builder instance
     */
    public final TicketBuilder status(final TicketStatus statusParam) {
        this.status = statusParam;
        return this;
    }

    /**
     * Sets the reporter username.
     *
     * @param reportedByParam reporter username
     * @return this builder instance
     */
    public final TicketBuilder reportedBy(final String reportedByParam) {
        this.reportedBy = reportedByParam;
        return this;
    }

    /**
     * Sets the required expertise area.
     *
     * @param expertiseAreaParam expertise area
     * @return this builder instance
     */
    public final TicketBuilder expertiseArea(final Expertise expertiseAreaParam) {
        this.expertiseArea = expertiseAreaParam;
        return this;
    }

    /**
     * Sets the creation date.
     *
     * @param createdAtParam creation date
     * @return this builder instance
     */
    public final TicketBuilder createdAt(final LocalDate createdAtParam) {
        this.createdAt = createdAtParam;
        return this;
    }

    // Accessor methods required for the Ticket constructor

    /** @return ticket id */
    public final int getId() {
        return id; }
    /** @return ticket type */
    public final TicketType getType() {
        return type; }
    /** @return ticket title */
    public final String getTitle() {
        return title; }
    /** @return creation date */
    public final LocalDate getCreatedAt() {
        return createdAt; }
    /** @return business priority */
    public final TicketPriority getBusinessPriority() {
        return businessPriority; }
    /** @return status */
    public final TicketStatus getStatus() {
        return status; }
    /** @return expertise area */
    public final Expertise getExpertiseArea() {
        return expertiseArea; }
    /** @return description */
    public final String getDescription() {
        return description; }
    /** @return reported by */
    public final String getReportedBy() {
        return reportedBy; }
    /** @return expected behavior */
    public final String getExpectedBehavior() {
        return expectedBehavior; }
    /** @return actual behavior */
    public final String getActualBehavior() {
        return actualBehavior; }
    /** @return frequency */
    public final BugFrequency getFrequency() {
        return frequency; }
    /** @return severity */
    public final BugSeverity getSeverity() {
        return severity; }
    /** @return environment */
    public final String getEnvironment() {
        return environment; }
    /** @return error code */
    public final int getErrorCode() {
        return errorCode; }
    /** @return business value */
    public final Impact getBusinessValue() {
        return businessValue; }
    /** @return customer demand */
    public final Demand getCustomerDemand() {
        return customerDemand; }
    /** @return UI element ID */
    public final String getUiElementId() {
        return uiElementId; }
    /** @return usability score */
    public final int getUsabilityScore() {
        return usabilityScore; }
    /** @return screenshot URL */
    public final String getScreenshotUrl() {
        return screenshotUrl; }
    /** @return suggested fix */
    public final String getSuggestedFix() {
        return suggestedFix; }

    /**
     * Builds and returns the ticket instance.
     *
     * @return constructed ticket
     */
    public Ticket build() {
        return new Ticket(this);
    }
}
