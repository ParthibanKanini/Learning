package pc.ado.dto;

/** Data Transfer Object for team member capacity information. */
public class TeamMemberCapacity {

  private final String displayName;
  private final double capacityPerDay;
  private final int daysOff;

  public TeamMemberCapacity(
      final String displayName, final double capacityPerDay, final int daysOff) {
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
   * Gets days off including member PTO and holidays.
   *
   * @return number of days off
   */
  public int getDaysOff() {
    return daysOff;
  }
}
