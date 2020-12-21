package io.github.cchristou3.CyParking.data.manager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

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
 *
 * @author Charalambos Christou
 * @version 1.0 21/12/20
 */
public class SingleLocationManager {

    // Constant variables
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 321;
    private static final String TAG = SingleLocationManager.class.getName() + "UniqueTag";

    // Data members
    private final FusedLocationProviderClient mFusedLocationProviderClient;
    private final LocationHandler mLocationHandler;

    /**
     * Public Constructor.
     * Initializes the {@link #mFusedLocationProviderClient} object with the given context
     * and the {@link #mLocationHandler} with the specified LocationHandler instance.
     *
     * @param context         The context of the {@link #mFusedLocationProviderClient}
     * @param locationHandler The callback method to be invoked when location updates are received.
     */
    public SingleLocationManager(@NonNull final Context context, @NonNull final LocationHandler locationHandler) {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        mLocationHandler = locationHandler;
    }

    /**
     * Requests the user's latest known location.
     * This procedure is wrapped around appropriate permission
     * checks to avoid requesting the user's location without
     * his permission.
     *
     * @param fragment The fragment that requests the location-based permissions.
     */
    public void getLastKnownLocationOfUser(@NonNull Fragment fragment) {
        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(fragment.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(fragment.requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= 23) {// Marshmallow
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
     *
     * @see #mLocationHandler
     */
    @SuppressLint("MissingPermission")
    private void requestLocation() {
        mFusedLocationProviderClient.requestLocationUpdates(new LocationRequest()
                        .setNumUpdates(1)
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY),
                new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        mLocationHandler.onLocationResult(locationResult);
                    }
                }, Looper.getMainLooper());
    }
}
