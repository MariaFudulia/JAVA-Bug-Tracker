package services;

import com.fasterxml.jackson.databind.JsonNode;
import context.AppContext;
import database.TicketDatabase;
import performance.PerformanceUtils;
import tickets.Ticket;
import tickets.TicketPriority;
import tickets.TicketStatus;
import tickets.TicketType;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service responsible for generating reports regarding customer impact.
 */
public final class CustomerImpactService {
    private static final double MAX_PERCENTAGE = 100.0;
    private static final double BUG_MAX_VALUE = 48.0;
    private static final double ROUND_SCALE = 100.0;

    public CustomerImpactService() { }

    /**
     * Generates a customer impact report based on open and in-progress tickets.
     * @param context the application context
     * @return a map containing the impact report data
     */
    public Map<String, Object> generateCustomerImpactReport(final AppContext context) {
        JsonNode input = context.getInput();
        TicketDatabase ticketDatabase = context.getTicketDatabase();

        List<Ticket> relevantTickets = ticketDatabase.getTickets().values().stream()
                .filter(t -> t.getStatus() == TicketStatus.OPEN
                        || t.getStatus() == TicketStatus.IN_PROGRESS)
                .toList();

        Map<TicketType, List<Ticket>> ticketsByType = relevantTickets.stream()
                .collect(Collectors.groupingBy(Ticket::getType));

        Map<String, Double> customerImpactByType = new LinkedHashMap<>();
        Map<String, Integer> ticketsByTypeCount = new LinkedHashMap<>();
        Map<String, Integer> ticketsByPriority = new LinkedHashMap<>();

        for (TicketPriority p : TicketPriority.values()) {
            ticketsByPriority.put(p.name(), 0);
        }

        int totalTickets = relevantTickets.size();

        for (TicketType type : TicketType.values()) {
            List<Ticket> tickets = ticketsByType.getOrDefault(type, List.of());
            ticketsByTypeCount.put(type.name(), tickets.size());

            List<Double> scores = new ArrayList<>();
            for (Ticket t : tickets) {
                double baseScore = 0.0;
                double maxValue = MAX_PERCENTAGE;

                switch (type) {
                    case BUG -> {
                        baseScore = t.getBugFrequency().getScore()
                                * t.getBusinessPriority().getScore()
                                * t.getSeverity().getScore();
                        maxValue = BUG_MAX_VALUE;
                    }
                    case FEATURE_REQUEST -> {
                        baseScore = (double) t.getBusinessValue().getScore()
                                * t.getCustomerDemand().getScore();
                        maxValue = MAX_PERCENTAGE;
                    }
                    case UI_FEEDBACK -> {
                        baseScore = (double) t.getBusinessValue().getScore()
                                * t.getUsabilityScore();
                        maxValue = MAX_PERCENTAGE;
                    }
                    default -> {
                        baseScore = 0.0;
                        maxValue = MAX_PERCENTAGE;
                    }
                }

                double finalScore = Math.min(MAX_PERCENTAGE, (baseScore * MAX_PERCENTAGE)
                        / maxValue);
                scores.add(finalScore);

                ticketsByPriority.merge(t.getBusinessPriority().name(), 1, Integer::sum);
            }

            double avgScore = scores.stream().mapToDouble(Double::doubleValue).average()
                    .orElse(0.0);
            customerImpactByType.put(type.name(), Math.round(avgScore * ROUND_SCALE) / ROUND_SCALE);
        }

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("totalTickets", totalTickets);
        report.put("ticketsByType", ticketsByTypeCount);
        report.put("ticketsByPriority", ticketsByPriority);
        report.put("customerImpactByType", customerImpactByType);

        return report;
    }

    /**
     * Calculates the average impact score grouped by ticket type.
     * @param tickets list of tickets to analyze
     * @return a map of ticket types and their average impact scores
     */
    public Map<String, Double> calculateImpactByType(final List<Ticket> tickets) {

        Map<TicketType, List<Double>> scoresByType = new EnumMap<>(TicketType.class);

        for (Ticket ticket : tickets) {
            double baseScore;
            double finalScore;

            switch (ticket.getType()) {
                case BUG -> {
                    baseScore = (double) ticket.getBugFrequency().getScore()
                            * ticket.getBusinessPriority().getScore()
                            * ticket.getSeverity().getScore();
                    finalScore = PerformanceUtils.calculateImpactFinal(baseScore, BUG_MAX_VALUE);
                }
                case FEATURE_REQUEST -> {
                    baseScore = (double) ticket.getBusinessValue().getScore()
                            * ticket.getCustomerDemand().getScore();
                    finalScore = PerformanceUtils.calculateImpactFinal(baseScore, MAX_PERCENTAGE);
                }
                case UI_FEEDBACK -> {
                    baseScore = (double) ticket.getBusinessValue().getScore()
                            * ticket.getUsabilityScore();
                    finalScore = PerformanceUtils.calculateImpactFinal(baseScore, MAX_PERCENTAGE);
                }
                default -> {
                    finalScore = 0.0;
                }
            }

            scoresByType
                    .computeIfAbsent(ticket.getType(), k -> new ArrayList<>())
                    .add(finalScore);
        }

        Map<String, Double> result = new LinkedHashMap<>();

        for (Map.Entry<TicketType, List<Double>> entry : scoresByType.entrySet()) {
            double avg = PerformanceUtils.calculateAverageImpact(entry.getValue());
            result.put(
                    entry.getKey().name(),
                    Math.round(avg * ROUND_SCALE) / ROUND_SCALE
            );
        }

        return result;
    }
}
