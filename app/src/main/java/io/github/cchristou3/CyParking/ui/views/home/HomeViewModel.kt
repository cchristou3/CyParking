package io.github.cchristou3.CyParking.ui.views.home

import android.util.Log
import androidx.core.util.Consumer
import androidx.lifecycle.LiveData
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import io.github.cchristou3.CyParking.R
import io.github.cchristou3.CyParking.ui.components.LocationServiceViewModel
import io.github.cchristou3.CyParking.ui.components.SingleLiveEvent

/**
 * Purpose: <p>Data persistence when configuration changes.
 * Also, handle business logic.</p>
 *
 * @author Charalambos Christou
 * @since 1.0 27/03/21
 */
class HomeViewModel : LocationServiceViewModel() {

    companion object {
        private val TAG = HomeViewModel::class.java.name
    }

    private val mNavigateToMap = SingleLiveEvent<LatLng>()

    val navigationToMap: LiveData<LatLng>
        get() = mNavigateToMap


    /**
     * Based on the given location result, either trigger an event to
     * navigate to the parking map fragment or display an error toast message.
     *
     * @param locationResult the location result of a request.
     * @param displayToast an interface for displaying toast messages.
     */
    fun navigateToMap(locationResult: LocationResult?, displayToast: Consumer<Int>) {
        Log.d(TAG, "onLocationResult: $locationResult")
        if (locationResult != null) {
            // Access the user's latest location
            // and navigate to the ParkingMapFragment
            mNavigateToMap.value = LatLng(
                    locationResult.lastLocation.latitude,
                    locationResult.lastLocation.longitude)
        } else {
            // Inform the user something wrong happened
            displayToast.accept(R.string.error_retrieving_location)
        }
    }
}