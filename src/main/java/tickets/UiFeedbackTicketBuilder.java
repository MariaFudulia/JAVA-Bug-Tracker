package tickets;

public class UiFeedbackTicketBuilder extends TicketBuilder {
    public UiFeedbackTicketBuilder() {
        this.type = TicketType.UI_FEEDBACK;
    }

    public UiFeedbackTicketBuilder uiElementId(String uiElementId) {
        this.uiElementId = uiElementId;
        return this;
    }

    public UiFeedbackTicketBuilder usabilityScore(int usabilityScore) {
        this.usabilityScore = usabilityScore;
        return this;
    }
}
