package services;

import context.AppContext;
import tickets.Ticket;
import tickets.TicketPriority;
import tickets.TicketStatus;
import tickets.TicketType;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service responsible for determining the overall stability of the application.
 * Analyzes risk and customer impact metrics to categorize application health.
 */
public final class AppStabilityService {

    private static final double STABILITY_THRESHOLD = 50.0;
    private final TicketRiskService ticketRiskService;
    private final CustomerImpactService customerImpactService;

    public AppStabilityService(final TicketRiskService ticketRiskService,
                               final CustomerImpactService customerImpactService) {
        this.ticketRiskService = ticketRiskService;
        this.customerImpactService = customerImpactService;
    }

    /**
     * Generates a stability report based on current open and in-progress tickets.
     *
     * @param context the application context
     * @return a map containing the stability metrics and final status
     */
    public Map<String, Object> generateAppStabilityReport(final AppContext context) {

        List<Ticket> openTickets = context.getTicketDatabase()
                .getTickets().values()
                .stream()
                .filter(t -> t.getStatus() == TicketStatus.OPEN
                        || t.getStatus() == TicketStatus.IN_PROGRESS)
                .collect(Collectors.toList());

        Map<String, Object> report = new LinkedHashMap<>();

        report.put("totalOpenTickets", openTickets.size());
        report.put("openTicketsByType", countByType(openTickets));
        report.put("openTicketsByPriority", countByPriority(openTickets));

        if (openTickets.isEmpty()) {
            report.put("appStability", "STABLE");
            return report;
        }

        Map<String, String> riskByType =
                ticketRiskService.calculateRiskByType(openTickets);

        Map<String, Double> impactByType =
                customerImpactService.calculateImpactByType(openTickets);

        report.put("riskByType", riskByType);
        report.put("impactByType", impactByType);

        report.put("appStability",
                determineStability(riskByType, impactByType));

        return report;
    }

    private String determineStability(final Map<String, String> riskByType,
                                      final Map<String, Double> impactByType) {

        if (riskByType.containsValue("SIGNIFICANT") || riskByType.containsValue("MAJOR")) {
            return "UNSTABLE";
        }

        boolean allNegligible =
                riskByType.values().stream()
                        .allMatch(r -> r.equals("NEGLIGIBLE"));

        boolean allImpactBelowThreshold =
                impactByType.values().stream()
                        .allMatch(v -> v < STABILITY_THRESHOLD);

        if (allNegligible && allImpactBelowThreshold) {
            return "STABLE";
        }

        return "PARTIALLY_STABLE";
    }

    private Map<String, Integer> countByType(final List<Ticket> tickets) {
        Map<String, Integer> result = new LinkedHashMap<>();
        for (TicketType type : TicketType.values()) {
            int count = (int) tickets.stream()
                    .filter(t -> t.getType() == type)
                    .count();
            result.put(type.name(), count);
        }
        return result;
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
}
