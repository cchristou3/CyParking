package io.github.cchristou3.CyParking.data.manager.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import io.github.cchristou3.CyParking.R
import io.github.cchristou3.CyParking.data.interfaces.LocationHandler
import io.github.cchristou3.CyParking.data.manager.location.LocationManager.Companion.permissionList
import io.github.cchristou3.CyParking.ui.components.BaseFragment
import io.github.cchristou3.CyParking.utils.Utility.isPermissionGranted
import org.jetbrains.annotations.Contract

/**
 * Purpose: Responsible for acquiring the user's location
 * via a [FusedLocationProviderClient] object.
 * The received [LocationResult] is then handled by the [LocationHandler]
 * object.
 *
 *
 * Performs checks on the bellow permissions ([permissionList]):
 *
 *  * [Manifest.permission.ACCESS_FINE_LOCATION]
 *  * [Manifest.permission.ACCESS_COARSE_LOCATION]
 *
 * and requests them if not already granted to allow the utilization
 * of the [FusedLocationProviderClient.requestLocationUpdates] method.
 *
 *  In all its subclasses the received LocationResult is handled by
 * the implementation of the LocationHandler provided by the fragment/activity.
 *
 * @author Charalambos Christou
 * @version 6.0 27/03/21
 * @param T Any subclass of [BaseFragment].
 * @param S Any subclass of [ViewBinding].
 */
abstract class LocationManager<T : BaseFragment<S>, S : ViewBinding> internal constructor(context: Context) {

    // Data members
    private val mFusedLocationProviderClient: FusedLocationProviderClient = ServiceLocator.getInstance(context).locationProviderClient
    private var mLocationCallback // Save a reference to it, so that we can remove it afterwards
            : LocationCallback? = null

    /**
     * Check whether the device is ready to perform location based requests.
     * @param fragment the fragment to make use of.
     */
    private fun checkLocationService(fragment: T) {
        Log.d(TAG, "checkLocationService")
        testLocationServiceTask(fragment.requireActivity(), SingleUpdateHelper.getRequest(), SubsequentUpdateHelper.getRequest())
                .addOnSuccessListener(fragment.requireActivity()) {
                    onLocationServiceTestSuccess(fragment, it)
                }
                .addOnFailureListener(fragment.requireActivity()) {
                    onLocationServiceTestFailure(fragment, it)
                }
    }

    /**
     * Handle the success of the [LocationSettingsResponse].
     * @param fragment the fragment to make use of.
     * @param locationSettingsResponse the response to the location service settings task.
     */
    private fun onLocationServiceTestSuccess(fragment: T, locationSettingsResponse: LocationSettingsResponse) {
        logLocationSettings("onSuccess", locationSettingsResponse.locationSettingsStates)
        isReadyToRequestLocation = true
        requestUserLocationUpdates(fragment)
    }

    /**
     * Handle the exception thrown in [checkLocationService].
     * If the exception is of type [ResolvableApiException]
     * it means that the device has its location service turned off.
     * In this case, the user is prompted to enable it.
     *
     * @param fragment the fragment to make use of.
     * @param exception the reason why the [LocationSettingsResponse] failed.
     */
    private fun onLocationServiceTestFailure(fragment: T, exception: Exception) {
        Log.d(TAG, "onFailure: " + exception.message)
        if (exception is ApiException) {
            when (exception.statusCode) {
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
                    // Location settings are not satisfied. But could be fixed by showing the
                    // user a dialog.
                    try {
                        // Cast to a resolvable exception.
                        val resolvable = exception as ResolvableApiException
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        resolvable.startResolutionForResult(
                                fragment.requireActivity(),
                                RC_CHECK_SETTINGS)
                    } catch (sie: IntentSender.SendIntentException) {
                        // Ignore the error.
                    } catch (cce: ClassCastException) {
                        // Ignore, should be an impossible error.
                    }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    // Location settings are not satisfied. However, we have no way to fix the
                    // settings so we won't show the dialog.
                }
            }
        }
    }

    /**
     * Requests the user's latest known location.
     * This procedure is wrapped around appropriate permission
     * checks to avoid requesting the user's location without
     * his permission.
     *
     * @param fragment The fragment that requests the location-based permissions.
     */
    fun requestUserLocationUpdates(fragment: T) {
        // Check for location permissions
        if (permissionList.any {
                    ActivityCompat.checkSelfPermission(fragment.requireContext(), it) == PackageManager.PERMISSION_GRANTED
                }.not()
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // Marshmallow
                fragment.requestPermissions(
                        permissionList.toTypedArray(),
                        RC_LOCATION_PERMISSION)
            }
        } else {
            // Permission granted
            requestLocation(fragment)
        }
    }

    /**
     * To be called in [Fragment.onRequestPermissionsResult]
     * to handle the result of the requested permissions.
     * If the permission was granted, then proceed using location-based operations.
     * Otherwise, display an informative message to the user.
     *
     * @param fragment     Where to display the message.
     * @param requestCode  The integer corresponding to our request.
     * @param grantResults The result of the permission request.
     */
    fun onRequestPermissionsResult(fragment: T, requestCode: Int, grantResults: IntArray) {
        if (requestCode == RC_LOCATION_PERMISSION) {
            if (isPermissionGranted(grantResults)) {
                // Permission granted
                // Proceed on doing something
                requestLocation(fragment)
            } else { // if permission is not granted
                // decide what you want to do if you don't get permissions
                fragment.globalStateViewModel.updateToastMessage(R.string.permission_not_granted)
                performErrorCleanUp(fragment)
            }
        }
    }

    /**
     * Callback method called in response to [ResolvableApiException.startResolutionForResult]
     * in [LocationManager.onLocationServiceTestFailure].
     * Handle the response of the user.
     * This method should be called in [Fragment.onActivityResult] or [Activity.onActivityResult]
     * depending who is requesting for location updates.
     *
     * @param fragment The fragment to make use of.
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     */
    fun onActivityResult(fragment: T, requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != RC_CHECK_SETTINGS) return

        if (data != null) {
            // Log the response
            LocationSettingsStates.fromIntent(data)?.let { logLocationSettings("onActivityResult", it) }
        }
        Log.d(TAG, "onActivityResult <> LocationManager")
        when (resultCode) {
            Activity.RESULT_OK -> {
                // All required changes were successfully made
                requestUserLocationUpdates(fragment)
            }
            Activity.RESULT_CANCELED -> {
                // The user was asked to change settings, but chose not to
                performErrorCleanUp(fragment)
            }
        }
    }


    /**
     * Log crucial information about the given [LocationSettingsStates].
     *
     * @param where the name of the method that is invoking this one.
     * @param states the location settings states.
     */
    private fun logLocationSettings(where: String, states: LocationSettingsStates) {
        Log.d(TAG, "$where <> isGpsPresent?  ${states.isGpsPresent}")
        Log.d(TAG, "$where <> isGpsUsable?  ${states.isGpsUsable}")
        Log.d(TAG, "$where <> isLocationPresent?  ${states.isLocationPresent}")
        Log.d(TAG, "$where <> isLocationUsable?  ${states.isLocationUsable}")
        Log.d(TAG, "$where <> isNetworkLocationPresent?  ${states.isNetworkLocationPresent}")
        Log.d(TAG, "$where <> isNetworkLocationUsable?  ${states.isNetworkLocationUsable}")
    }

    /**
     * Clean up resources whenever an error occurs.
     * @param fragment any subclass of [BaseFragment].
     */
    abstract fun performErrorCleanUp(fragment: T)

    /**
     * Send a single high-priority request concerning the user's
     * location. Handle the result with the given callback method.
     *
     * @param fragment The [BaseFragment] instance to make use of.
     */
    @SuppressLint("MissingPermission")
    private fun requestLocation(fragment: T) {
        if (isInFlightMode(fragment.requireContext().applicationContext)) {
            // Display a toast
            fragment.globalStateViewModel.updateToastMessage(R.string.in_flight_mode_message)
            performErrorCleanUp(fragment)
            return
        }
        Log.d(TAG, "isReadyToRequestLocation <> $isReadyToRequestLocation")
        if (!isReadyToRequestLocation) {
            checkLocationService(fragment)
            return
        }
        // Do some extra preparation here
        beforeRequests(fragment)

        Log.d(TAG, "requestLocation <> Already checked!")
        mFusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                mLocationCallback,
                Looper.getMainLooper())
    }

    /**
     * Callback to be invoked just before the device starts requesting
     * for the user's location.
     * @param fragment An instance of [BaseFragment].
     */
    abstract fun beforeRequests(fragment: T)

    /**
     * Assigns the value of [.mLocationCallback]
     * with the given argument.
     *
     * @param locationCallback The new value of [.mLocationCallback]
     */
    fun setLocationCallback(locationCallback: LocationCallback?) {
        mLocationCallback = locationCallback
    }

    /**
     * Unregisters the [.mFusedLocationProviderClient] from future
     * location updates.
     */
    fun removeLocationUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback)
        Log.d(TAG, "Callback removed!")
    }

    /**
     * Returns an instance of [LocationRequest].
     * To be overridden by its subclasses.
     * Each subclass can customise its [LocationRequest] to cover its needs.
     *
     * @return an instance of [LocationRequest].
     * @see LocationRequest
     */
    abstract val locationRequest: LocationRequest

    companion object {
        // Constant variables
        const val RC_LOCATION_PERMISSION = 321
        private val TAG = LocationManager::class.java.name + "UniqueTag"
        const val RC_CHECK_SETTINGS = 534
        const val RC_USER_MANUAL_PERMISSION_CHECK = 15

        private var isReadyToRequestLocation = false

        val permissionList = listOf(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION)

        /**
         * Create a new instance of [SingleUpdateHelper].
         *
         * @param fragment the fragment to make use of.
         * @param locationHandler The handler for the location result.
         * @param errorCallback a method to get triggered whenever an error occurs.
         * @param locationServiceError a method to get triggered when a location
         * service error occurs.
         * @return an instance of [SingleUpdateHelper].
         */
        @JvmStatic
        @Contract("_, _ -> new")
        fun <T : BaseFragment<S>, S : ViewBinding> createSingleUpdateHelper(
                fragment: T, locationHandler: LocationHandler,
                errorCallback: Runnable?, locationServiceError: Runnable
        )
                : SingleUpdateHelper<T, S> = SingleUpdateHelper<T, S>(fragment, locationHandler, errorCallback, locationServiceError)


        /**
         * Create a new instance of [SubsequentUpdateHelper].
         *
         * @param fragment        The fragment to subscribe the location updates to.
         * @param locationHandler The handler for the location result.
         * @return an instance of [SubsequentUpdateHelper].
         */
        @JvmStatic
        @Contract("_, _, _ -> new")
        fun <T : BaseFragment<S>, S : ViewBinding> createSubsequentUpdateHelper(
                fragment: T, locationHandler: LocationHandler
        ): SubsequentUpdateHelper<T, S> = SubsequentUpdateHelper<T, S>(fragment, locationHandler)


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

        /**
         * Create a task for testing the location services.
         *
         * @param fragmentActivity the activity to make use of.
         * @param locationRequests one or more [LocationRequest] objects.
         * @return A [Task] holding the [LocationSettingsResponse].
         */
        @JvmStatic
        fun testLocationServiceTask(fragmentActivity: FragmentActivity, vararg locationRequests: LocationRequest): Task<LocationSettingsResponse> {
            val builder = LocationSettingsRequest.Builder()
            for (request in locationRequests) {
                builder.addLocationRequest(request)
            }
            val settingsClient = LocationServices.getSettingsClient(fragmentActivity)
            return settingsClient.checkLocationSettings(builder.build())
        }

        /**
         * Open the application's permission settings.
         * The activity can check whether the user navigated back to it
         * from the settings by checking if the request code on [Activity.onActivityResult]
         * is the same as [RC_USER_MANUAL_PERMISSION_CHECK].
         *
         * @param activity the activity to make use of.
         */
        @JvmStatic
        fun openAppPermissionSettings(activity: Activity) {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.fromParts("package", activity.packageName, null)
            activity.startActivityForResult(intent, RC_USER_MANUAL_PERMISSION_CHECK)
        }

        /**
         * Check whether the device is in flight mode.
         * @return True if the device has flight mode turned on. Otherwise, false.
         * @param appContext the application context to make use of.
         */
        @JvmStatic
        fun isInFlightMode(appContext: Context): Boolean {
            return Settings.Global.getInt(
                    appContext.applicationContext.contentResolver,
                    Settings.Global.AIRPLANE_MODE_ON, 0
            ) != 0
        }
    }

}