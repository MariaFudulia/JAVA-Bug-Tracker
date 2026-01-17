package tickets;

public class TicketBuilderFactory {
    private TicketBuilderFactory() {}

    public static TicketBuilder create(final TicketType ticketType) {
        return switch (ticketType) {
            case BUG -> new BugTicketBuilder();
            case FEATURE_REQUEST ->  new FeatureRequestTicketBuilder();
            case UI_FEEDBACK -> new UiFeedbackTicketBuilder();
        };
    }
}
