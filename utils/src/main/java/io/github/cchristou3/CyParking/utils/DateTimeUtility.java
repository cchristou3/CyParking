package io.github.cchristou3.CyParking.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Purpose: <p>Contain all helper / utility methods that the application needs
 * related to date and time.</p>
 *
 * @author Charalambos Christou
 * @version 2.0 29/03/21
 */
public class DateTimeUtility {

    // Static Constants
    private static final String DATE_PATTERN = "dd/MM/yyyy";

    // No instances. Static utilities only.
    private DateTimeUtility() {
    }

    /**
     * Checks whether the given date is the same or greater than the
     * current date.
     *
     * @param date The date to be compared.
     * @return True, if the above condition is true. Otherwise, false.
     */
    public static boolean isGreaterOrEqualToCurrentDate(Date date) {
        return getCurrentDate().compareTo(date) <= 0;
    }

    /**
     * Accesses the Calendar instance to create a Date object
     * with the given params.
     *
     * @param year  the year of a date
     * @param month the month of a year
     * @param day   the day of the month
     * @return A Date object based on specified year, month and day
     * @throws IllegalArgumentException if the parameters are not valid
     * @see #checkIfFieldsValid(int, int, int)
     */
    @NotNull
    public static Date getDateOf(int year, int month, int day) throws IllegalArgumentException {
        checkIfFieldsValid(year, month, day);
        final Calendar innerCalendar = Calendar.getInstance();
        innerCalendar.set(year,
                month - 1,// Months are zero-based in Calendar
                day, 0, 0, 0);
        innerCalendar.set(Calendar.MILLISECOND, 0);
        return innerCalendar.getTime();
    }

    /**
     * Creates a string representation of the date accumulated by the specified
     * year, month and day.
     *
     * @param year  The year of the date.
     * @param month The month of the date.
     * @param day   The day of the date.
     * @return The string version of the date in the dd-MM-yyyy format.
     * @throws IllegalArgumentException if the parameters are not valid
     * @see #checkIfFieldsValid(int, int, int)
     * @see #getDateOf(int, int, int)
     * @see #dateToString(Date)
     */
    @NotNull
    public static String dateToString(int year, int month, int day) throws IllegalArgumentException {
        checkIfFieldsValid(year, month, day);
        return dateToString(getDateOf(year, month, day));
    }

    /**
     * Checks whether the year is greater than 0,
     * the month is in the range of 1..12 (inclusive) and
     * that the day is in the range of 1..31 (inclusive).
     *
     * @param year  The year represented by an int (e.g. 2020).
     * @param month The month represented by an int (e.g. 1 -> January).
     * @param day   The day of the month represented by an int (1 -> first day if the month).
     * @throws IllegalArgumentException When at least one of the fields are invalid.
     */
    static void checkIfFieldsValid(int year, int month, int day) throws IllegalArgumentException {
        if (year <= 0 // Year check
                || month >= 12 || month < 0 // Month check
                || day > 31 || day <= 0) // Day check
            throw new IllegalArgumentException("Month must be in 1..12 (inclusive) and"
                    + " Day must be 1..31 range (inclusive)");
    }

    /**
     * Creates a string representation of the specified date object.
     *
     * @param date The date to represent in a string.
     * @return The string version of the date in the dd-MM-yyyy format.
     */
    @NotNull
    public static String dateToString(@NotNull Date date) {
        return getDateFormatter()
                .format(date);
    }

    /**
     * Creates a new instance of Date that corresponds
     * to the current date.
     * Note: The hours, minutes, seconds and milliseconds
     * are all set to 0.
     *
     * @return The current date.
     */
    @NotNull
    public static Date getCurrentDate() {
        final Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(Calendar.getInstance().getTime());
        currentDate.set(Calendar.HOUR_OF_DAY, 0);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.SECOND, 0);
        currentDate.set(Calendar.MILLISECOND, 0);
        return currentDate.getTime();
    }

    /**
     * Converts the given string into a date object.
     *
     * @param date The string to be converted to a Date object.
     * @return The representation of the string's date value into a Date object.
     * @throws ParseException When the given string cannot be parsed into a Date object.
     */
    public static Date fromStringToDate(String date) throws ParseException {
        return getDateFormatter()
                .parse(date);
    }

    /**
     * Check if the given time is valid based on the specific date.
     * It specifically checks for the following case:
     * Same date -> check times (ensure that the given time is not earlier
     * than the current time)
     * E.g. current 12/12/12 12:00, given 12/12/12 11:30
     * -> should return true
     *
     * @param pickedDateTime  the picked date.
     * @param selectedHours   The selected time's hours.
     * @param selectedMinutes The selected time's minutes.
     * @return True or false based on the above conditions.
     */
    public static boolean isInValidTime(Date pickedDateTime, int selectedHours, int selectedMinutes) {
        // Access current date
        Calendar currentDate = reset(Calendar.getInstance().getTime());

        // Access picked date without - time attributes
        final Calendar pickedDate = reset(pickedDateTime);

        // First check if the date is the same
        if (currentDate.compareTo(pickedDate) == 0) {
            // Now check for the time
            currentDate = Calendar.getInstance();
            currentDate.setTime(Calendar.getInstance().getTime());
            int currentHours = currentDate.get(Calendar.HOUR_OF_DAY);
            int currentMinutes = currentDate.get(Calendar.MINUTE);
            if (selectedHours < currentHours) return true;
            if (selectedHours == currentHours) {
                return selectedMinutes < currentMinutes;
            }
        }
        return false;
    }

    /**
     * Returns an instance of {@link SimpleDateFormat}
     * that parses dates into the following format
     * DD/MM/YYYY.
     *
     * @return An instance of {@link SimpleDateFormat}.
     * @see #DATE_PATTERN
     */
    @NotNull
    @Contract(" -> new")
    private static SimpleDateFormat getDateFormatter() {
        return new SimpleDateFormat(DATE_PATTERN, Locale.getDefault());
    }

    /**
     * Set all the attributes of the given date to 0
     * except of day, month and year.
     *
     * @param date the date tp reset.
     * @return a calendar object that has the same day, month and year
     * of the given date.
     */
    @NotNull
    private static Calendar reset(Date date) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }
}
