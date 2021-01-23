package io.github.cchristou3.CyParking.data.interfaces;

import androidx.fragment.app.Fragment;

import io.github.cchristou3.CyParking.data.manager.location.LocationManager;
import io.github.cchristou3.CyParking.ui.home.HomeFragment;
import io.github.cchristou3.CyParking.ui.parking.lots.register.RegisterLotFragment;

/**
 * Purpose:
 * Provide the {@link LocationManager} class the
 * appropriate callback method to handle
 * incoming LocationResult objects.
 * Used by {@link HomeFragment}
 * and {@link RegisterLotFragment}.
 *
 * @author Charalambos Christou
 * @version 1.0 21/12/20
 */
public interface LocationHandler {
    /**
     * Callback invoked when the user's location is received.
     *
     * @param locationResult The result of the user's requested location.
     * @see LocationManager#requestUserLocationUpdates(Fragment)
     */
    void onLocationResult(com.google.android.gms.location.LocationResult locationResult);
}
