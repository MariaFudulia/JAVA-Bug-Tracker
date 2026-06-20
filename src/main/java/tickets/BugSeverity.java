package tickets;

public enum BugSeverity {
    MINOR(1),
    MODERATE(2),
    SEVERE(3);

    private int score;

    BugSeverity(final int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }
}
