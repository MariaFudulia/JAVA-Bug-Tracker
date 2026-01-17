package services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import context.AppContext;
import database.MilestoneDatabase;
import database.TicketDatabase;
import milestones.Milestone;
import milestones.MilestoneBuilder;
import milestones.MilestoneStatus;
import tickets.Ticket;
import tickets.TicketPriority;
import tickets.TicketStatus;
import users.Developer;
import users.Manager;
import users.User;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MilestoneService {
    MilestoneDatabase milestoneDatabase;

    public MilestoneService(MilestoneDatabase milestoneDatabase) {
        this.milestoneDatabase = milestoneDatabase;
    }

    public Milestone createMilestone(AppContext context) {
        JsonNode input = context.getInput();
        ObjectMapper mapper = new ObjectMapper();

        Milestone milestone = new MilestoneBuilder()
                .name(input.get("name").asText())
                .createdAt(LocalDate.parse(input.get("timestamp").asText()))
                .dueDate(LocalDate.parse(input.get("dueDate").asText()))
                .createdBy(input.get("username").asText())
                .tickets(mapper.convertValue(input.get("tickets"),
                        new TypeReference<List<Integer>>(){}))
                .assignedDevs(mapper.convertValue(
                        input.get("assignedDevs"),
                        new TypeReference<List<String>>() {}))
                .blockingFor(mapper.convertValue(
                        input.get("blockingFor"),
                        new TypeReference<List<String>>() {}))
                .build();

        String name = input.get("name").asText();
        milestoneDatabase.addMilestone(name, milestone);
        return milestone;
    }

    public int TicketsAlreadyAssigned(AppContext context, TicketService ticketService) {
        JsonNode input = context.getInput();
        ObjectMapper mapper = new ObjectMapper();

        List<Integer> ticketIds = mapper.convertValue(input.get("tickets"),
                new TypeReference<List<Integer>>(){});

        for (Integer ticketId : ticketIds) {
            if (ticketService.getTicketById(ticketId).isAssignedToAMilestone())
                return ticketId;

        }

        return -1;
    }

    public void assignTicketsToMilestone(AppContext context, TicketService ticketService) {
        JsonNode input = context.getInput();
        ObjectMapper mapper = new ObjectMapper();

        List<Integer> ticketIds = mapper.convertValue(input.get("tickets"),
                new TypeReference<List<Integer>>(){});

        for (Integer ticketId : ticketIds) {
            ticketService.getTicketById(ticketId)
                    .setIsAssignedToAMilestone(true);
            ticketService.getTicketById(ticketId).setBelongsToMilestone(input
                    .get("name").asText());
        }
    }

    public void assignDevsToMilestone(AppContext context) {
        JsonNode input = context.getInput();
        ObjectMapper mapper = new ObjectMapper();

        List<String> assignedDevs = mapper.convertValue(
                input.get("assignedDevs"),
                new TypeReference<List<String>>() {});

        for (String dev : assignedDevs) {
            Developer user = (Developer) context.getUserDatabase().getUser(dev);
            user.addToMilestoneList(input.get("name").asText());
        }
    }

    public void appointManagerToMilestone(AppContext context) {
        JsonNode input = context.getInput();
        ObjectMapper mapper = new ObjectMapper();
        Manager user =  (Manager) context.getUserDatabase().getUser(input.get("username").asText());
        user.addToMilestoneList(input.get("name").asText());
    }

    public void blockOtherMilestones(AppContext context) {
        JsonNode input = context.getInput();
        ObjectMapper mapper = new ObjectMapper();

        List<String> blockingFor =  mapper.convertValue(
                input.get("blockingFor"),
                new TypeReference<List<String>>() {});

        for (String name : blockingFor) {
            milestoneDatabase.getMilestone(name).setIsBlocked(true);
        }

    }

    public void refreshMilestone(AppContext context, TicketService ticketService) {
        applyPriorityUpdates(context, ticketService);
        updateMetadata(context);
    }

    public void applyPriorityUpdates(AppContext context, TicketService ticketService) {
        JsonNode input = context.getInput();
        Map<String, Milestone> milestones = milestoneDatabase.getMilestones();
        LocalDate now = LocalDate.parse(input.get("timestamp").asText());

        for (Milestone milestone : milestones.values()) {
            if (milestone.isBlocked())
                continue;
            long daysSinceCreation = ChronoUnit.DAYS.between(milestone
                    .getCreatedAt(), now) + 1;
            long daysUntilDue = ChronoUnit.DAYS.between(now, milestone
                    .getDueDate()) + 1;

            List<Integer> ticketIds = milestone.getTickets();
            for (Integer ticketId : ticketIds) {
                Ticket ticket = ticketService.getTicketById(ticketId);
                if (ticket.getStatus() == TicketStatus.CLOSED)
                    continue;

                if (daysUntilDue <= 1)
                    ticket.setBusinessPriority(TicketPriority.CRITICAL);
                else if (daysSinceCreation >= 3) {
                    int steps = (int) (daysSinceCreation / 3);
                    ticket.increasePriorityBy(steps);
                }
            }
        }
    }

    public void updateMetadata(AppContext context) {
        JsonNode input = context.getInput();
        Map<String, Milestone> milestones = milestoneDatabase.getMilestones();
        LocalDate now = LocalDate.parse(input.get("timestamp").asText());

        for (Milestone milestone : milestones.values()) {
            long days = ChronoUnit.DAYS.between(now, milestone
                    .getDueDate()) + 1;

            long overdue = ChronoUnit.DAYS.between(now, milestone
                    .getDueDate());

            if (milestone.getStatus() == MilestoneStatus.COMPLETED)
                return;

            milestone.setDaysUntilDue((int) Math.max(0, days));
            milestone.setOverdueBy((int) (overdue < 0 ? Math.abs(overdue) + 1 : 0));
        }
    }

}
