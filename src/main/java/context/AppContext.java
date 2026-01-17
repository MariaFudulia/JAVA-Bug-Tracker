package context;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import database.MilestoneDatabase;
import database.TicketDatabase;
import database.UserDatabase;
import users.User;
import workflow.DevelopmentPhase;
import workflow.Workflow;
import workflow.WorkflowPhase;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class AppContext {
    private TicketDatabase ticketDatabase;
    private JsonNode input;
    private UserDatabase userDatabase;
    private WorkflowPhase workflowPhase;
    private MilestoneDatabase milestoneDatabase;
    private ObjectMapper mapper;
    LocalDate startedTesting;

    public AppContext(final TicketDatabase ticketDatabase,
                      final UserDatabase userDatabase,
                      final  WorkflowPhase workflowPhase,
                      final ObjectMapper mapper, final LocalDate startedTesting,
                      final MilestoneDatabase milestoneDatabase) {
        this.ticketDatabase = ticketDatabase;
        this.userDatabase = userDatabase;
        this.workflowPhase = workflowPhase;
        this.mapper = mapper;
        this.startedTesting = startedTesting;
        this.milestoneDatabase = milestoneDatabase;
    }

    public JsonNode getInput() {
        return input;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public TicketDatabase getTicketDatabase() {
        return ticketDatabase;
    }

    public UserDatabase getUserDatabase() {
        return userDatabase;
    }

    public WorkflowPhase getWorkflowPhase() {
        return workflowPhase;
    }

    public MilestoneDatabase getMilestoneDatabase() {
        return milestoneDatabase;
    }

    public LocalDate getStartedTesting() {
        return startedTesting;
    }

    public void setInput(JsonNode input) {
        this.input = input;
    }

    public void updateWorkflowPhase(WorkflowPhase workflowPhase) {
        this.workflowPhase = workflowPhase;
    }

    public boolean isInTesting() {
        return workflowPhase.getCurrentPhase().canReportTicket();
    }

    public void applyAutomaticPhaseUpdates() {
        if (isInTesting()) {
            LocalDate now = LocalDate.parse(input.get("timestamp").asText());
            if ((int) ChronoUnit.DAYS.between(startedTesting, now) >= 12) {
                workflowPhase.setCurrentPhase(new DevelopmentPhase());
            }
        }
    }
}
