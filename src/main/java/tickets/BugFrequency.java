package tickets;

public enum BugFrequency {
    RARE(1),
    OCCASIONAL(2),
    FREQUENT(3),
    ALWAYS(4);

    private int score;

    BugFrequency(final int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }
}
