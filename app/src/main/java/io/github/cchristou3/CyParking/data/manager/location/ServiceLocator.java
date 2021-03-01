package io.github.cchristou3.CyParking.data.manager.location;

import android.content.Context;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

/**
 * Purpose: ensure that the application is making use of a single
 * {@link FusedLocationProviderClient} instance.
 * The {@link LocationManager} and its subclasses are supplied
 * the same instance via this class.
 *
 * @author Charalambos Christou
 * @version 1.0 26/02/21
 */
class ServiceLocator {

    private static ServiceLocator INSTANCE = null;

    private final FusedLocationProviderClient mFusedLocationProviderClient;

    /**
     * Private constructor - Singleton access
     * Initializes the object's FusedLocationProviderClient.
     *
     * @param context The context to make use of.
     */
    private ServiceLocator(Context context) {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
    }

    /**
     * A thread safe method for accessing the current instance.
     *
     * @param context The context to make use of.
     * @return The single {@link ServiceLocator} instance of the application.
     */
    /*package-private*/
    static ServiceLocator getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (ServiceLocator.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ServiceLocator(context);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Access the {@link #mFusedLocationProviderClient}.
     *
     * @return A reference to the {@link #mFusedLocationProviderClient}.
     */
    public FusedLocationProviderClient getLocationProviderClient() {
        return mFusedLocationProviderClient;
    }
}