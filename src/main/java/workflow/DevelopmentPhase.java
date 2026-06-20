package workflow;

public class DevelopmentPhase implements Workflow {
    public DevelopmentPhase() { }

    /**
     *
     * @return true if ticket can be reported
     */
    @Override
    public boolean canReportTicket() {
        return false;
    }
}
