package io.github.cchristou3.CyParking.data.interfaces;

import android.net.ConnectivityManager;
import android.net.Network;

/**
 * Purpose: allow the View to handle changes to the device's Internet Connection.
 * It is used by {@link io.github.cchristou3.CyParking.ui.views.host.MainHostActivity}.
 *
 * @author Charalambos Christou
 * @version 1.0 25/01/21
 * @see io.github.cchristou3.CyParking.ui.views.host.MainHostActivity#onConnectionStateChanged(boolean).
 * @see io.github.cchristou3.CyParking.data.manager.ConnectivityHelper
 */
public interface ConnectionHandler {
    /**
     * Triggered whenever the
     * {@link ConnectivityManager.NetworkCallback} invokes
     * either {@link ConnectivityManager.NetworkCallback#onAvailable(Network)}
     * or {@link ConnectivityManager.NetworkCallback#onLost(Network)}.
     *
     * @param isConnected The state of the Internet connection.
     * @see io.github.cchristou3.CyParking.data.manager.ConnectivityHelper#onLost(Network)
     * @see io.github.cchristou3.CyParking.data.manager.ConnectivityHelper#onAvailable(Network)
     */
    void onConnectionStateChanged(boolean isConnected);
}
