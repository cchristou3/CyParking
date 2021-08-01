package io.github.cchristou3.CyParking.ui.views.home

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import io.github.cchristou3.CyParking.ui.components.SingleLiveEvent

/**
 * Purpose: <p>Data persistence when configuration changes.
 * Also, handle business logic.</p>
 *
 * @author Charalambos Christou
 * @since 1.0 27/03/21
 */
class HomeViewModel : ViewModel() {

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
     * @param locations an list of the retrieved locations
     */
    fun navigateToMap(locations: ArrayList<Location>) {
        Log.d(TAG, "onLocationResult: $locations")
        locations[0].let {
            mNavigateToMap.value = LatLng(
                    it.latitude,
                    it.longitude)
        }
    }
}