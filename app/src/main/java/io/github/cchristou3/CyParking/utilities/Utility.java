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
 * @version 5.0 30/12/20
 */
public class Utility {

    // Static Constants
    private static final double MAXIMUM_METERS_FROM_USER = 1000.0D;
    private static final String DATE_PATTERN = "dd-MM-yyyy";

    /**
     * Creates a string representation of the date accumulated by the specified
     * year, month and day.
     *
     * @param year  The year of the date.
     * @param month The month of the date.
     * @param day   The day of the date.
     * @return The string version of the date in the dd-MM-yyyy format.
     */
    @NotNull
    public static String dateToString(int year, int month, int day) {
        return dateToString(Utility.getDateOf(year, month, day));
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
        return new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                .parse(date);
    }

    /**
     * Checks whether the specified lat and lng are inside the user's range
     *
     * @param userLatLng The lat and lng of the user
     * @param parkingLat The Lat of the private parking
     * @param parkingLng The Lng of the private parking
     * @return True, if yes. Otherwise, false.
     */
    public static boolean isNearbyUser(@NotNull LatLng userLatLng, double parkingLat, double parkingLng) {
        // Calculate the distance between the two points (User and current parking)
        // Reference: http://www.movable-type.co.uk/scripts/latlong.html
        final double R = 6371e3; // metres
        double phi1 = userLatLng.latitude * Math.PI / 180; // φ, λ in radians
        double phi2 = parkingLat * Math.PI / 180;
        double deltaPhi = (parkingLat - userLatLng.latitude) * Math.PI / 180;
        double deltaLambda = (parkingLng - userLatLng.longitude) * Math.PI / 180;

        double a = Math.sin(deltaPhi / 2) * Math.sin(deltaPhi / 2) +
                Math.cos(phi1) * Math.cos(phi2) *
                        Math.sin(deltaLambda / 2) * Math.sin(deltaLambda / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c; // d is the total distance in metres

        return (d <= MAXIMUM_METERS_FROM_USER);
    }

    /**
     * Creates the time based on specified hours and minutes (E.g. "12 : 45")
     *
     * @param finalHours the hour of the day
     * @param minute     the minute(s) of the hour
     * @return A string of the format "__ : __" where _ is a digit.
     */
    @NotNull
    @Contract(pure = true)
    public static String getTimeOf(int finalHours, int minute) {
        return "" + finalHours + " : " + ((minute < 10) ? "0" : "") + minute;
    }

    /**
     * Accesses the Calendar instance to create a Date object
     * with the given params.
     *
     * @param year  the year of a date
     * @param month the month of a year
     * @param day   the day of the month
     * @return A Date object based on specified year, month and day
     */
    @NotNull
    public static Date getDateOf(int year, int month, int day) {
        final Calendar innerCalendar = Calendar.getInstance();
        innerCalendar.set(Calendar.YEAR, year);
        innerCalendar.set(Calendar.MONTH, month);
        innerCalendar.set(Calendar.DAY_OF_MONTH, day);
        innerCalendar.set(Calendar.HOUR_OF_DAY, 0);
        innerCalendar.set(Calendar.MINUTE, 0);
        innerCalendar.set(Calendar.SECOND, 0);
        innerCalendar.set(Calendar.MILLISECOND, 0);
        return innerCalendar.getTime();
    }

    /**
     * Generates a sequence of numbers and stores them in an Array.
     * The number are in range of startFrom - endTo.
     *
     * @param multiplicand The number to multiply with every index (multiplier) of the array.
     * @return An array of string that holds numeric values.
     */
    @NotNull
    public static String[] getVolume(float multiplicand, int startFrom, int endTo /* non-inclusive */) {
        final String[] volumes = new String[10];
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
