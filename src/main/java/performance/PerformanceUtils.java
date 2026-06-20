package performance;

import java.util.List;

/**
 * Utility class for performance metric calculations.
 */
public final class PerformanceUtils {
    private static final double TYPE_COUNT = 3.0;
    private static final double MAX_PERCENTAGE = 100.0;

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private PerformanceUtils() {
    }

    /**
     * Calculates the average number of tickets across all types.
     *
     * @param bug     number of resolved bugs
     * @param feature number of resolved feature requests
     * @param ui      number of resolved UI feedback tickets
     * @return the average value
     */
    public static double averageResolvedTicketType(final int bug,
                                                   final int feature,
                                                   final int ui) {
        return (bug + feature + ui) / TYPE_COUNT;
    }

    /**
     * Calculates the standard deviation of resolved ticket types.
     *
     * @param bug     number of resolved bugs
     * @param feature number of resolved feature requests
     * @param ui      number of resolved UI feedback tickets
     * @return the standard deviation
     */
    public static double standardDeviation(final int bug,
                                           final int feature,
                                           final int ui) {
        double mean = averageResolvedTicketType(bug, feature, ui);
        double variance = (Math.pow(bug - mean, 2)
                + Math.pow(feature - mean, 2)
                + Math.pow(ui - mean, 2)) / TYPE_COUNT;
        return Math.sqrt(variance);
    }

    /**
     * Calculates the diversity factor based on the coefficient of variation.
     *
     * @param bug     number of resolved bugs
     * @param feature number of resolved feature requests
     * @param ui      number of resolved UI feedback tickets
     * @return the diversity factor
     */
    public static double ticketDiversityFactor(final int bug,
                                               final int feature,
                                               final int ui) {
        double mean = averageResolvedTicketType(bug, feature, ui);
        if (mean == 0.0) {
            return 0.0;
        }
        double std = standardDeviation(bug, feature, ui);
        return std / mean;
    }

    /**
     * Normalizes a base score relative to a maximum value, capped at 100.
     *
     * @param baseScore the raw score calculated
     * @param maxValue  the maximum possible value for normalization
     * @return normalized score between 0 and 100
     */
    public static double calculateImpactFinal(final double baseScore,
                                              final double maxValue) {
        if (maxValue <= 0) {
            return 0.0;
        }

        double normalized = (baseScore * MAX_PERCENTAGE) / maxValue;
        return Math.min(MAX_PERCENTAGE, normalized);
    }

    /**
     * Calculates the average value from a list of scores.
     *
     * @param scores list of normalized scores
     * @return the average impact score
     */
    public static double calculateAverageImpact(final List<Double> scores) {
        return scores.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }
}
