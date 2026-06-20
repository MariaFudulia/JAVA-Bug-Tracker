package milestones;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MilestoneBuilder {
    private String name;
    private List<Integer> tickets = new ArrayList<>();
    private List<String> assignedDevs = new ArrayList<>();
    private List<String> blockingFor = new ArrayList<>();

    private String createdBy;
    private LocalDate createdAt;
    private LocalDate dueDate;

    /**
     *
     * @param nameB
     * @return this
     */
    public MilestoneBuilder nameB(final String nameB) {
        this.name = nameB;
        return this;
    }

    /**
     *
     * @param ticketsB
     * @return this
     */
    public MilestoneBuilder ticketsB(final List<Integer> ticketsB) {
        this.tickets = ticketsB;
        return this;
    }

    /**
     *
     * @param assignedDevsB
     * @return this
     */
    public MilestoneBuilder assignedDevsB(final List<String> assignedDevsB) {
        this.assignedDevs = assignedDevsB;
        return this;
    }

    /**
     *
     * @param dueDateB
     * @return this
     */
    public MilestoneBuilder dueDateB(final LocalDate dueDateB) {
        this.dueDate = dueDateB;
        return this;
    }

    /**
     *
     * @param createdByB
     * @return this
     */
    public MilestoneBuilder createdByB(final String createdByB) {
        this.createdBy = createdByB;
        return this;
    }

    /**
     *
     * @param createdAtB
     * @return this
     */
    public MilestoneBuilder createdAtB(final LocalDate createdAtB) {
        this.createdAt = createdAtB;
        return this;
    }

    /**
     *
     * @param blockingForB
     * @return this
     */
    public MilestoneBuilder blockingForB(final List<String> blockingForB) {
        this.blockingFor = blockingForB;
        return this;
    }

    /**
     *
     * @return new milestone
     */
    public Milestone build() {
        return new Milestone(name, createdAt, dueDate, createdBy, tickets,
                assignedDevs, blockingFor);
    }
}
