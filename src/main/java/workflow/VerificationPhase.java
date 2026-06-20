package workflow;

public class VerificationPhase implements Workflow {
    /**
     *
     * @return true if ticket can be reported
     */
    @Override
    public boolean canReportTicket() {
        return false;
    }
}
