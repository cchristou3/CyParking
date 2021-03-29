package io.github.cchristou3.CyParking.ui.views.home

import androidx.core.util.Consumer
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.cchristou3.CyParking.R
import io.github.cchristou3.CyParking.apiClient.model.data.parking.slot.booking.Booking
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

    private val mUpcomingBookingState = MutableLiveData<Booking>()

    private val mHideBooking = MutableLiveData<Any>()

    var upcomingBooking: LiveData<Booking> = mUpcomingBookingState
        private set
    var hideBooking: LiveData<Any> = mHideBooking
        private set

    fun getUpcomingBooking(userId: String, activity: FragmentActivity, displayToast: Consumer<Int>) {
        mUserRepository.getUpcomingBooking(userId).get()
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) return@addOnCompleteListener
                    task.result?.let { querySnapshot ->
                        if (querySnapshot.documents.isEmpty()) return@addOnCompleteListener
                        val currentBooking = querySnapshot.documents[0].toObject(Booking::class.java)
                        mUserRepository.bookingsRef.document(querySnapshot.documents[0].id)
                                .addSnapshotListener(activity) { value, _ ->
                                    value?.let { documentSnapshot ->
                                        if (!documentSnapshot.exists()) return@addSnapshotListener
                                        documentSnapshot.toObject(Booking::class.java)
                                                ?.let {
                                                    mUpcomingBookingState.value = it
                                                    if (it.isCompleted // If the current booking got completed
                                                            && currentBooking?.generateDocumentId()
                                                            == it.generateDocumentId() && !currentBooking?.isCompleted!!) {
                                                        // Trigger observer update
                                                        mHideBooking.value = null
                                                        displayToast.accept(R.string.booking_completed)
                                                    }
                                                }
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