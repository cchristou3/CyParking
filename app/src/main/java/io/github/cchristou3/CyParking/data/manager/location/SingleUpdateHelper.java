package io.github.cchristou3.CyParking.data.manager.location;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import org.jetbrains.annotations.NotNull;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import io.github.cchristou3.CyParking.data.interfaces.LocationHandler;
import io.github.cchristou3.CyParking.ui.components.BaseFragment;

/**
 * Purpose: Provide a <u>single-update</u> location request.
 * Requests the user's location only once with a
 * high-priority queue.
 * <p><b>Important:</b> Once the {@link LocationCallback#onLocationResult(LocationResult)} callback
 * has been triggered, any further calls to {@link #requestUserLocationUpdates(BaseFragment)}
 * will be ignored. To avoid this `neglecting` behaviour simply call {@link #prepareCallback(BaseFragment)}
 * on each subsequent call to {@link #requestUserLocationUpdates(BaseFragment)}.</p>
 * <p>
 * <b>Note:</b> it can only be constructed via
 * {@link LocationManager#createSingleUpdateHelper(BaseFragment, LocationHandler, Runnable, Runnable)}.
 * </p>
 *
 * @param <T> Any subclass of [BaseFragment].
 * @param <S> Any subclass of [ViewBinding].
 * @author Charalambos Christou
 * @version 2.0 27/03/21
 * @see io.github.cchristou3.CyParking.data.manager.location.LocationManager
 */
public class SingleUpdateHelper<T extends BaseFragment<S>, S extends ViewBinding>
        extends LocationManager<T, S> {

    private static final String TAG = SingleUpdateHelper.class.getName();
    private static final long SINGLE_REQUEST_TIME_OUT = 8000L;
    private final LocationHandler mLocationHandler;
    private final Runnable mLocationServiceError;
    private final Runnable mErrorCallback;
    private final AtomicBoolean mWasRequestReceived = new AtomicBoolean(false);


    /**
     * Public Constructor. Initialize the object's data members.
     *
     * @param fragment             The current active fragment instance.
     * @param locationHandler      The callback method to be invoked when location updates are received.
     * @param errorCallback        a method to get triggered whenever an error occurs.
     * @param locationServiceError a method to get triggered when a location
     */
    /*package-private*/ SingleUpdateHelper(
            @NonNull T fragment, @NonNull LocationHandler locationHandler,
            @Nullable Runnable errorCallback, Runnable locationServiceError
    ) {
        super(fragment.requireContext());
        mLocationHandler = locationHandler;
        this.mLocationServiceError = locationServiceError;
        this.mErrorCallback = errorCallback;
        prepareCallback(fragment);
    }

    /**
     * @return A {@link LocationRequest}.
     * @see #getLocationRequest()
     */
    @NotNull
    public static LocationRequest getRequest() {
        return new LocationRequest()
                .setNumUpdates(2)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setFastestInterval(10L)
                .setInterval(60L)
                .setMaxWaitTime(200L);
    }

    /**
     * Returns an instance of {@link LocationRequest}.
     * This instance will ensure that only one update is received,
     * with a very accurate and yet power-saving priority.
     *
     * @return an instance of {@link LocationRequest}.
     * @see LocationRequest
     */
    @NotNull
    @Override
    public LocationRequest getLocationRequest() {
        return getRequest();
    }

    /**
     * Implement the {@link LocationCallback#onLocationResult(LocationResult)}
     * based on the class' {@link #mLocationHandler}. Once the callback method
     * has been called, the class unregisters itself from location updates
     * via {@link #removeLocationUpdates()}.
     *
     * @param fragment The current active fragment instance.
     * @return An instance of {@link LocationCallback}.
     */
    @NonNull
    private LocationCallback getLocationCallback(T fragment) {
        return new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.d(TAG, "onLocationResult: " + locationResult.toString());
                mWasRequestReceived.set(true);
                fragment.getGlobalStateViewModel().hideLoadingBar();
                mLocationHandler.onLocationResult(locationResult);
                removeLocationUpdates();
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
                Log.d(TAG, "onLocationAvailability: " + locationAvailability.isLocationAvailable());
                if (!locationAvailability.isLocationAvailable()) {
                    // If the device's location services are no available
                    performErrorCleanUp(fragment);
                    mLocationServiceError.run();
                }
            }
        };
    }

    /**
     * Sets the value of the parent's {@link LocationCallback}
     * to the value returned by this subclass implementation of
     * {@link #getLocationCallback(BaseFragment)}.
     */
    public void prepareCallback(T fragment) {
        setLocationCallback(getLocationCallback(fragment));
    }


    /**
     * Gets invoked before the devices starts requesting
     * for the user's location.
     *
     * @param fragment The current active fragment instance.
     * @see LocationManager#beforeRequests(BaseFragment)
     */
    @Override
    public void beforeRequests(T fragment) {
        fragment.getGlobalStateViewModel().showLoadingBar();
        setUpTimeOut(fragment);
    }

    /**
     * Set up a time-out to trigger a callback after
     * a period of time ({@link #SINGLE_REQUEST_TIME_OUT}).
     * callback: If by then, not a single location was retrieved
     * (onLocationResult was not called), then stop
     * listening for location updates and inform the user
     * that something is wrong with the location services (permissions).
     *
     * @param fragment The current active fragment instance.
     */
    private void setUpTimeOut(T fragment) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (!mWasRequestReceived.get()) {
                    performErrorCleanUp(fragment);
                    mLocationServiceError.run();
                }
            }
        }, SINGLE_REQUEST_TIME_OUT);
    }

    /**
     * Clean up resources whenever an error occurs.
     *
     * @param fragment any subclass of {@link BaseFragment}.
     */
    public void performErrorCleanUp(@NotNull T fragment) {
        // Stop listening to updates - it is taking too long
        // something might be wrong with the device or the location
        // service
        removeLocationUpdates();
        fragment.getGlobalStateViewModel().hideLoadingBar();
        // Inform user
        if (mErrorCallback != null) {
            mErrorCallback.run();
        }
    }
}
