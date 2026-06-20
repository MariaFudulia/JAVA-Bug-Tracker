package workflow;

import java.time.LocalDate;

public class TestingPhase implements Workflow {
    private WorkflowPhase phase;
    private LocalDate startDate;

    public TestingPhase(final WorkflowPhase phase) {
        this.phase = phase;
    }

    /**
     *
     * @param startDate
     */
    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    /**
     *
     * @return phase
     */
    public WorkflowPhase getPhase() {
        return phase;
    }

    /**
     *
     * @return start date
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     *
     * @return true if ticket can be reported
     */
    @Override
    public boolean canReportTicket() {
        return true;
    }
}
