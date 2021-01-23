package io.github.cchristou3.CyParking.ui.parking.slots.viewBooking;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.github.cchristou3.CyParking.data.model.parking.slot.booking.Booking;
import io.github.cchristou3.CyParking.data.repository.BookingRepository;

/**
 * Purpose: <p>Data persistence when orientation changes.
 * Used when the users try to view their bookings.</p>
 *
 * @author Charalambos Christou
 * @version 2.0 23/01/21
 */
public class ViewBookingsViewModel extends ViewModel {

    private static final String TAG = ViewBookingsViewModel.class.getName();

    // Data member
    private final MutableLiveData<List<Booking>> mBookingList =
            new MutableLiveData<>();

    private final BookingRepository mBookingRepository;

    /**
     * Initialize the ViewModel's BookingRepository instance
     * with the given argument.
     *
     * @param bookingRepository An BookingRepository instance.
     */
    public ViewBookingsViewModel(BookingRepository bookingRepository) {
        this.mBookingRepository = bookingRepository;
    }

    /**
     * Access the booking list's state
     *
     * @return The state of the booking list.
     */
    public LiveData<List<Booking>> getBookingListState() {
        return mBookingList;
    }

    /**
     * Access the booking list's state
     *
     * @return The state of the booking list.
     */
    public List<Booking> getBookingList() {
        return mBookingList.getValue();
    }


    /**
     * Sets the value of {@link #mBookingList} to the given argument.
     *
     * @param newBookingList The new value of {@link #mBookingList}.
     */
    public void updateBookingList(List<Booking> newBookingList) {
        mBookingList.setValue(newBookingList);
    }

    /**
     * Returns the bookings of the specified userId,
     * starting from the "Pending" ones and finishing with the "Completed" ones
     *
     * @param userId The is of the Firebase user
     * @return A query which returns all the bookings of the specified userId
     */
    @NotNull
    public Task<QuerySnapshot> getUserBookings(String userId) {
        return this.mBookingRepository.getUserBookings(userId).get();
    }

    /**
     * Deletes the specified document using the document ID
     *
     * @param idOfBookingToBeCancelled The id of the document which we want to delete
     */
    public void cancelParkingBooking(@NotNull String idOfBookingToBeCancelled) {
        // Delete the booking info to the database
        mBookingRepository.cancelParkingBooking(idOfBookingToBeCancelled);
    }
}
