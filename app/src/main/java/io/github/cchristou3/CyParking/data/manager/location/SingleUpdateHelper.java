package io.github.cchristou3.CyParking.data.manager.location;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import io.github.cchristou3.CyParking.data.interfaces.LocationHandler;

/**
 * Purpose: Provide a <u>single-update</u> location request.
 * Requests the user's location only once with a
 * high-priority queue.
 * <p><b>Important:</b> Once the {@link LocationCallback#onLocationResult(LocationResult)} callback
 * has been triggered, any further calls to {@link #requestUserLocationUpdates(Fragment)}
 * will be ignored. To avoid this `neglecting` behaviour simply call {@link #prepareCallback()}
 * on each subsequent call to {@link #requestUserLocationUpdates(Fragment)}.</p>
 * <p>
 * <b>Note:</b> it can only be constructed via
 * {@link LocationManager#createSingleUpdateHelper(Context, LocationHandler)}.
 * </p>
 *
 * @author Charalambos Christou
 * @version 1.0 23/01/21
 * @see io.github.cchristou3.CyParking.data.manager.location.LocationManager
 */
public class SingleUpdateHelper extends LocationManager {

    private static final String TAG = SingleUpdateHelper.class.getName();
    private final LocationHandler mLocationHandler;

    /**
     * Public Constructor.
     *
     * @param context         The context of the parent's {@link FusedLocationProviderClient}.
     * @param locationHandler The callback method to be invoked when location updates are received.
     */
    /*package-private*/ SingleUpdateHelper(@NonNull Context context, @NonNull LocationHandler locationHandler) {
        super(context);
        mLocationHandler = locationHandler;
        prepareCallback();
    }

    /**
     * Returns an instance of {@link LocationRequest}.
     * This instance will ensure that only one update is received,
     * with a very accurate and yet power-saving priority.
     *
     * @return an instance of {@link LocationRequest}.
     * @see LocationRequest
     */
    @Override
    public LocationRequest getLocationRequest() {
        return new LocationRequest()
                .setNumUpdates(2)
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    /**
     * Implement the {@link LocationCallback#onLocationResult(LocationResult)}
     * based on the class' {@link #mLocationHandler}. Once the callback method
     * has been called, the class unregisters itself from location updates
     * via {@link #removeLocationUpdates()}.
     *
     * @return An instance of {@link LocationCallback}.
     */
    @NonNull
    private LocationCallback getLocationCallback() {
        return new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.d(TAG, "QuickLocation onLocationResult");
                mLocationHandler.onLocationResult(locationResult);
                removeLocationUpdates();
            }
        };
    }

    /**
     * Sets the value of the parent's {@link LocationCallback}
     * to the value returned by this subclass implementation of
     * {@link #getLocationCallback()}.
     */
    public void prepareCallback() {
        setLocationCallback(getLocationCallback());
    }
}
