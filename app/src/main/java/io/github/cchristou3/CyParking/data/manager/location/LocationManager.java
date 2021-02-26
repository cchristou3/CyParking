package io.github.cchristou3.CyParking.data.manager.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.data.interfaces.LocationHandler;

/**
 * Purpose: Responsible for acquiring the user's location
 * via a {@link FusedLocationProviderClient} object.
 * The received {@link LocationResult} is then handled by the {@link LocationHandler}
 * object.
 * <p>
 * Performs checks on the bellow permissions:
 * <ul>
 *      <li>{@link Manifest.permission#ACCESS_FINE_LOCATION}</li>
 *      <li>{@link Manifest.permission#ACCESS_COARSE_LOCATION}</li>
 * </ul>
 * and requests them if not already granted to allow the utilization
 * of the {@link FusedLocationProviderClient#requestLocationUpdates} method.
 * <p> In all its subclasses the received LocationResult is handled by
 *  the implementation of the LocationHandler provided by the fragment/activity.
 *
 * @author Charalambos Christou
 * @version 4.0 23/01/21
 */
public abstract class LocationManager {

    // Constant variables
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 321;
    private static final String TAG = LocationManager.class.getName() + "UniqueTag";

    // Data members
    private final FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationCallback mLocationCallback; // Save a reference so that we can remove it afterwards

    /**
     * Public Constructor.
     * Initializes the {@link #mFusedLocationProviderClient} object with the given context.
     *
     * @param context The context of the {@link #mFusedLocationProviderClient} to use.
     */
    /*package-private*/ LocationManager(@NonNull final Context context) {
        Log.d(TAG, "LocationManager initialized");
        mFusedLocationProviderClient = ServiceLocator.getInstance(context).getLocationProviderClient();
    }

    /**
     * Create a new instance of {@link SingleUpdateHelper}.
     *
     * @param context         The context to make use of.
     * @param locationHandler The handler for the location result.
     * @return an instance of {@link SingleUpdateHelper}.
     */
    @NotNull
    @Contract("_, _ -> new")
    public static SingleUpdateHelper createSingleUpdateHelper(@NonNull Context context, @NonNull LocationHandler locationHandler) {
        return new SingleUpdateHelper(context, locationHandler);
    }

    /**
     * Create a new instance of {@link SubsequentUpdateHelper}.
     *
     * @param context         The context to make use of.
     * @param fragment        The fragment to subscribe the location updates to.
     * @param locationHandler The handler for the location result.
     * @return an instance of {@link SubsequentUpdateHelper}.
     */
    @NotNull
    @Contract("_, _, _ -> new")
    public static SubsequentUpdateHelper createSubsequentUpdateHelper(
            @NonNull Context context, @NonNull Fragment fragment, @NonNull LocationHandler locationHandler
    ) {
        return new SubsequentUpdateHelper(context, fragment, locationHandler);
    }

    /**
     * Requests the user's latest known location.
     * This procedure is wrapped around appropriate permission
     * checks to avoid requesting the user's location without
     * his permission.
     *
     * @param fragment The fragment that requests the location-based permissions.
     */
    public void requestUserLocationUpdates(@NonNull Fragment fragment) {
        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(fragment.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(fragment.requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // Marshmallow

                fragment.requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);

            }
        } else {
            // no need to ask for permission
            // Do something... fetch position
            requestLocation();
        }
    }

    /**
     * To be called in {@link Fragment#onRequestPermissionsResult(int, String[], int[])}
     * to handle the result of the requested permissions.
     * If the permission was granted, then proceed using location-based operations.
     * Otherwise, display an informative message to the user.
     *
     * @param context      Where to display the message.
     * @param requestCode  The integer corresponding to our request.
     * @param grantResults The result of the permission request.
     */
    public void onRequestPermissionsResult(final Context context, int requestCode, @NotNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                // Proceed on doing something
                requestLocation();
            } else { // if permission is not granted
                // decide what you want to do if you don't get permissions
                Toast.makeText(context, "Permission is not granted!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Send a single high-priority request concerning the user's
     * location. Handle the result with the given callback method.
     */
    @SuppressLint("MissingPermission")
    private void requestLocation() {
        Log.d(TAG, "requestLocation");
        mFusedLocationProviderClient.requestLocationUpdates(
                getLocationRequest(),
                mLocationCallback,
                Looper.getMainLooper());
    }

    /**
     * Assigns the value of {@link #mLocationCallback}
     * with the given argument.
     *
     * @param locationCallback The new value of {@link #mLocationCallback}
     */
    /*package-private*/ void setLocationCallback(LocationCallback locationCallback) {
        this.mLocationCallback = locationCallback;
    }

    /**
     * Unregisters the {@link #mFusedLocationProviderClient} from future
     * location updates.
     */
    /*package-private*/ void removeLocationUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(this.mLocationCallback);
        Log.d(TAG, "Callback removed!");
    }

    /**
     * Returns an instance of {@link LocationRequest}.
     * To be overridden by its subclasses.
     * Each subclass can customise its {@link LocationRequest} to cover its needs.
     *
     * @return an instance of {@link LocationRequest}.
     * @see LocationRequest
     */
    public abstract LocationRequest getLocationRequest();
}