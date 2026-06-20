package services;

import context.AppContext;
import performance.PerformanceUtils;
import tickets.Ticket;
import tickets.TicketPriority;
import tickets.TicketStatus;
import tickets.TicketType;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service responsible for calculating and reporting technical and business risks
 * associated with active tickets.
 */
public final class TicketRiskService {

    private static final String NEGLIGIBLE = "NEGLIGIBLE";
    private static final String MODERATE = "MODERATE";
    private static final String SIGNIFICANT = "SIGNIFICANT";
    private static final String MAJOR = "MAJOR";

    private static final int RISK_THRESHOLD_LOW = 24;
    private static final int RISK_THRESHOLD_MID = 49;
    private static final int RISK_THRESHOLD_HIGH = 74;

    private static final int CLASSIFY_THRESHOLD_LOW = 25;
    private static final int CLASSIFY_THRESHOLD_MID = 50;
    private static final int CLASSIFY_THRESHOLD_HIGH = 75;

    private static final double SCALE_100 = 100.0;
    private static final double BUG_MAX = 12.0;
    private static final double FEATURE_MAX = 20.0;
    private static final int UI_USABILITY_BASE = 11;

    public TicketRiskService() { }

    private String getRiskGrade(final double normalizedScore) {
        if (normalizedScore <= RISK_THRESHOLD_LOW) {
            return NEGLIGIBLE;
        }
        if (normalizedScore <= RISK_THRESHOLD_MID) {
            return MODERATE;
        }
        if (normalizedScore <= RISK_THRESHOLD_HIGH) {
            return SIGNIFICANT;
        }
        return MAJOR;
    }

    private double normalizeScore(final double baseScore, final double maxValue) {
        return Math.min(SCALE_100, (baseScore * SCALE_100) / maxValue);
    }

    /**
     * Generates a technical risk report based on open and in-progress tickets.
     * @param context the application context
     * @return a map containing the risk report data
     */
    public Map<String, Object> generateTicketRiskReport(final AppContext context) {
        List<Ticket> tickets = context.getTicketDatabase().getTickets().values().stream()
                .filter(t -> t.getStatus() == TicketStatus.OPEN
                        || t.getStatus() == TicketStatus.IN_PROGRESS)
                .collect(Collectors.toList());

        Map<TicketType, List<Double>> scoresByType = new HashMap<>();
        Map<TicketType, Integer> ticketsByTypeCount = new HashMap<>();
        Map<TicketPriority, Integer> ticketsByPriority = new HashMap<>();

        for (TicketType type : TicketType.values()) {
            scoresByType.put(type, new ArrayList<>());
            ticketsByTypeCount.put(type, 0);
        }
        for (TicketPriority prio : TicketPriority.values()) {
            ticketsByPriority.put(prio, 0);
        }

        for (Ticket t : tickets) {
            TicketType type = t.getType();
            TicketPriority priority = t.getBusinessPriority();

            ticketsByTypeCount.put(type, ticketsByTypeCount.get(type) + 1);
            ticketsByPriority.put(priority, ticketsByPriority.get(priority) + 1);

            double baseScore = 0.0;
            double maxScore = SCALE_100;

            switch (type) {
                case BUG -> {
                    baseScore = t.getBugFrequency().getScore() * t.getSeverity().getScore();
                    maxScore = BUG_MAX;
                }
                case FEATURE_REQUEST -> {
                    baseScore = t.getBusinessValue().getScore() + t.getCustomerDemand().getScore();
                    maxScore = FEATURE_MAX;
                }
                case UI_FEEDBACK -> {
                    baseScore = (UI_USABILITY_BASE - t.getUsabilityScore())
                            * t.getBusinessValue().getScore();
                    maxScore = SCALE_100;
                }
                default -> {
                    baseScore = 0.0;
                    maxScore = SCALE_100;
                }
            }

            double normalized = normalizeScore(baseScore, maxScore);
            scoresByType.get(type).add(normalized);
        }

        Map<String, String> riskByType = new HashMap<>();
        for (TicketType type : TicketType.values()) {
            List<Double> list = scoresByType.get(type);
            double avg = list.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            riskByType.put(type.name(), getRiskGrade(avg));
        }

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("totalTickets", tickets.size());
        report.put("ticketsByType", ticketsByTypeCount.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().name(), Map.Entry::getValue)));
        report.put("ticketsByPriority", ticketsByPriority.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().name(), Map.Entry::getValue)));
        report.put("riskByType", riskByType);

        return report;
    }

    /**
     * Calculates risk assessment for each ticket type based on provided tickets.
     * @param tickets list of tickets to analyze
     * @return a map of ticket type names and their risk classification
     */
    public Map<String, String> calculateRiskByType(final List<Ticket> tickets) {
        Map<TicketType, List<Double>> scoresByType = new EnumMap<>(TicketType.class);

        for (Ticket ticket : tickets) {
            double baseScore;
            double finalScore;

            switch (ticket.getType()) {
                case BUG -> {
                    baseScore = (double) ticket.getBugFrequency().getScore()
                            * ticket.getSeverity().getScore();
                    finalScore = PerformanceUtils.calculateImpactFinal(baseScore, BUG_MAX);
                }
                case FEATURE_REQUEST -> {
                    baseScore = (double) ticket.getBusinessValue().getScore()
                            + ticket.getCustomerDemand().getScore();
                    finalScore = PerformanceUtils.calculateImpactFinal(baseScore, FEATURE_MAX);
                }
                case UI_FEEDBACK -> {
                    baseScore = (double) (UI_USABILITY_BASE - ticket.getUsabilityScore())
                            * ticket.getBusinessValue().getScore();
                    finalScore = PerformanceUtils.calculateImpactFinal(baseScore, SCALE_100);
                }
                default -> {
                    finalScore = 0.0;
                }
            }
            scoresByType.computeIfAbsent(ticket.getType(), k -> new ArrayList<>()).add(finalScore);
        }

        Map<String, String> result = new LinkedHashMap<>();
        for (Map.Entry<TicketType, List<Double>> entry : scoresByType.entrySet()) {
            double avg = PerformanceUtils.calculateAverageImpact(entry.getValue());
            result.put(entry.getKey().name(), classifyRisk(avg));
        }
        return result;
    }

    private String classifyRisk(final double value) {
        if (value < CLASSIFY_THRESHOLD_LOW) {
            return NEGLIGIBLE;
        }
        if (value < CLASSIFY_THRESHOLD_MID) {
            return MODERATE;
        }
        if (value < CLASSIFY_THRESHOLD_HIGH) {
            return SIGNIFICANT;
        }
        return MAJOR;
    }
}
