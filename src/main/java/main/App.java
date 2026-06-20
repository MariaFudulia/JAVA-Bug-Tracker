package main;

import commands.Command;
import commands.CommandFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import context.AppContext;
import database.MilestoneDatabase;
import database.TicketDatabase;
import database.UserDatabase;
import fileio.InputLoader;
import fileio.UserLoader;
import services.*;
import workflow.WorkflowPhase;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * main.App represents the main application logic that processes input commands,
 * generates outputs, and writes them to a file
 */
public class App {
    private App() {
    }

    private static final String INPUT_USERS_FIELD = "input/database/users.json";

    private static final ObjectWriter WRITER =
            new ObjectMapper().writer().withDefaultPrettyPrinter();

    /**
     * Runs the application: reads commands from an input file,
     * processes them, generates results, and writes them to an output file
     *
     * @param inputPath path to the input file containing commands
     * @param outputPath path to the file where results should be written
     */
    public static void run(final String inputPath, final String outputPath) {
        // feel free to change this if needed
        // however keep 'outputs' variable name to be used for writing
        List<ObjectNode> outputs = new ArrayList<>();

        /*
            TODO 1 :
            Load initial user data and commands. we strongly recommend using jackson library.
            you can use the reading from hw1 as a reference.
            however you can use some of the more advanced features of
            jackson library, available here: https://www.baeldung.com/jackson-annotations
        */
        ObjectMapper mapper = new ObjectMapper();
        InputLoader loader;
        List<JsonNode> commands;
        UserLoader userLoader;
        UserDatabase userDatabase;

        try {
            loader = new InputLoader(inputPath);
            commands = loader.getCommands();
        } catch (IOException e) {
            System.out.println("error reading input file: " + e.getMessage());
            return;
        }

        try {
            userLoader = new UserLoader(INPUT_USERS_FIELD);
            userDatabase = userLoader.getUserDatabase();
        } catch (IOException e) {
            System.out.println("error reading users file: " + e.getMessage());
            return;
        }

        TicketDatabase ticketDatabase = new TicketDatabase();
        TicketService ticketService = new TicketService(ticketDatabase);

        MilestoneDatabase milestoneDatabase = new MilestoneDatabase();
        MilestoneService milestoneService = new MilestoneService(milestoneDatabase);

        WorkflowPhase workflowPhase = new WorkflowPhase();
        String firstTimestamp = commands.get(0).get("timestamp").asText();
        LocalDate startDate = LocalDate.parse(firstTimestamp);

        AppContext appContext = new AppContext(ticketDatabase, userDatabase,
                workflowPhase, mapper, startDate, milestoneDatabase);

        PerformanceService performanceService = new PerformanceService();
        CustomerImpactService customerImpactService = new CustomerImpactService();
        TicketRiskService ticketRiskService = new TicketRiskService();
        ResolutionEfficiencyService resolutionEfficiencyService = new ResolutionEfficiencyService();
        AppStabilityService appStabilityService = new AppStabilityService(ticketRiskService, customerImpactService);

        for (JsonNode input : commands) {
            appContext.setInput(input);
            appContext.applyAutomaticPhaseUpdates();
            milestoneService.refreshMilestone(appContext, ticketService);
            CommandFactory factory = new CommandFactory();
            Command cmd = factory.createCommand(appContext, ticketService, ticketDatabase,
                    milestoneService, milestoneDatabase, performanceService,
                    customerImpactService, ticketRiskService, resolutionEfficiencyService,
                    appStabilityService);

            if (cmd == null) {
                continue;
            }

            ObjectNode node = cmd.execute();
            if (node != null) {
                outputs.add(node);
            }
        }

        // TODO 2: process commands.

        // TODO 3: create objectNodes for output, add them to outputs list.

        // DO NOT CHANGE THIS SECTION IN ANY WAY
        try {
            File outputFile = new File(outputPath);
            outputFile.getParentFile().mkdirs();
            WRITER.withDefaultPrettyPrinter().writeValue(outputFile, outputs);
        } catch (IOException e) {
            System.out.println("error writing to output file: " + e.getMessage());
        }
    }
}
