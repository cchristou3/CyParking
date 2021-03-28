package io.github.cchristou3.CyParking.data.manager.location;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.viewbinding.ViewBinding;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

import io.github.cchristou3.CyParking.data.interfaces.LocationHandler;
import io.github.cchristou3.CyParking.ui.components.BaseFragment;

/**
 * Purpose: Provide <u>continuous</u> location requests. Refers to
 * receiving the user's position at intervals.
 * The class' constructor registers the FusedLocationProviderClient
 * for location updates.
 * This mode is lifecycle-aware, as the class itself manages
 * the registration and removal of location updates, in
 * {@link #onResume(LifecycleOwner)} and in {@link #onPause(LifecycleOwner)}
 * respectively.
 * Besides, as a lifecycle observer, it handles it own cleanup in {@link #onStop(LifecycleOwner)}.
 * <p>
 * <b>Note:</b> it can only be constructed via
 * {@link LocationManager#createSubsequentUpdateHelper(BaseFragment, LocationHandler)}.
 * </p>
 *
 * @param <T> Any subclass of [BaseFragment].
 * @param <S> Any subclass of [ViewBinding].
 * @author Charalambos Christou
 * @version 2.0 27/03/21
 * @see androidx.lifecycle.DefaultLifecycleObserver
 * @see io.github.cchristou3.CyParking.data.manager.location.LocationManager
 */
public class SubsequentUpdateHelper<T extends BaseFragment<S>, S extends ViewBinding>
        extends LocationManager<T, S> implements DefaultLifecycleObserver {

    private static final long INTERVAL_TIME = 5000L;
    private final WeakReference<T> mWeakFragment;


    /**
     * Public Constructor.
     * Initializes the the parent's {@link com.google.android.gms.location.FusedLocationProviderClient}
     * with the given context and creates an appropriate {@link LocationCallback} based on
     * the given {@link LocationHandler}.
     * Also, it creates a {@link WeakReference} of the specified fragment and attaches
     * to its lifecycle the LocationManager instance.
     *
     * @param fragment        The fragment requesting location updates.
     * @param locationHandler The callback method to be invoked when location updates are received.
     */
    /*package-private*/ SubsequentUpdateHelper(
            @NonNull T fragment, @NonNull LocationHandler locationHandler
    ) {
        super(fragment.requireContext());
        setLocationCallback(new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                locationHandler.onLocationResult(locationResult);
            }
        });
        mWeakFragment = new WeakReference<>(fragment);
        fragment.getLifecycle().addObserver(this);
    }

    /**
     * @return A {@link LocationRequest}.
     * @see #getLocationRequest()
     */
    @NotNull
    public static LocationRequest getRequest() {
        return new LocationRequest().setInterval(INTERVAL_TIME);
    }

    /**
     * Notifies that {@code ON_RESUME} event occurred.
     * Registers the FusedLocationProviderClient instance for updates.
     *
     * @param owner the component, whose state was changed
     * @see #requestUserLocationUpdates(BaseFragment)
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
     * @see #removeLocationUpdates()
     */
    @Override
    public void onPause(@NonNull LifecycleOwner owner) {
        removeLocationUpdates();
    }

    /**
     * Notifies that {@code ON_STOP} event occurred.
     * <p>
     * The observer added in {@link #SubsequentUpdateHelper(BaseFragment, LocationHandler)}
     * is removed.
     *
     * @param owner the component, whose state was changed
     */
    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        mWeakFragment.get().getLifecycle().removeObserver(this);
    }

    /**
     * Returns an instance of {@link LocationRequest}.
     * The instance's Interval got set to {@link #INTERVAL_TIME}.
     * Thus, every INTERVAL_TIME millis a new location is received.
     *
     * @return an instance of {@link LocationRequest}.
     * @see LocationRequest
     * @see #INTERVAL_TIME
     */
    @NotNull
    @Override
    public LocationRequest getLocationRequest() {
        return getRequest();
    }

    /**
     * @param fragment The current active fragment instance.
     * @see LocationManager#beforeRequests(BaseFragment)
     */
    @Override
    public void beforeRequests(@NotNull T fragment) { /* no extra preparation is needed */ }

    /**
     * Clean up resources whenever an error occurs.
     *
     * @param fragment any subclass of {@link BaseFragment}.
     */
    @Override
    public void performErrorCleanUp(@NotNull T fragment) { /* no need to clean up */ }
}
