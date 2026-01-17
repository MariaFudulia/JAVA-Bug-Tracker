package tickets;

public class BugTicketBuilder extends TicketBuilder {
    public BugTicketBuilder() {
        this.type = TicketType.BUG;
    }

    public BugTicketBuilder expectedBehavior(String expectedBehavior) {
        this.expectedBehavior = expectedBehavior;
        return this;
    }

    public BugTicketBuilder actualBehavior(String actualBehavior) {
        this.actualBehavior = actualBehavior;
        return this;
    }

    public BugTicketBuilder frequency(BugFrequency frequency) {
        this.frequency = frequency;
        return this;
    }

    public BugTicketBuilder severity(BugSeverity severity) {
        this.severity = severity;
        return this;
    }

    public BugTicketBuilder errorCode(int errorCode) {
        this.errorCode = errorCode;
        return this;
    }
}
