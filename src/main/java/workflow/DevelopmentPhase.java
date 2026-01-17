package workflow;

public class DevelopmentPhase implements Workflow {
    public DevelopmentPhase() {}

    @Override
    public boolean canReportTicket() {
        return false;
    }
}
