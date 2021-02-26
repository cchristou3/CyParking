package io.github.cchristou3.CyParking.utilities;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Purpose: <p>Contain all helper / utility methods that the application needs.</p>
 *
 * @author Charalambos Christou
 * @version 7.0 05/02/21
 */
public class Utility {

    // No instances. Static utilities only.
    private Utility() {
    }

    /**
     * Checks whether the given list contains the given element
     * via the use of {@link Comparable#compareTo(Object)}.
     *
     * @param aList A list containing elements of type {@link T}.
     * @param elem  The element to look for
     * @param <T>   any type
     * @return True, if an element was found in the list with the same contents
     * of the given element. Otherwise, false.
     */
    public static <T extends Comparable<T>> boolean contains(@NotNull List<T> aList, T elem) {
        for (T e : aList)
            if (e.compareTo(elem) == 0)
                return true;
        return false;
    }

    /**
     * Create a new list (new reference),
     * containing all the elements of the given list.
     *
     * @param list A simple list object.
     * @param <T>  any type
     * @return A fresh list containing all the elements
     * (of type {@link T}) of the given list.
     */
    @NotNull
    @Contract("_ -> new")
    public static <T> List<T> cloneList(List<T> list) {
        return new ArrayList<>(list);
    }

    /**
     * Create a list of that contains items of type {@link T}
     * based on the given {@link QuerySnapshot} object.
     *
     * @param value  The {@link QuerySnapshot} object containing all the user's bookings.
     * @param tClass the class of {@link T}
     * @param <T>    any type
     * @return A {@link List} of {@link T} objects.
     */
    @NotNull
    public static <T> List<T> getListOf(@NotNull QuerySnapshot value, Class<T> tClass) {
        return value.toObjects(tClass);
    }

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
}
