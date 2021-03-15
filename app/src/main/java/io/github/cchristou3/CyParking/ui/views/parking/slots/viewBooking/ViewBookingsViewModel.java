package io.github.cchristou3.CyParking.ui.views.parking.slots.viewBooking;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.apiClient.model.data.parking.slot.booking.Booking;
import io.github.cchristou3.CyParking.apiClient.remote.repository.BookingRepository;
import io.github.cchristou3.CyParking.apiClient.utils.Utils;
import io.github.cchristou3.CyParking.ui.components.ToastViewModel;

/**
 * Purpose: <p>Data persistence when configuration changes.
 * Used when the users try to view their bookings.</p>
 *
 * @author Charalambos Christou
 * @version 3.0 28/02/21
 */
public class ViewBookingsViewModel extends ToastViewModel {

    private static final String TAG = ViewBookingsViewModel.class.getName();
    // Data member
    private final MutableLiveData<List<Booking>> mBookingList =
            new MutableLiveData<>();
    private final BookingRepository mBookingRepository;
    private boolean mWasDataLoaded = false;

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
     * that are not completed. Also, if this method is called
     * more than one time, then the remaining calls are ignored.
     *
     * @param userId The id of the Firebase user.
     */
    public void getUserBookings(String userId) {
        if (!mWasDataLoaded) {
            this.mBookingRepository.getUserBookings(userId).get()
                    .addOnCompleteListener(task -> {
                        final Exception error = task.getException();
                        final QuerySnapshot value = task.getResult();
                        if (error != null || value == null) { // Check whether an error occurred
                            updateToastMessage(R.string.load_booking_failed);
                            return;
                        }

                        List<Booking> bookings = Utils.getListOf(value, Booking.class);
                        Log.d(TAG, "New Snapshot success: " + bookings.size());
                        // - Update the booking list state with the newly created booking list
                        updateBookingList(bookings);
                        mWasDataLoaded = true;
                    });
        }
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
