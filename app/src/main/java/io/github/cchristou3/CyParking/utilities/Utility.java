package io.github.cchristou3.CyParking.utilities;

import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Purpose: <p>Contain all helper / utility methods which the application needs.</p>
 *
 * @author Charalambos Christou
 * @version 6.0 21/01/21
 */
public class Utility {

    // Static Constants
    private static final String DATE_PATTERN = "dd-MM-yyyy";

    /**
     * Checks whether the given number is an even number.
     *
     * @param number The number to be checked.
     * @return True if the number has no remainder when divided by two.
     * Otherwise, false.
     */
    public static boolean isEven(int number) {
        return number % 2 == 0;
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
     * Checks whether the hour is in the range of 0..23 (inclusive)
     * and that the minute is in the range of 0..59 (inclusive).
     *
     * @param hours  The hours of the day represented by an int.
     * @param minute The minute of the hour represented by an int.
     * @throws IllegalArgumentException When at least one of the fields are invalid.
     */
    static void checkIfFieldsValid(int hours, int minute) throws IllegalArgumentException {
        if (!(hours >= 0 && hours <= 23) // Hours check
                || !(minute >= 0 && minute <= 59))
            throw new IllegalArgumentException("The hours must be in range of 0..23 (inclusive)"
                    + " and the minutes in range of 0..59 (inclusive).");
    }

    /**
     * Creates a string representation of the specified date object.
     *
     * @param date The date to represent in a string.
     * @return The string version of the date in the dd-MM-yyyy format.
     */
    @NotNull
    public static String dateToString(@NotNull Date date) {
        return new SimpleDateFormat(DATE_PATTERN,
                Locale.getDefault())
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
        return new SimpleDateFormat(DATE_PATTERN, Locale.getDefault())
                .parse(date);
    }

    /**
     * Calculates the distance between the two given {@link LatLng} objects.
     *
     * @param latLng1 A {@link LatLng} object.
     * @param latLng2 A {@link LatLng} object.
     * @return The distance between the two given {@link LatLng} objects in meters.
     */
    public static double getDistanceApart(@NotNull LatLng latLng1, @NotNull LatLng latLng2) {
        // Calculate the distance between the two points (User and current parking)
        // Reference: http://www.movable-type.co.uk/scripts/latlong.html
        final double R = 6371e3; // metres
        double phi1 = latLng1.latitude * Math.PI / 180; // φ, λ in radians
        double phi2 = latLng2.latitude * Math.PI / 180;
        double deltaPhi = (latLng2.latitude - latLng1.latitude) * Math.PI / 180;
        double deltaLambda = (latLng2.longitude - latLng1.longitude) * Math.PI / 180;

        double a = Math.sin(deltaPhi / 2) * Math.sin(deltaPhi / 2) +
                Math.cos(phi1) * Math.cos(phi2) *
                        Math.sin(deltaLambda / 2) * Math.sin(deltaLambda / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d; // d is the total distance in metres
        d = R * c;

        return d;
    }

    /**
     * Creates the time based on specified hours and minutes (E.g. "12 : 45")
     *
     * @param finalHours the hour of the day
     * @param minute     the minute(s) of the hour
     * @return A string of the format "__ : __" where _ is a digit.
     * @throws IllegalArgumentException if the parameters are invalid.
     * @see #checkIfFieldsValid(int, int)
     */
    @NotNull
    @Contract(pure = true)
    public static String getTimeOf(int finalHours, int minute) {
        checkIfFieldsValid(finalHours, minute);
        return ((finalHours < 10) ? "0" : "") + finalHours + " : " + ((minute < 10) ? "0" : "") + minute;
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
     * Generates a sequence of numbers and stores them in an Array.
     * The number are in range of @param startFrom - @param endTo.
     *
     * @param multiplicand The number to multiply with every index (multiplier) of the array.
     * @param startFrom    The initial value of the multiplier.
     * @param endTo        The length of the array.
     * @return An array of string that holds numeric values.
     * @throws IllegalArgumentException If @param endTo is less or equal than 0.
     */
    @NotNull
    public static String[] getVolume(float multiplicand, int startFrom, int endTo /* non-inclusive */)
            throws IllegalArgumentException {
        if (endTo <= 0)
            throw new IllegalArgumentException("Parameter endTo must be greater than 0");
        final String[] volumes = new String[endTo];
        int multiplier = startFrom;
        for (int i = 0; i < endTo; i++) {
            volumes[i] = String.valueOf((multiplier * multiplicand));
            multiplier++;
        }
        return volumes;
    }

    /**
     * Computes the time of the day.
     *
     * @return A String which corresponds to the time (E.g. "12 : 30").
     */
    @NotNull
    public static String getCurrentTime() {
        // Access the current time of the day
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minute = Calendar.getInstance().get(Calendar.MINUTE);
        return (Utility.getTimeOf((hour), minute));
    }
}
