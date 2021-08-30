package io.github.cchristou3.CyParking.ui.components

import android.content.Intent
import android.net.Uri
import androidx.viewbinding.ViewBinding
import io.github.cchristou3.CyParking.R
import mumayank.com.airlocationlibrary.AirLocation

/**
 * A simple [BaseFragment] subclass.
 * Encapsulates logic for retrieving the user's device's location.
 *
 * @author Charalambos Christou
 * @since 01/08/21
 */
abstract class LocationFragment<T : ViewBinding> : NavigatorFragment<T>() {

    private val airLocation by lazy {
        initializeLocationApi()
    }

    /**
     * Gets invoked after the user has been asked for a permission for a given package.
     * If permission was granted, request for the user's latest known location.
     *
     * @param requestCode  The code of the user's request.
     * @param permissions  The permission that were asked.
     * @param grantResults The results of the user's response.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) =
            airLocation.onRequestPermissionsResult(requestCode, permissions, grantResults)


    /**
     * Receive the result from a previous call to
     * [.startActivityForResult].
     *
     * @param requestCode The integer request code originally supplied to
     * startActivityForResult(), allowing you to identify who this
     * result came from.
     * @param resultCode  The integer result code returned by the child activity
     * through its setResult().
     * @param data        An Intent, which can return result data to the caller
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) =
            airLocation.onActivityResult(requestCode, resultCode, data)

    /**
     * Start listening for location updates
     */
    fun startLocationUpdates() = airLocation.start()

    /**
     * Returns an instance of [AirLocation] that contains
     * information about the location retrieval behaviour.
     * @return An instance of [AirLocation].
     */
    abstract fun initializeLocationApi(): AirLocation

    companion object {
        /**
         * Launch a Google Maps intent in which the given latitude and longitude
         * are set the center point of the map. Also, the point is given the
         * specified label.
         * If the device has no apps that support this operation, then a Toast
         * message is displayed.
         *
         * @param fragment   The fragment to make use of.
         * @param latitude  the center point of the map in the Y axis.
         * @param longitude the center point of the map in the X axis.
         * @param label     The label of the point in the map.
         */
        @JvmStatic
        fun <T : BaseFragment<S>, S : ViewBinding> launchGoogleMaps(fragment: T, latitude: Double, longitude: Double, label: String) {
            // Create Uri (query string) for a Google Maps Intent
            // Launch Google Maps activity
            Intent(Intent.ACTION_VIEW,
                    Uri.parse("geo:0,0?q=" + latitude
                            + "," + longitude + "("
                            + label
                            + ")")
            ).setPackage("com.google.android.apps.maps")
                    .let {
                        if (it.resolveActivity(fragment.requireContext().packageManager) != null) {
                            fragment.requireContext().startActivity(it)
                        } else {
                            fragment.globalStateViewModel.updateToastMessage(R.string.no_google_maps_app_found)
                        }
                    }
        }
    }
}