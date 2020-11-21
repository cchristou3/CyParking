package io.github.cchristou3.CyParking.view.utilities;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;

import io.github.cchristou3.CyParking.view.ui.ParkingMapFragment;

/**
 * Purpose: <p>Contain all helper / utility methods which the application needs.</p>
 *
 * @author Charalambos Christou
 * @version 1.0 29/10/20
 */
public class Utility {

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

        return (d <= ParkingMapFragment.MAXIMUM_METERS_FROM_USER);
    }

    /**
     * Creates a Bitmap object based on a specified drawable
     *
     * @param drawable the drawable to be placed on a bitmap
     * @return A bitmap used to indicate the user's location
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * Creates the time based on specified hours and minutes (E.g. "12 : 45")
     *
     * @param finalHours the hour of the day
     * @param minute     the minute(s) of the hour
     * @return A string of the format "__ : __" where _ is a digit.
     */
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
}
