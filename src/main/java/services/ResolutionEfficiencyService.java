package services;

import context.AppContext;
import performance.PerformanceUtils;
import tickets.Ticket;
import tickets.TicketPriority;
import tickets.TicketStatus;
import tickets.TicketType;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service responsible for generating the Resolution Efficiency Report.
 * It measures how quickly and effectively tickets are being resolved by type.
 */
public final class ResolutionEfficiencyService {

    private static final double BUG_MULTIPLIER = 10.0;
    private static final double BUG_MAX_VALUE = 70.0;
    private static final double FEATURE_MAX_VALUE = 20.0;
    private static final double UI_MAX_VALUE = 20.0;
    private static final double ROUND_FACTOR = 100.0;

    public ResolutionEfficiencyService() { }

    /**
     * Generates a report regarding resolution efficiency for CLOSED and RESOLVED tickets.
     * @param context the application context
     * @return a map containing the efficiency report data
     */
    public Map<String, Object> generateResolutionEfficiencyReport(final AppContext context) {
        List<Ticket> eligibleTickets = context.getTicketDatabase()
                .getTickets().values()
                .stream()
                .filter(t -> t.getStatus() == TicketStatus.CLOSED
                        || t.getStatus() == TicketStatus.RESOLVED)
                .collect(Collectors.toList());

        Map<String, Object> report = new LinkedHashMap<>();

        report.put("totalTickets", eligibleTickets.size());
        report.put("ticketsByType", countByType(eligibleTickets));
        report.put("ticketsByPriority", countByPriority(eligibleTickets));
        report.put("efficiencyByType", calculateEfficiencyByType(eligibleTickets));

        return report;
    }

    private Map<String, Integer> countByType(final List<Ticket> tickets) {
        return tickets.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getType().name(),
                        LinkedHashMap::new,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));
    }

    private Map<String, Integer> countByPriority(final List<Ticket> tickets) {
        Map<String, Integer> result = new LinkedHashMap<>();

        for (TicketPriority p : TicketPriority.values()) {
            int count = (int) tickets.stream()
                    .filter(t -> t.getBusinessPriority() == p)
                    .count();
            result.put(p.name(), count);
        }
        return result;
    }

    private Map<String, Double> calculateEfficiencyByType(final List<Ticket> tickets) {
        Map<TicketType, List<Double>> scoresByType = new EnumMap<>(TicketType.class);

        for (Ticket ticket : tickets) {
            long daysToResolve = ChronoUnit.DAYS.between(
                    ticket.getAssignedAt(),
                    ticket.getSolvedAt()
            ) + 1;

            double baseScore;

            switch (ticket.getType()) {
                case BUG -> {
                    baseScore = (ticket.getBugFrequency().getScore()
                            + ticket.getSeverity().getScore())
                            * BUG_MULTIPLIER / daysToResolve;
                    baseScore = PerformanceUtils.calculateImpactFinal(baseScore, BUG_MAX_VALUE);
                }
                case FEATURE_REQUEST -> {
                    baseScore = (double) (ticket.getBusinessValue().getScore()
                            + ticket.getCustomerDemand().getScore())
                            / daysToResolve;
                    baseScore = PerformanceUtils.calculateImpactFinal(baseScore, FEATURE_MAX_VALUE);
                }
                case UI_FEEDBACK -> {
                    baseScore = (double) (ticket.getUsabilityScore()
                            + ticket.getBusinessValue().getScore())
                            / daysToResolve;
                    baseScore = PerformanceUtils.calculateImpactFinal(baseScore, UI_MAX_VALUE);
                }
                default -> {
                    baseScore = 0.0;
                }
            }

            scoresByType
                    .computeIfAbsent(ticket.getType(), k -> new ArrayList<>())
                    .add(baseScore);
        }

        Map<String, Double> result = new LinkedHashMap<>();
        for (TicketType type : TicketType.values()) {
            List<Double> scores = scoresByType.getOrDefault(type, List.of());
            double avg = PerformanceUtils.calculateAverageImpact(scores);
            result.put(type.name(), roundTwoDecimals(avg));
        }

        return result;
    }

    private double roundTwoDecimals(final double value) {
        return Math.round(value * ROUND_FACTOR) / ROUND_FACTOR;
    }
}
