package workflow;

public class WorkflowPhase {
    private Workflow currentPhase;

    public WorkflowPhase() {
        this.currentPhase = new TestingPhase(this);
    }

    /**
     *
     * @param currentPhase
     */
    public void setCurrentPhase(final Workflow currentPhase) {
        this.currentPhase = currentPhase;
    }

    /**
     *
     * @return current phase
     */
    public Workflow getCurrentPhase() {
        return currentPhase;
    }
}
