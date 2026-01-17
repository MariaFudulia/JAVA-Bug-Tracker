package workflow;

public class VerificationPhase implements Workflow {
    @Override
    public boolean canReportTicket() {
        return false;
    }
}
