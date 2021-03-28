package io.github.cchristou3.CyParking.utils;

import android.content.pm.PackageManager;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

/**
 * Purpose: <p>Contain all helper / utility methods that the application needs.</p>
 *
 * @author Charalambos Christou
 * @version 8.0 27/03/21
 */
public class Utility {

    // No instances. Static utilities only.
    private Utility() {
    }

    /**
     * Access the currency of the {@link Locale#GERMANY} (Euro).
     *
     * @return the currency instance of Euro.
     */
    public static Currency getCurrency() {
        return Currency.getInstance(Currency.getInstance(Locale.GERMANY).getCurrencyCode());
    }

    /**
     * Checks whether the given list contains the given element
     * via the use of {@link Comparable#compareTo(Object)}.
     *
     * @param aList A list containing elements of type {@link T}.
     * @param elem  The element to look for
     * @param <T>   any type that implements {@link Comparable}.
     * @return The index of the element in the list if found. Otherwise, -1.
     */
    public static <T extends Comparable<T>> boolean contains(@NotNull List<T> aList, T elem) {
        for (T e : aList)
            if (e.compareTo(elem) == 0)
                return true;
        return false;
    }

    /**
     * Find the index of the given element within the specified list
     * via the use of {@link Comparable#compareTo(Object)}.
     *
     * @param aList A list containing elements of type {@link T}.
     * @param elem  The element to look for
     * @param <T>   any type that implements {@link Comparable}.
     * @return True, if an element was found in the list with the same contents
     * of the given element. Otherwise, false.
     */
    public static <T extends Comparable<T>> int indexOf(@NotNull List<T> aList, T elem) {
        for (int i = 0; i < aList.size(); i++) {
            T a = aList.get(i);
            if (a.compareTo(elem) == 0)
                return i;
        }
        return -1;
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
     * Checks whether a permission was granted with the
     * given grantResults.
     *
     * @param grantResults The results of a permission request.
     * @return True if permission is granted. Otherwise, false.
     */
    @Contract(pure = true)
    public static boolean isPermissionGranted(@NotNull int[] grantResults) {
        return grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }
}
