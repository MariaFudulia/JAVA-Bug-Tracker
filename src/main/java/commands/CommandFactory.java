package commands;

import context.AppContext;
import database.MilestoneDatabase;
import database.TicketDatabase;
import services.*;

public class CommandFactory {
    public CommandFactory() { }

    /**
     *
     * @param appContext
     * @param ticketService
     * @param milestoneService
     * @param milestoneDatabase
     * @return command
     */
    public Command createCommand(final AppContext appContext,
                                 final TicketService ticketService,
                                 final TicketDatabase ticketDatabase,
                                 final MilestoneService milestoneService,
                                 final MilestoneDatabase milestoneDatabase,
                                 final PerformanceService performanceService,
                                 final CustomerImpactService customerImpactService,
                                 final TicketRiskService ticketRiskService,
                                 final ResolutionEfficiencyService efficiencyService,
                                 final AppStabilityService stabilityService) {
        String cmdName = appContext.getInput().get("command").asText();
        switch (cmdName) {
            case "reportTicket":
                return new ReportTicketCommand(ticketService, appContext);
            case "viewTickets":
                return new ViewTicketsCommand(ticketService, appContext);
            case "createMilestone":
                return new CreateMilestoneCommand(milestoneService, appContext, ticketService);
            case "viewMilestones":
                return new ViewMilestonesCommand(milestoneService, appContext,
                        ticketService, milestoneDatabase);
            case "assignTicket":
                return new AssignTicketCommand(ticketService, appContext, milestoneDatabase);
            case "viewAssignedTickets":
                return new ViewAssignedTicketsCommand(ticketService, appContext);
            case "undoAssignTicket":
                return new UndoAssignTicketCommand(ticketService, appContext, milestoneDatabase);
            case "addComment":
                return new AddCommentCommand(appContext, ticketService);
            case "undoAddComment":
                return new UndoAddCommentCommand(ticketService, appContext);
            case "changeStatus":
                return new ChangeStatusCommand(appContext, ticketService, milestoneDatabase);
            case "undoChangeStatus":
                return new UndoChangeStatusCommand(appContext, ticketService);
            case "viewTicketHistory":
                return new ViewTicketHistory(appContext, milestoneDatabase,
                        ticketDatabase, ticketService);
            case "viewNotifications":
                return new ViewNotificationsCommand(appContext);
            case "generatePerformanceReport":
                return new GeneratePerformanceReportCommand(performanceService, appContext,
                        ticketService);
            case "generateCustomerImpactReport":
                return new GenerateCustomerImpactReportCommand(appContext,customerImpactService);
            case "generateTicketRiskReport":
                return new GenerateTicketRiskReportCommand(appContext, ticketRiskService);
            case "generateResolutionEfficiencyReport":
                return new GenerateResolutionEfficiencyReportCommand(appContext,
                        efficiencyService);
            case "appStabilityReport":
                return new GenerateAppStabilityReportCommand(appContext, stabilityService);
            default:
                return null;
        }
    }
}
