package tickets;

public class UiFeedbackTicketBuilder extends TicketBuilder {
    public UiFeedbackTicketBuilder() {
        this.type = TicketType.UI_FEEDBACK;
    }

    /**
     *
     * @param uiElementId
     * @return builder
     */
    public UiFeedbackTicketBuilder uiElementId(final String uiElementId) {
        this.uiElementId = uiElementId;
        return this;
    }

    /**
     *
     * @param usabilityScore
     * @return builder
     */
    public UiFeedbackTicketBuilder usabilityScore(final int usabilityScore) {
        this.usabilityScore = usabilityScore;
        return this;
    }

    /**
     *
     * @param businessValue
     * @return builder
     */
    public UiFeedbackTicketBuilder businessValue(final Impact businessValue) {
        this.businessValue = businessValue;
        return this;
    }
}
