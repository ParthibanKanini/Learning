package pc.ado;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Utility class for date manipulation and formatting operations. */
public final class DateUtils {

  private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);

  private DateUtils() {
    // Utility class - prevent instantiation
  }

  /**
   * Formats an ISO date string to dd-MMM-yyyy format.
   *
   * @param date the ISO formatted date string
   * @return formatted date string or original if formatting fails
   */
  public static String formatISODate(final String date) {
    try {
      final LocalDateTime dateTime =
          LocalDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
      final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
      return dateTime.format(formatter);
    } catch (Exception e) {
      logger.warn("Failed to format date: {}", date, e);
      return date;
    }
  }

  /**
   * Calculates work days between two dates, excluding weekends.
   *
   * @param start start date
   * @param finish finish date
   * @return number of weekdays between dates
   */
  public static int calculateWeekDays(final LocalDate start, final LocalDate finish) {
    return calculateWeekDays(start, finish, 0);
  }

  /**
   * Calculates work days between two dates, excluding weekends and holidays.
   *
   * @param start start date
   * @param finish finish date
   * @param holidays number of holidays to subtract
   * @return number of working days, minimum 0
   */
  public static int calculateWeekDays(
      final LocalDate start, final LocalDate finish, final int holidays) {
    logger.trace("Start Date {} - Finish Date {} - Holidays {}", start, finish, holidays);
    try {
      final int workedDays = countWeekDaysBetween(start, finish) - holidays;
      logger.trace("Calculated worked days: {}", workedDays);
      return Math.max(0, workedDays);
    } catch (Exception e) {
      logger.error("Failed to calculate work days from {} to {}", start, finish, e);
      return 0;
    }
  }

  /**
   * Parses a date string in dd-MMM-yyyy format to LocalDate.
   *
   * @param startDate date string in dd-MMM-yyyy format
   * @return parsed LocalDate
   */
  public static LocalDate formatStringToLocalDate(final String startDate) {
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    return LocalDate.parse(startDate, formatter);
  }

  /**
   * Converts ISO date string to LocalDate.
   *
   * @param startDate ISO formatted date string
   * @return LocalDate representation
   */
  public static LocalDate formatISODateToLocalDate(final String startDate) {
    final LocalDateTime dateTime =
        LocalDateTime.parse(startDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    return dateTime.toLocalDate();
  }

  /**
   * Calculates days between two ISO date strings.
   *
   * <p>If the start and end dates are same, returns 1.
   *
   * @param startDate start date in ISO format
   * @param endDate end date in ISO format
   * @return string representation of days between dates, "0" if error
   */
  public static String daysBetween(final String startDate, final String endDate) {
    try {
      final LocalDateTime start =
          LocalDateTime.parse(startDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
      final LocalDateTime end =
          LocalDateTime.parse(endDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
      final int days = (int) java.time.Duration.between(start, end).toDays() + 1;
      return String.valueOf(days);
    } catch (Exception e) {
      logger.error("Failed to calculate days between: {} and {}", startDate, endDate, e);
      return "0";
    }
  }

  /**
   * Counts the number of week days between two dates.
   *
   * <p>Ignores Saturday and Sunday.
   *
   * @param start the start date
   * @param finish the finish date
   * @return the count of week days
   */
  private static int countWeekDaysBetween(final LocalDate start, final LocalDate finish) {
    LocalDate current = start;
    int weekendDays = 0;
    while (!current.isAfter(finish)) {
      final int dayOfWeek = current.getDayOfWeek().getValue();
      if (dayOfWeek == 6 || dayOfWeek == 7) {
        weekendDays++;
      }
      current = current.plusDays(1);
    }
    final long totalDays = ChronoUnit.DAYS.between(start, finish) + 1;
    return (int) (totalDays - weekendDays);
  }

  /**
   * Returns a set of all weekdays (Monday-Friday) between two dates, inclusive.
   *
   * @param start start date
   * @param end end date
   * @return set of weekdays between dates, empty set if invalid input
   */
  public static Set<LocalDate> getWeekDaysBetween(final LocalDate start, final LocalDate end) {
    final Set<LocalDate> days = new HashSet<>();
    if (start == null || end == null || end.isBefore(start)) {
      return days;
    }
    LocalDate current = start;
    while (!current.isAfter(end)) {
      switch (current.getDayOfWeek()) {
        case MONDAY:
        case TUESDAY:
        case WEDNESDAY:
        case THURSDAY:
        case FRIDAY:
          days.add(current);
          break;
        default:
          break;
      }
      current = current.plusDays(1);
    }
    return days;
  }
}
