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
public class ViewParkingViewModel extends ViewModel {

    // TODO: Bind with ViewBookingFragment & initialize livedata here
    private MutableLiveData<List<PrivateParkingBooking>> listMutableLiveData;

    public MutableLiveData<List<PrivateParkingBooking>> getListMutableLiveData() {
        return listMutableLiveData;
    }
}
