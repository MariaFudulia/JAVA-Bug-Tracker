package tickets;

public enum TicketPriority {
    LOW(1),
    MEDIUM(2),
    HIGH(3),
    CRITICAL(4);

    private int score;
    TicketPriority(final int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }
}
