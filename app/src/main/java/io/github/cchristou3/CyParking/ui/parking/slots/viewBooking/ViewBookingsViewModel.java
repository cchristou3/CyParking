package io.github.cchristou3.CyParking.ui.parking.slots.viewBooking;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import io.github.cchristou3.CyParking.data.pojo.parking.slot.booking.PrivateParkingBooking;

/**
 * Purpose: <p>Data persistence when orientation changes.
 * Used when the users try to view their bookings.</p>
 *
 * @author Charalambos Christou
 * @version 1.0 05/11/20
 */
public class ViewBookingsViewModel extends ViewModel {

    // Data member
    final private MutableLiveData<List<PrivateParkingBooking>> bookingList =
            new MutableLiveData<>();

    /**
     * Access the booking list's state
     *
     * @return The state of the booking list.
     */
    public MutableLiveData<List<PrivateParkingBooking>> getBookingList() {
        return bookingList;
    }
}
