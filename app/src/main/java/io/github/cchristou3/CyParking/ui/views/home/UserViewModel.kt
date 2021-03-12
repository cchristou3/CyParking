package io.github.cchristou3.CyParking.ui.views.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.cchristou3.CyParking.apiClient.model.parking.slot.booking.Booking
import io.github.cchristou3.CyParking.apiClient.remote.repository.UserRepository

/**
 * Purpose: <p>Data persistence when configuration changes.
 * Used when the user is in [HomeFragment].</p>
 *
 * @author Charalambos Christou
 * @see 1.0 12/03/21
 * @constructor Initialize the ViewModel's repository.
 */
class UserViewModel(private val mUserRepository: UserRepository) : ViewModel() {

    private val upcomingBookingState = MutableLiveData<Booking>()

    var upcomingBooking: LiveData<Booking> = upcomingBookingState
        private set

    fun getUpcomingBooking(userId: String): Unit {
        mUserRepository.getUpcomingBooking(userId)
                .addSnapshotListener { value, error ->
                    value?.let { querySnapshot ->
                        if (querySnapshot.documents.isNotEmpty()) {
                            querySnapshot.documents[0]
                                    .toObject(Booking::class.java)?.let {
                                        upcomingBookingState.value = it
                                    }
                        }
                    }
                }
    }

    class Factory : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
                UserViewModel(UserRepository()) as T
            } else {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }

    }
}