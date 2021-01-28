package io.github.cchristou3.CyParking.data.manager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.util.Log;

import androidx.annotation.NonNull;

import io.github.cchristou3.CyParking.data.interfaces.ConnectionHandler;

import static io.github.cchristou3.CyParking.ui.views.host.MainHostActivity.TAG;

/**
 * Purpose: receive Internet connection changes.
 * The specified {@link ConnectionHandler} provides
 * logic on how to handle connection changes.
 *
 * @author Charalambos Christou
 * @version 1.0 25/01/21
 * @see #onAvailable(Network)
 * @see #onLost(Network)
 */
public class ConnectivityHelper extends ConnectivityManager.NetworkCallback {

    private final ConnectivityManager mConnectivityManager;

    private final ConnectionHandler mConnectionHandler;

    /**
     * Initializes the {@link #mConnectivityManager} based on the given context.
     * Assigns to {@link #mConnectionHandler} the given {@link ConnectionHandler}
     * object.
     *
     * @param context           The context to make use of.
     * @param connectionHandler The handler for connection changes.
     */
    public ConnectivityHelper(@NonNull Context context, ConnectionHandler connectionHandler) {
        mConnectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        mConnectionHandler = connectionHandler;
        if (mConnectivityManager.getActiveNetworkInfo() != null)
            Log.d(TAG, "ConnectivityHelper: isConnected? "
                    + mConnectivityManager.getActiveNetworkInfo().isConnected());
    }

    /**
     * Register the {@link #mConnectivityManager} for connection updates.
     */
    public void registerNetworkCallback() {
        mConnectivityManager.registerNetworkCallback(
                new NetworkRequest.Builder()
                        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        .build(),
                this
        );
    }

    /**
     * Unregister the {@link #mConnectivityManager} from further connection updates.
     */
    public void unregisterNetworkCallback() {
        mConnectivityManager.unregisterNetworkCallback(this);
    }

    /**
     * Check whether the device is connected to a network.
     *
     * @return True, if connected. Otherwise, false.
     */
    public boolean isConnected() {
        return mConnectivityManager.getActiveNetworkInfo() != null // Non-null
                && mConnectivityManager.getActiveNetworkInfo().isAvailable() // Connection is available
                && mConnectivityManager.getActiveNetworkInfo().isConnected(); // Connected
    }

    /**
     * Called when the framework connects and has declared a new network ready for use.
     * This callback may be called more than once if the {@link Network} that is
     * satisfying the request changes. This will always immediately be followed by a
     * call to {@link #onCapabilitiesChanged(Network, NetworkCapabilities)} then by a
     * call to {@link #onLinkPropertiesChanged(Network, LinkProperties)}.
     *
     * @param network The {@link Network} of the satisfying network.
     */
    @Override
    public void onAvailable(Network network) {
        super.onAvailable(network);
        Log.d(TAG, "onAvailable: " + network.toString());
        mConnectionHandler.onConnectionStateChanged(true);
    }

    /**
     * Called when the network is about to be disconnected.  Often paired with an
     * {@link ConnectivityManager.NetworkCallback#onAvailable} call with the new replacement network
     * for graceful handover.  This may not be called if we have a hard loss
     * (loss without warning).  This may be followed by either a
     * {@link ConnectivityManager.NetworkCallback#onLost} call or a
     * {@link ConnectivityManager.NetworkCallback#onAvailable} call for this network depending
     * on whether we lose or regain it.
     *
     * @param network     The {@link Network} that is about to be disconnected.
     * @param maxMsToLive The time in ms the framework will attempt to keep the
     *                    network connected.  Note that the network may suffer a
     */
    @Override
    public void onLosing(Network network, int maxMsToLive) {
        super.onLosing(network, maxMsToLive);
        Log.d(TAG, "onLosing: " + network.toString() + " in " + maxMsToLive + "ms");
    }

    /**
     * Called when the framework has a hard loss of the network or when the
     * graceful failure ends.
     *
     * @param network The {@link Network} lost.
     */
    @Override
    public void onLost(Network network) {
        super.onLost(network);
        Log.d(TAG, "onLost: " + network.toString());
        mConnectionHandler.onConnectionStateChanged(false);
    }

    /**
     * Called when the network the framework connected to for this request
     * changes capabilities but still satisfies the stated need.
     *
     * @param network             The {@link Network} whose capabilities have changed.
     * @param networkCapabilities The new {@link NetworkCapabilities} for this
     */
    @Override
    public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities);
        Log.d(TAG, "onCapabilitiesChanged: network " + network.toString()
                + " with capabilities: " + networkCapabilities.toString());
    }

    /**
     * Called when the network the framework connected to for this request
     * changes {@link LinkProperties}.
     *
     * @param network        The {@link Network} whose link properties have changed.
     * @param linkProperties The new {@link LinkProperties} for this network.
     */
    @Override
    public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
        super.onLinkPropertiesChanged(network, linkProperties);
        Log.d(TAG, "onLinkPropertiesChanged: network " + network.toString()
                + " with link properties: " + linkProperties.toString());
    }

    /**
     * Called if no network is found in the timeout time specified in
     * {@link ConnectivityManager.NetworkCallback#requestNetwork(NetworkRequest, ConnectivityManager.NetworkCallback, int)}
     * call. This callback is not
     * called for the version of
     * {@link ConnectivityManager#requestNetwork(NetworkRequest, ConnectivityManager.NetworkCallback)}
     * without timeout. When this callback is invoked the associated
     * {@link NetworkRequest} will have already been removed and released, as if
     * {@link ConnectivityManager#unregisterNetworkCallback(ConnectivityManager.NetworkCallback)} had been called.
     */
    @Override
    public void onUnavailable() {
        super.onUnavailable();
        Log.d(TAG, "onUnavailable");
    }
}
