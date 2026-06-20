package services;

import com.fasterxml.jackson.databind.JsonNode;
import context.AppContext;
import database.TicketDatabase;
import performance.PerformanceUtils;
import tickets.Ticket;
import tickets.TicketPriority;
import tickets.TicketStatus;
import tickets.TicketType;
import users.Developer;
import users.Manager;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service responsible for generating performance reports for developers.
 */
public final class PerformanceService {
    private static final int JUNIOR_BONUS = 5;
    private static final int MID_BONUS = 15;
    private static final int SENIOR_BONUS = 30;

    private static final double WEIGHT_TOTAL_CLOSED = 0.5;
    private static final double WEIGHT_HIGH_PRIO_MID = 0.7;
    private static final double WEIGHT_AVG_RES_MID = 0.3;
    private static final double WEIGHT_HIGH_PRIO_SENIOR = 1.0;
    private static final double WEIGHT_AVG_RES_SENIOR = 0.5;

    private static final double ROUND_SCALE = 100.0;

    public PerformanceService() {
    }

    /**
     * Generates a performance report for all subordinates of a manager.
     * @param context the application context
     * @param ticketDatabase database containing tickets
     * @return a list of maps containing performance metrics
     */
    public List<Map<String, Object>> generatePerformanceReport(final AppContext context,
                                                               final TicketDatabase ticketDatabase) {
        JsonNode input = context.getInput();
        String managerUsername = input.get("username").asText();
        Manager manager = (Manager) context.getUserDatabase().getUser(managerUsername);
        LocalDate timestamp = LocalDate.parse(input.get("timestamp").asText());

        YearMonth previousMonth = YearMonth.from(timestamp.minusMonths(1));
        LocalDate startDate = previousMonth.atDay(1);
        LocalDate endDate = previousMonth.atEndOfMonth();

        List<Map<String, Object>> report = new ArrayList<>();

        List<Developer> devs = manager.getSubordinates().stream()
                .map(username -> (Developer) context.getUserDatabase().getUser(username))
                .sorted(Comparator.comparing(Developer::getUsername))
                .collect(Collectors.toList());

        for (Developer dev : devs) {
            List<Ticket> tickets = ticketDatabase.getTickets().values().stream()
                    .filter(t -> t.getActions().stream()
                            .anyMatch(a -> a.getBy().equals(dev.getUsername())))
                    .collect(Collectors.toList());

            List<Ticket> closedTickets = tickets.stream()
                    .filter(t -> t.getStatus() == TicketStatus.CLOSED)
                    .filter(t -> t.getSolvedAt() != null)
                    .filter(t -> {
                        LocalDate solved = t.getSolvedAt();
                        return !solved.isBefore(startDate) && !solved.isAfter(endDate);
                    })
                    .collect(Collectors.toList());

            int totalClosed = closedTickets.size();

            Map<String, Object> devReport = new LinkedHashMap<>();
            devReport.put("username", dev.getUsername());
            devReport.put("closedTickets", totalClosed);

            if (totalClosed == 0) {
                devReport.put("averageResolutionTime", 0.0);
                devReport.put("performanceScore", 0.0);
                devReport.put("seniority", dev.getSeniority().name());
                report.add(devReport);
                continue;
            }

            double avgResolution = closedTickets.stream()
                    .mapToDouble(t -> {
                        LocalDate solved = t.getSolvedAt();
                        LocalDate assigned = t.getAssignedAtToDev(dev.getUsername());
                        return (double) (ChronoUnit.DAYS.between(assigned, solved) + 1);
                    })
                    .average().orElse(0.0);

            int bug = (int) closedTickets.stream()
                    .filter(t -> t.getType() == TicketType.BUG).count();
            int feature = (int) closedTickets.stream()
                    .filter(t -> t.getType() == TicketType.FEATURE_REQUEST).count();
            int ui = (int) closedTickets.stream()
                    .filter(t -> t.getType() == TicketType.UI_FEEDBACK).count();
            int highPriority = (int) closedTickets.stream()
                    .filter(t -> t.getBusinessPriority() == TicketPriority.HIGH
                            || t.getBusinessPriority() == TicketPriority.CRITICAL)
                    .count();

            double performanceScore = 0.0;
            int seniorityBonus = switch (dev.getSeniority()) {
                case JUNIOR -> JUNIOR_BONUS;
                case MID -> MID_BONUS;
                case SENIOR -> SENIOR_BONUS;
                default -> 0;
            };

            switch (dev.getSeniority()) {
                case JUNIOR -> {
                    double diversity = PerformanceUtils.ticketDiversityFactor(bug, feature, ui);
                    performanceScore = Math.max(0, WEIGHT_TOTAL_CLOSED * totalClosed - diversity)
                            + seniorityBonus;
                }
                case MID -> {
                    performanceScore = Math.max(0, WEIGHT_TOTAL_CLOSED * totalClosed
                            + WEIGHT_HIGH_PRIO_MID * highPriority
                            - WEIGHT_AVG_RES_MID * avgResolution) + seniorityBonus;
                }
                case SENIOR -> {
                    performanceScore = Math.max(0, WEIGHT_TOTAL_CLOSED * totalClosed
                            + WEIGHT_HIGH_PRIO_SENIOR * highPriority
                            - WEIGHT_AVG_RES_SENIOR * avgResolution) + seniorityBonus;
                }
                default -> performanceScore = 0.0;
            }

            devReport.put("averageResolutionTime",
                    Math.round(avgResolution * ROUND_SCALE) / ROUND_SCALE);
            devReport.put("performanceScore",
                    Math.round(performanceScore * ROUND_SCALE) / ROUND_SCALE);
            devReport.put("seniority", dev.getSeniority().name());

            report.add(devReport);
        }

        return report;
    }
}