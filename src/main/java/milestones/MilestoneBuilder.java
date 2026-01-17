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

    public MilestoneBuilder name(final String name) {
        this.name = name;
        return this;
    }

    public MilestoneBuilder tickets(final List<Integer> tickets) {
        this.tickets = tickets;
        return this;
    }

    public MilestoneBuilder assignedDevs(final List<String> assignedDevs) {
        this.assignedDevs = assignedDevs;
        return this;
    }

    public MilestoneBuilder dueDate(final LocalDate dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public MilestoneBuilder createdBy(final String createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public MilestoneBuilder createdAt(final LocalDate createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public MilestoneBuilder blockingFor(final List<String> blockingFor) {
        this.blockingFor = blockingFor;
        return this;
    }

    public Milestone build() {
        return new Milestone(name, createdAt, dueDate, createdBy, tickets,
                assignedDevs, blockingFor);
    }
}
