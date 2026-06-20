package context;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import database.MilestoneDatabase;
import database.TicketDatabase;
import database.UserDatabase;
import workflow.DevelopmentPhase;
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
    private LocalDate startedTesting;
    private final int TWELVE = 12;

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

    /**
     *
     * @return input
     */
    public JsonNode getInput() {
        return input;
    }

    /**
     *
     * @return mapper
     */
    public ObjectMapper getMapper() {
        return mapper;
    }

    /**
     *
     * @return ticket db
     */
    public TicketDatabase getTicketDatabase() {
        return ticketDatabase;
    }

    /**
     *
     * @return user db
     */
    public UserDatabase getUserDatabase() {
        return userDatabase;
    }

    /**
     *
     * @return phase
     */
    public WorkflowPhase getWorkflowPhase() {
        return workflowPhase;
    }

    /**
     *
     * @return milestone db
     */
    public MilestoneDatabase getMilestoneDatabase() {
        return milestoneDatabase;
    }

    /**
     *
     * @return when app started
     */
    public LocalDate getStartedTesting() {
        return startedTesting;
    }

    /**
     *
     * @param input
     */
    public void setInput(final JsonNode input) {
        this.input = input;
    }

    /**
     *
     * @param phase
     */
    public void updateWorkflowPhase(final WorkflowPhase phase) {
        this.workflowPhase = phase;
    }

    /**
     *
     * @return true if app is in testing phase
     */
    public boolean isInTesting() {
        return workflowPhase.getCurrentPhase().canReportTicket();
    }

    /**
     * updates phase
     */
    public void applyAutomaticPhaseUpdates() {
        if (isInTesting()) {
            LocalDate now = LocalDate.parse(input.get("timestamp").asText());
            if ((int) ChronoUnit.DAYS.between(startedTesting, now) >= TWELVE) {
                workflowPhase.setCurrentPhase(new DevelopmentPhase());
            }
        }
    }
}
