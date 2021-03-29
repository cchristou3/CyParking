package io.github.cchristou3.CyParking.ui.views.parking.slots.bookingDetails

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.cchristou3.CyParking.apiClient.model.data.parking.lot.ParkingLot
import io.github.cchristou3.CyParking.apiClient.model.data.parking.slot.booking.Booking
import io.github.cchristou3.CyParking.apiClient.remote.repository.OperatorRepository

/**
 * Purpose: <p>Data persistence when configuration changes.
 * Used when the users try to view the details of one of their
 * bookings.</p>
 *
 * @author Charalambos Christou
 * @since 3.0 28/02/21
 */
class BookingDetailsViewModel(private val repository: OperatorRepository) : ViewModel() {

    private val mlotOfBooking = MutableLiveData<ParkingLot>()

    private val mIsCompleted = MutableLiveData<Boolean>()

    /**
     * A LiveData getter of [mlotOfBooking] to ensure that
     * its value cannot be changed outside of the ViewModel scope.
     * Whereas its setter is private.
     */
    var lotOfBooking: LiveData<ParkingLot> = mlotOfBooking
        private set

    var isCompleted: LiveData<Boolean> = mIsCompleted
        private set

    /**
     * Retrieves the [ParkingLot] associated with the given [Booking].
     */
    fun getLotOfBooking(booking: Booking) {
        repository.getParkingLot(booking.operatorId).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val lot = task.result?.documents?.get(0)?.toObject(ParkingLot::class.java)
                        lot?.let {
                            mlotOfBooking.value = it
                        }
                    }
                }
    }

    /**
     * Observe the booking that has as a document id the given bookingId.
     *
     * @param bookingId the document id of a booking
     * @param activity the activity to handle the eventlistener.
     */
    fun observeBookingStatus(bookingId: String?, activity: FragmentActivity) {
        repository.getBooking(bookingId)
                .addSnapshotListener(activity) { value, _ ->
                    if (value == null || !value.exists()) {
                        mIsCompleted.value = true // Does not exist. Do not allow the user to generate QR Code.
                        return@addSnapshotListener
                    }
                    val booking = value.toObject(Booking::class.java)
                    if (booking == null) {
                        mIsCompleted.value = true // Does not exist. Do not allow the user to generate QR Code.
                        return@addSnapshotListener
                    }
                    mIsCompleted.value = booking.isCompleted
                }

    }
}