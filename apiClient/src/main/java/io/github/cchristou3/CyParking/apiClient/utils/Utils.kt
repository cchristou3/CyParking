@file:JvmName("Utils")
@file:JvmMultifileClass

package io.github.cchristou3.CyParking.apiClient.utils

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.QuerySnapshot

/**
 * Create a list of that contains items of type [T]
 * based on the given [QuerySnapshot] object.
 *
 * @param value  The [QuerySnapshot] object containing all the user's bookings.
 * @param tClass the class of [T]
 * @param <T>    any type
 * @return A [List] of [T] objects.
</T> */
fun <T> getListOf(value: QuerySnapshot, tClass: Class<T>): List<T> {
    return value.toObjects(tClass)
}

/**
 * Calculates the distance between the two given [LatLng] objects.
 *
 * @param latLng1 A [LatLng] object.
 * @param latLng2 A [LatLng] object.
 * @return The distance between the two given [LatLng] objects in meters.
 */
fun getDistanceApart(latLng1: LatLng, latLng2: LatLng): Double {
    // Calculate the distance between the two points (User and current parking)
    // Reference: http://www.movable-type.co.uk/scripts/latlong.html
    val R = 6371e3 // metres
    val phi1: Double = latLng1.latitude * Math.PI / 180 // φ, λ in radians
    val phi2: Double = latLng2.latitude * Math.PI / 180
    val deltaPhi: Double = (latLng2.latitude - latLng1.latitude) * Math.PI / 180
    val deltaLambda: Double = (latLng2.longitude - latLng1.longitude) * Math.PI / 180
    val a = Math.sin(deltaPhi / 2) * Math.sin(deltaPhi / 2) +
            Math.cos(phi1) * Math.cos(phi2) *
            Math.sin(deltaLambda / 2) * Math.sin(deltaLambda / 2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    val d: Double // d is the total distance in metres
    d = R * c
    return d
}