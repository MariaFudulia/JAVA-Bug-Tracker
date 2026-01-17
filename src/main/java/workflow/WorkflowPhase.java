package workflow;

public class WorkflowPhase {
    private Workflow currentPhase;

    public WorkflowPhase() {
        this.currentPhase = new TestingPhase(this);
    }

    public void setCurrentPhase(Workflow currentPhase) {
        this.currentPhase = currentPhase;
    }

    public Workflow getCurrentPhase() {
        return currentPhase;
    }
}
