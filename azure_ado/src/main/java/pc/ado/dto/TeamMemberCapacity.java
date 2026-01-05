package pc.ado.dto;

/**
 * Data Transfer Object for team member capacity information.
 */
public class TeamMemberCapacity {

    private final String displayName;
    private final double capacityPerDay;

    public TeamMemberCapacity(String displayName, double capacityPerDay) {
        this.displayName = displayName;
        this.capacityPerDay = capacityPerDay;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getCapacityPerDay() {
        return capacityPerDay;
    }
}
