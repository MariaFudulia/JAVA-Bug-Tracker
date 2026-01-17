package commands;

import context.AppContext;
import database.MilestoneDatabase;
import services.MilestoneService;
import services.TicketService;

public class CommandFactory {
    public CommandFactory() {}

    public Command createCommand(final AppContext appContext,
                                 final TicketService ticketService,
                                 final MilestoneService milestoneService,
                                 final MilestoneDatabase milestoneDatabase) {
        String cmdName = appContext.getInput().get("command").asText();
        switch (cmdName) {
            case "reportTicket":
                return new ReportTicketCommand(ticketService, appContext);
            case "viewTickets":
                return new ViewTicketsCommand(ticketService, appContext);
            case "createMilestone":
                return new CreateMilestoneCommand(milestoneService, appContext,  ticketService);
            case "viewMilestones":
                return new ViewMilestonesCommand(milestoneService, appContext,
                        ticketService, milestoneDatabase);
            default:
                return null;
        }
    }
}
