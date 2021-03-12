package io.github.cchristou3.CyParking.apiClient.remote.repository

import com.google.firebase.firestore.Query
import io.github.cchristou3.CyParking.apiClient.remote.repository.DataSourceRepository.BookingHandler.Companion.BOOKING_DETAILS
import io.github.cchristou3.CyParking.apiClient.remote.repository.DataSourceRepository.BookingHandler.Companion.DATE_OF_BOOKING
import io.github.cchristou3.CyParking.apiClient.remote.repository.DataSourceRepository.BookingHandler.Companion.HOUR
import io.github.cchristou3.CyParking.apiClient.remote.repository.DataSourceRepository.BookingHandler.Companion.MINUTE
import io.github.cchristou3.CyParking.apiClient.remote.repository.DataSourceRepository.BookingHandler.Companion.STARTING_TIME

/**
 * Purpose: encapsulate logic for retrieving 'user'-specific data.
 * For now, it provides an interface to accessing the user's most
 * recent upcoming booking.
 *
 * @author Charalambos Christou
 * @since 12/03/21
 */
class UserRepository : DataSourceRepository.BookingHandler {

    companion object {
        const val TAG = "UserRepository"
    }

    /**
     * Returns the pending bookings of the user with the specified userId.
     *
     * @param userId The is of the Firebase user
     * @return A query which returns all the bookings of the specified userId
     */
    fun getUpcomingBooking(userId: String): Query = getUserUpcomingBookings(userId)
            .orderBy("$BOOKING_DETAILS.$DATE_OF_BOOKING", Query.Direction.ASCENDING)
            .orderBy("$BOOKING_DETAILS.$STARTING_TIME.$HOUR", Query.Direction.ASCENDING)
            .orderBy("$BOOKING_DETAILS.$STARTING_TIME.$MINUTE", Query.Direction.ASCENDING)
            .limit(1)
}