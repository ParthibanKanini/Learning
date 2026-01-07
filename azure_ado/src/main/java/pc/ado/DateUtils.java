package pc.ado;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtils {

    private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);

    /**
     * Formats an ISO date string to dd-MMM-yyyy format.
     *
     * @param date
     * @return
     */
    public static String formatISODate(String date) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
            return dateTime.format(formatter);
        } catch (Exception e) {
            logger.warn("Failed to format date: {}", date, e);
            return date;
        }
    }

    /**
     * Calculates work days between two dates, excluding holidays.
     *
     * @param start LocalDate
     * @param finish LocalDate
     * @return
     */
    public static int calculateWeekDays(LocalDate start, LocalDate finish) {
        return calculateWeekDays(start, finish, 0);
    }

    /**
     * Calculates work days between two dates, excluding holidays.
     *
     * @param start LocalDate
     * @param finish LocalDate
     * @param holidays number of holidays
     * @return
     */
    public static int calculateWeekDays(LocalDate start, LocalDate finish, int holidays) {
        logger.trace("Start Date {} - Finish Date {} - Holidays {}", start, finish, holidays);
        try {
            int workedDays = weekDaysBetween(start, finish) - holidays;
            logger.trace("Calculated worked days: {}", workedDays);
            return Math.max(0, workedDays); // Return at least 0
        } catch (Exception e) {
            logger.error("Failed to calculate work days from {} to {}", start, finish, e);
            return 0;
        }
    }

    /**
     *
     * @param startDate dd-MMM-yyyy
     * @return
     */
    public static LocalDate formatStringToLocalDate(String startDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        return LocalDate.parse(startDate, formatter);
    }

    public static LocalDate formatISODateToLocalDate(String startDate) {
        LocalDateTime dateTime = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return dateTime.toLocalDate();
    }

    /**
     * Calculates days between two ISO date strings. If the start and end dates
     * are same, returns 1
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static String daysBetween(String startDate, String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            LocalDateTime end = LocalDateTime.parse(endDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            // Ignore weekends and holidays calculation for now
            int days = (int) java.time.Duration.between(start, end).toDays() + 1;
            return String.valueOf(days);
            //return String.valueOf(java.time.Duration.between(start, end).toDays() + 1);
        } catch (Exception e) {
            logger.error("Failed to calculate days between: {} and {}", startDate, endDate, e);
            return "0";
        }
    }

    /**
     * Calculates the number of week days between two dates. Ignores Saturday &
     * Sunday.
     *
     * @param start the start date
     * @param finish the finish date
     * @return the count of week days
     */
    private static int weekDaysBetween(LocalDate start, LocalDate finish) {
        // Counting weekend days
        LocalDate current = start;
        int weekendDays = 0;
        while (!current.isAfter(finish)) {
            int dayOfWeek = current.getDayOfWeek().getValue();
            if (dayOfWeek == 6 || dayOfWeek == 7) {
                weekendDays++;
            }
            current = current.plusDays(1);
        }
        long totalDays = ChronoUnit.DAYS.between(start, finish) + 1;
        return (int) (totalDays - weekendDays);
    }

}
