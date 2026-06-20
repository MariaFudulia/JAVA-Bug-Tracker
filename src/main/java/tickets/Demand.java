package tickets;

public enum Demand {
    LOW(1),
    MEDIUM(3),
    HIGH(6),
    VERY_HIGH(10);

    private int score;
    Demand(final int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }
}
