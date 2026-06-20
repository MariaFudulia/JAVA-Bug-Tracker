package tickets;

/**
 * Builder class for creating {@link Ticket} objects of type FEATURE_REQUEST.
 */
public class FeatureRequestTicketBuilder extends TicketBuilder {

    /**
     * Constructs a new FeatureRequestTicketBuilder and sets the default ticket type.
     */
    public FeatureRequestTicketBuilder() {
        this.type = TicketType.FEATURE_REQUEST;
    }

    /**
     * Sets the business value impact of the feature request.
     *
     * @param businessValueParam the estimated business impact
     * @return this builder instance
     */
    public final FeatureRequestTicketBuilder businessValue(final Impact businessValueParam) {
        this.businessValue = businessValueParam;
        return this;
    }

    /**
     * Sets the customer demand level for this feature.
     *
     * @param customerDemandParam the level of demand from customers
     * @return this builder instance
     */
    public final FeatureRequestTicketBuilder customerDemand(final Demand customerDemandParam) {
        this.customerDemand = customerDemandParam;
        return this;
    }

    /**
     * Builds and returns a new FeatureRequestTicket.
     *
     * @return a new Ticket instance
     */
    @Override
    public Ticket build() {
        return new Ticket(this);
    }
}
