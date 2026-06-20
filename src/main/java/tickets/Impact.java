package tickets;

public enum Impact {
    S(1),
    M(3),
    L(6),
    XL(10);

    private int score;
    Impact(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }
}
