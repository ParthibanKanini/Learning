package pc.ado.dto;

/**
 * Data Transfer Object for team member allocation within an iteration.
 *
 * <p>Contains allocation details including capacity, days off, and worked hours.
 */
public class TeamMemberAllocation {

  private final String name;
  private final double capacity;
  private final int daysOff;
  private final int workedDays;
  private final double workedHours;

  public TeamMemberAllocation(
      final String name,
      final double capacity,
      final int daysOff,
      final int workedDays,
      final double workedHours) {
    this.name = name;
    this.capacity = capacity;
    this.daysOff = daysOff;
    this.workedDays = workedDays;
    this.workedHours = workedHours;
  }

  public String getName() {
    return name;
  }

  public double getCapacity() {
    return capacity;
  }

  public int getDaysOff() {
    return daysOff;
  }

  public int getWorkedDays() {
    return workedDays;
  }

  public double getWorkedHours() {
    return workedHours;
  }
}
