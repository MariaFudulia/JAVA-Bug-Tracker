package tickets;

/**
 * Builder class for creating {@link Ticket} objects of type BUG.
 */
public class BugTicketBuilder extends TicketBuilder {

    /**
     * Constructs a new BugTicketBuilder and sets the default ticket type.
     */
    public BugTicketBuilder() {
        this.type = TicketType.BUG;
    }

    /**
     * Sets the expected behavior of the bug.
     *
     * @param expectedBehaviorParam the expected behavior
     * @return this builder instance
     */
    public final BugTicketBuilder expectedBehavior(final String expectedBehaviorParam) {
        this.expectedBehavior = expectedBehaviorParam;
        return this;
    }

    /**
     * Sets the actual behavior observed.
     *
     * @param actualBehaviorParam the actual behavior
     * @return this builder instance
     */
    public final BugTicketBuilder actualBehavior(final String actualBehaviorParam) {
        this.actualBehavior = actualBehaviorParam;
        return this;
    }

    /**
     * Sets the frequency of the bug occurrence.
     *
     * @param frequencyParam the frequency
     * @return this builder instance
     */
    public final BugTicketBuilder frequency(final BugFrequency frequencyParam) {
        this.frequency = frequencyParam;
        return this;
    }

    /**
     * Sets the severity of the bug.
     *
     * @param severityParam the severity
     * @return this builder instance
     */
    public final BugTicketBuilder severity(final BugSeverity severityParam) {
        this.severity = severityParam;
        return this;
    }

    /**
     * Sets the error code associated with the bug.
     *
     * @param errorCodeParam the error code
     * @return this builder instance
     */
    public final BugTicketBuilder errorCode(final int errorCodeParam) {
        this.errorCode = errorCodeParam;
        return this;
    }

    /**
     * Builds and returns a new BugTicket.
     *
     * @return a new Ticket instance
     */
    @Override
    public Ticket build() {
        return new Ticket(this);
    }
}
