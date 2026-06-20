package workflow;

public interface Workflow {
    /**
     *
     * @return if ticket can be reported in this phase
     */
    boolean canReportTicket();
}
