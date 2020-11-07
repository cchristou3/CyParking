package io.github.cchristou3.CyParking.view.ui.viewBooking;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import io.github.cchristou3.CyParking.view.data.pojo.parking.booking.PrivateParkingBooking;

/**
 * Purpose: <p>Data persistence when orientation changes. Shared amongst all tab fragments.
 * Used when the users try to view their bookings.</p>
 *
 * @author Charalambos Christou
 * @version 1.0 05/11/20
 */
public class ViewBookingsViewModel extends ViewModel {

    final private MutableLiveData<List<PrivateParkingBooking>> bookingListMutableLiveData =
            new MutableLiveData<>();

    public MutableLiveData<List<PrivateParkingBooking>> getBookingListMutableLiveData() {
        return bookingListMutableLiveData;
    }
}
