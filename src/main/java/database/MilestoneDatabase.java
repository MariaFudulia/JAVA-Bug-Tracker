package database;

import milestones.Milestone;

import java.util.*;
import java.util.stream.Collectors;

public class MilestoneDatabase {
    private Map<String, Milestone> milestones = new HashMap<>();

    public void addMilestone(String name, Milestone milestone) {
        milestones.put(name, milestone);
    }

    public Map<String, Milestone> getMilestones() {
        return milestones;
    }

    public Milestone getMilestone(String name) {
        return milestones.get(name);
    }

    public List<Milestone> getMilestonesSortedByDueDateAndName(List<Milestone> milestones) {
        return milestones.stream()
                .sorted(
                        Comparator
                                .comparing(Milestone::getDueDate)
                                .thenComparing(Milestone::getName)
                )
                .collect(Collectors.toList());
    }
}
