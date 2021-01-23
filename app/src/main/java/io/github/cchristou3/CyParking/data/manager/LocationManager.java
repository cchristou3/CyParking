package io.github.cchristou3.CyParking.data.manager;

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
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

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
 * <p>
 * The LocationManager instance can be set into two main modes depending
 * on which of its constructors is invoked.
 * <p>Single-request mode: Requests the user's location only once with a
 * high-priority queue.
 * {@link LocationManager#LocationManager(Context, LocationHandler, boolean)}
 * </p>
 * <p>Continuous-requests mode: Refers to receiving the user's position
 * at intervals.
 * The following constructor:
 * {@link LocationManager#LocationManager(Context, Fragment, LocationHandler, boolean)}
 * registers the FusedLocationProviderClient for location updates.
 * This mode is lifecycle-aware, as the class itself manages
 * the registration and removal of location updates, in
 * {@link #onResume(LifecycleOwner)} and in {@link #onPause(LifecycleOwner)}
 * respectively.
 * Besides, as a lifecycle observer, it handles it own cleanup in {@link #onStop(LifecycleOwner)}.
 * </p>
 * <p> In both modes, the received LocationResult is then handled by
 *  the implementation of the LocationHandler provided by the fragment/activity.
 *  This mode is set by the invocation of</p>
 *
 * @author Charalambos Christou
 * @version 3.0 23/01/21
 */
public class LocationManager implements DefaultLifecycleObserver {

    // Constant variables
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 321;
    private static final long INTERVAL_TIME = 10000L;
    private static final String TAG = LocationManager.class.getName() + "UniqueTag";

    // Data members
    private final FusedLocationProviderClient mFusedLocationProviderClient;
    private final LocationHandler mLocationHandler;
    private final boolean mIsSingleRequest;
    private final LocationCallback mLocationCallback;
    private WeakReference<Fragment> mWeakFragment;

    /**
     * Public Constructor.
     * Initializes the {@link #mFusedLocationProviderClient} object with the given context,
     * the {@link #mLocationHandler} with the specified LocationHandler instance,
     * the {@link #mIsSingleRequest} with the specified argument and
     * the {@link #mLocationCallback}.
     *
     * @param context         The context of the {@link #mFusedLocationProviderClient}
     * @param locationHandler The callback method to be invoked when location updates are received.
     * @param isSingleRequest Flag indicating whether the client requests a single location update or
     *                        many within intervals.
     */
    public LocationManager(@NonNull final Context context, @NonNull final LocationHandler locationHandler, boolean isSingleRequest) {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        Log.d(TAG, "LocationManager: ");
        mLocationHandler = locationHandler;
        this.mIsSingleRequest = isSingleRequest;
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.d(TAG, "onLocationResult Of Manager: ");
                mFusedLocationProviderClient.removeLocationUpdates(this); // Required
                mLocationHandler.onLocationResult(locationResult);
            }
        };
    }

    /**
     * Public Constructor.
     * Initializes the {@link #mFusedLocationProviderClient} object with the given context
     * and the {@link #mLocationHandler} with the specified LocationHandler instance,
     * the {@link #mIsSingleRequest} with the specified argument and
     * the {@link #mLocationCallback}.
     * Also, it creates a {@link WeakReference} of the specified fragment and attaches
     * to its lifecycle the LocationManager instance.
     *
     * @param context         The context of the {@link #mFusedLocationProviderClient}
     * @param fragment        The fragment requesting location updates.
     * @param locationHandler The callback method to be invoked when location updates are received.
     * @param isSingleRequest Flag indicating whether the client requests a single location update or
     *                        many within intervals.
     */
    public LocationManager(@NonNull final Context context, @NonNull Fragment fragment,
                           @NonNull final LocationHandler locationHandler, boolean isSingleRequest) {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        mLocationHandler = locationHandler;
        this.mIsSingleRequest = isSingleRequest;
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.d(TAG, "onLocationResult Of Manager: ");
                mLocationHandler.onLocationResult(locationResult);
            }
        };
        mWeakFragment = new WeakReference<>(fragment);
        fragment.getLifecycle().addObserver(this);
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
            Log.d(TAG, "Above if: ");
            if (Build.VERSION.SDK_INT >= 23) {// Marshmallow
                Log.d(TAG, "Inside if: ");
                fragment.requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            Log.d(TAG, "The else clause: ");
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
        Log.d(TAG, "requestLocation: mIsSingleRequest ? " + mIsSingleRequest);
        mFusedLocationProviderClient.requestLocationUpdates(
                (mIsSingleRequest) ?
                        new LocationRequest()
                                .setNumUpdates(1)
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        :
                        new LocationRequest().setInterval(INTERVAL_TIME),
                mLocationCallback,
                Looper.getMainLooper())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "requestLocation: Success: " + task.getResult());
                    } else {
                        Log.d(TAG, "requestLocation: Failed " + task.getException());
                    }
                });
    }

    /**
     * Notifies that {@code ON_RESUME} event occurred.
     * Registers the FusedLocationProviderClient instance for updates.
     *
     * @param owner the component, whose state was changed
     */
    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        requestUserLocationUpdates(mWeakFragment.get());
    }

    /**
     * Notifies that {@code ON_PAUSE} event occurred.
     * Unregisters the FusedLocationProviderClient instance from updates.
     * Thus, the client will no more receive updates concerning the user's
     * location.
     *
     * @param owner the component, whose state was changed
     */
    @Override
    public void onPause(@NonNull LifecycleOwner owner) {
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }

    /**
     * Notifies that {@code ON_STOP} event occurred.
     * <p>
     * The observer added in {@link #LocationManager(Context, Fragment, LocationHandler, boolean)}
     * is removed.
     *
     * @param owner the component, whose state was changed
     */
    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        mWeakFragment.get().getLifecycle().removeObserver(this);
    }
}