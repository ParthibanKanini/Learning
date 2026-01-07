package pc.ado;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class DateUtilsTest {

    @Test
    public void testDaysBetween_SameDates() {
        // When start and end dates are the same, should return 1
        String startDate = "2024-01-15T10:00:00+00:00";
        String endDate = "2024-01-15T10:00:00+00:00";

        String result = DateUtils.daysBetween(startDate, endDate);

        assertEquals("1", result);
    }

    @Test
    public void testDaysBetween_OneDayDifference() {
        // One day difference should return 2 (start day + 1)
        String startDate = "2024-01-15T10:00:00+00:00";
        String endDate = "2024-01-16T10:00:00+00:00";

        String result = DateUtils.daysBetween(startDate, endDate);

        assertEquals("2", result);
    }

    @Test
    public void testDaysBetween_FewDaysDifference() {
        // Few days difference should return correct count
        String startDate = "2025-05-16T00:00:00Z";
        String endDate = "2025-05-29T00:00:00Z";

        String result = DateUtils.daysBetween(startDate, endDate);

        assertEquals("14", result);
    }

    @Test
    public void testDaysBetween_MultipleDaysDifference() {
        // Multiple days difference
        String startDate = "2024-01-15T00:00:00+00:00";
        String endDate = "2024-01-20T00:00:00+00:00";

        String result = DateUtils.daysBetween(startDate, endDate);

        assertEquals("6", result);
    }

    @Test
    public void testDaysBetween_InvalidDateFormat() {
        // Invalid date format should return "0"
        String startDate = "invalid-date";
        String endDate = "also-invalid";

        String result = DateUtils.daysBetween(startDate, endDate);

        assertEquals("0", result);
    }

    @Test
    public void testDaysBetween_PartiallyInvalidDate() {
        // One invalid date should return "0"
        String startDate = "2024-01-15T10:00:00+00:00";
        String endDate = "not-a-date";

        String result = DateUtils.daysBetween(startDate, endDate);

        assertEquals("0", result);
    }

    // Test cases for calculateWeekDays method
    @Test
    public void testCalculateWeekDays_SameDateNoHolidays() {
        // Same date should return 1 (one work day)
        LocalDate date = LocalDate.of(2024, 1, 15); // Monday

        int result = DateUtils.calculateWeekDays(date, date);

        assertEquals(1, result);
    }

    @Test
    public void testCalculateWeekDays_SameDateWithHolidays() {
        // Same date with holiday should return 0
        LocalDate date = LocalDate.of(2024, 1, 15); // Monday

        int result = DateUtils.calculateWeekDays(date, date, 1);

        assertEquals(0, result);
    }

    @Test
    public void testCalculateWeekDays_OneDayDifference() {
        // One day difference (Monday to Tuesday) = 2 work days
        LocalDate start = LocalDate.of(2024, 1, 15); // Monday
        LocalDate finish = LocalDate.of(2024, 1, 16); // Tuesday

        int result = DateUtils.calculateWeekDays(start, finish);

        assertEquals(2, result);
    }

    @Test
    public void testCalculateWeekDays_FullWeek() {
        // Full week (Monday to Friday) = 5 work days
        LocalDate start = LocalDate.of(2024, 1, 15); // Monday
        LocalDate finish = LocalDate.of(2024, 1, 19); // Friday

        int result = DateUtils.calculateWeekDays(start, finish);

        assertEquals(5, result);
    }

    @Test
    public void testCalculateWeekDays_WeekIncludingWeekend() {
        // Monday to Sunday = 5 work days (Mon-Fri)
        LocalDate start = LocalDate.of(2024, 1, 15); // Monday
        LocalDate finish = LocalDate.of(2024, 1, 21); // Sunday

        int result = DateUtils.calculateWeekDays(start, finish);

        assertEquals(5, result);
    }

    @Test
    public void testCalculateWeekDays_WeekIncludingWeekendCust() {
        // Monday to Sunday = 5 work days (Mon-Fri)
        // Few days difference should return correct count
        String startDate = "2025-05-16T00:00:00Z";
        String endDate = "2025-05-29T00:00:00Z";
        LocalDate start = DateUtils.formatISODateToLocalDate(startDate); // Monday
        LocalDate finish = DateUtils.formatISODateToLocalDate(endDate); // Sunday

        int result = DateUtils.calculateWeekDays(start, finish);
        assertEquals(10, result);
    }

    @Test
    public void testCalculateWeekDays_TwoWeeks() {
        // Two full weeks (Monday to Friday of next week) = 10 work days
        LocalDate start = LocalDate.of(2024, 1, 15); // Monday
        LocalDate finish = LocalDate.of(2024, 1, 26); // Friday

        int result = DateUtils.calculateWeekDays(start, finish);

        assertEquals(10, result);
    }

    @Test
    public void testCalculateWeekDays_WithHolidaySubtraction() {
        // Full week with 1 holiday = 4 work days
        LocalDate start = LocalDate.of(2024, 1, 15); // Monday
        LocalDate finish = LocalDate.of(2024, 1, 19); // Friday

        int result = DateUtils.calculateWeekDays(start, finish, 1);

        assertEquals(4, result);
    }

    @Test
    public void testCalculateWeekDays_MultipleHolidaysSubtraction() {
        // Full week with 3 holidays = 2 work days
        LocalDate start = LocalDate.of(2024, 1, 15); // Monday
        LocalDate finish = LocalDate.of(2024, 1, 19); // Friday

        int result = DateUtils.calculateWeekDays(start, finish, 3);

        assertEquals(2, result);
    }

    @Test
    public void testCalculateWeekDays_HolidaysExceedingDays() {
        // Holidays exceed available days, should return 0 (not negative)
        LocalDate start = LocalDate.of(2024, 1, 15); // Monday
        LocalDate finish = LocalDate.of(2024, 1, 19); // Friday

        int result = DateUtils.calculateWeekDays(start, finish, 10);

        assertEquals(0, result);
    }

    @Test
    public void testCalculateWeekDays_StartOnWeekend() {
        // Start on Saturday (weekend day)
        LocalDate start = LocalDate.of(2024, 1, 20); // Saturday
        LocalDate finish = LocalDate.of(2024, 1, 22); // Monday

        int result = DateUtils.calculateWeekDays(start, finish);

        assertEquals(1, result); // Only Monday counts
    }
}
