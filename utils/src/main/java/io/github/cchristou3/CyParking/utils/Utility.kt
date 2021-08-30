package io.github.cchristou3.CyParking.utils

import android.content.pm.PackageManager
import android.os.Looper
import org.jetbrains.annotations.Contract
import java.util.*

/**
 * Purpose:
 *
 * Contain all helper / utility methods that the application needs.
 *
 * @author Charalambos Christou
 * @version 10.0 30/08/21
 */
object Utility {
    /**
     * Check whether the calling thread is the main thread.
     *
     * @return true, if the calling thread is the main thread. Otherwise, false.
     */
    val isInMainThread: Boolean
        get() = Looper.myLooper() == Looper.getMainLooper()

    /**
     * Access the currency of the [Locale.GERMANY] (Euro).
     *
     * @return the currency instance of Euro.
     */
    @JvmStatic
    val currency: Currency
        get() = Currency.getInstance(Currency.getInstance(Locale.GERMANY).currencyCode)

    /**
     * Checks whether the given list contains the given element
     * via the use of [Comparable.compareTo].
     *
     * @param aList A list containing elements of type [T].
     * @param elem  The element to look for
     * @param <T>   any type that implements [Comparable].
     * @return The index of the element in the list if found. Otherwise, -1.
    </T> */
    @JvmStatic
    fun <T : Comparable<T>?> contains(aList: List<T>, elem: T): Boolean {
        for (e in aList) if (e!!.compareTo(elem) == 0) return true
        return false
    }

    /**
     * Find the index of the given element within the specified list
     * via the use of [Comparable.compareTo].
     *
     * @param aList A list containing elements of type [T].
     * @param elem  The element to look for
     * @param <T>   any type that implements [Comparable].
     * @return True, if an element was found in the list with the same contents
     * of the given element. Otherwise, false.
    </T> */
    fun <T : Comparable<T>?> indexOf(aList: List<T>, elem: T): Int {
        for (i in aList.indices) {
            val a = aList[i]
            if (a!!.compareTo(elem) == 0) return i
        }
        return -1
    }

    /**
     * Create a new list (new reference),
     * containing all the elements of the given list.
     *
     * @param list A simple list object.
     * @param <T>  any type
     * @return A fresh list containing all the elements
     * (of type [T]) of the given list.
     */
    @JvmStatic
    @Contract("_ -> new")
    fun <T> cloneList(list: List<T>?): MutableList<T> {
        return ArrayList(list.orEmpty()).toMutableList()
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
    @JvmStatic
    @Throws(IllegalArgumentException::class)
    fun getVolume(multiplicand: Float, startFrom: Int, endTo: Int /* non-inclusive */): Array<String?> {
        require(endTo > 0) { "Parameter endTo must be greater than 0" }
        val volumes = arrayOfNulls<String>(endTo)
        var multiplier = startFrom
        for (i in 0 until endTo) {
            volumes[i] = (multiplier * multiplicand).toString()
            multiplier++
        }
        return volumes
    }

    /**
     * Checks whether a permission was granted with the
     * given grantResults.
     *
     * @param grantResults The results of a permission request.
     * @return True if permission is granted. Otherwise, false.
     */
    @Contract(pure = true)
    fun isPermissionGranted(grantResults: IntArray): Boolean {
        return grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
    }
}