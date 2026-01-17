package workflow;

import com.fasterxml.jackson.databind.node.ObjectNode;
import context.AppContext;

import java.time.LocalDate;

public class TestingPhase implements Workflow{
    private WorkflowPhase phase;
    private LocalDate startDate;

    public TestingPhase(WorkflowPhase phase) {
        this.phase = phase;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    public WorkflowPhase getPhase() {
        return phase;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    @Override
    public boolean canReportTicket() {
        return true;
    }
}
