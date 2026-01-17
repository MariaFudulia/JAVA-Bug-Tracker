package tickets;

public class FeatureRequestTicketBuilder extends TicketBuilder {
    public FeatureRequestTicketBuilder() {
        this.type = TicketType.FEATURE_REQUEST;
    }

    public FeatureRequestTicketBuilder businessValue(Impact businessValue) {
        this.businessValue = businessValue;
        return this;
    }

    public FeatureRequestTicketBuilder customerDemand(Demand customerDemand) {
        this.customerDemand = customerDemand;
        return this;
    }
}
