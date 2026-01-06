package pc.ado.dto;

/**
 * Data Transfer Object for team member capacity information.
 */
public class TeamMemberCapacity {

    private final String displayName;
    private final double capacityPerDay;
    // Days off including member PTO & Holidays
    private final int daysOff;

    public TeamMemberCapacity(String displayName, double capacityPerDay, int daysOff) {
        this.displayName = displayName;
        this.capacityPerDay = capacityPerDay;
        this.daysOff = daysOff;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getCapacityPerDay() {
        return capacityPerDay;
    }

    /**
     * Days off including member PTO & Holidays
     *
     * @return daysOff
     */
    public int getDaysOff() {
        return daysOff;
    }
}
