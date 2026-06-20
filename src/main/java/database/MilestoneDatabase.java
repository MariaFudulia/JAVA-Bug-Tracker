package database;

import milestones.Milestone;


import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MilestoneDatabase {
    private Map<String, Milestone> milestones = new HashMap<>();

    /**
     *
     * @param name
     * @param milestone
     */
    public void addMilestone(final String name, final Milestone milestone) {
        milestones.put(name, milestone);
    }

    /**
     *
     * @return milestones
     */
    public Map<String, Milestone> getMilestones() {
        return milestones;
    }

    /**
     *
     * @param name
     * @return milestone by name
     */
    public Milestone getMilestone(final String name) {
        return milestones.get(name);
    }

    /**
     *
     * @param milestonesList
     * @return sorted milestones by due date and name
     */
    public List<Milestone> getMilestonesSortedByDueDateAndName(
            final List<Milestone> milestonesList) {
        return milestonesList.stream()
                .sorted(
                        Comparator
                                .comparing(Milestone::getDueDate)
                                .thenComparing(Milestone::getName)
                )
                .collect(Collectors.toList());
    }
}
