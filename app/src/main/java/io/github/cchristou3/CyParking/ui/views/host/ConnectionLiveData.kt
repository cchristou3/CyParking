package io.github.cchristou3.CyParking.ui.views.host

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkRequest
import android.util.Log
import androidx.lifecycle.LiveData

/**
 * Purpose: Observe network connectivity changes and maintain its state.
 *
 * @author Charalambos Christou
 * @version 1.0 09/02/21
 * @see [ConnectionLiveData.onActive]
 * @see [ConnectionLiveData.onInactive]
 * @see [ConnectivityManager.NetworkCallback.onAvailable]
 * @see [ConnectivityManager.NetworkCallback.onLost]
 */
class ConnectionLiveData(context: Context) : LiveData<Boolean>() {

    private val TAG = "ConnectionLiveData"
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val validNetworks: MutableSet<Network> = HashSet()

    /**
     * Post the [LiveData]'s value based on the [validNetworks]'s
     * size. If [validNetworks]'s size is greater than 0 then
     * the user's device is connected to the internet.
     */
    private fun checkValidNetworks() {
        postValue(validNetworks.size > 0)
    }

    /**
     * Registers the network callback.
     * Gets triggered when the lifecycle owner is in
     * [Lifecycle.State.STARTED] or [Lifecycle.State.RESUMED].
     */
    override fun onActive() {
        networkCallback = createNetWorkCallback()
        val networkRequest = NetworkRequest.Builder()
                .addCapability(NET_CAPABILITY_INTERNET)
                .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    /**
     * Unregisters the network callback.
     * Gets triggered when the lifecycle owner is not in
     * [Lifecycle.State.STARTED] or [Lifecycle.State.RESUMED].
     */
    override fun onInactive() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    /**
     * Creates a [ConnectivityManager.NetworkCallback] instance to handle
     * upcoming network broadcast events.
     */
    private fun createNetWorkCallback(): ConnectivityManager.NetworkCallback {
        return object : ConnectivityManager.NetworkCallback() {
            /**
             * Called when the framework connects and has declared a new network ready for use.
             * This callback may be called more than once if the [Network] that is
             * satisfying the request changes. This will always immediately be followed by a
             * call to [.onCapabilitiesChanged] then by a
             * call to [.onLinkPropertiesChanged].
             *
             * @param network The [Network] of the satisfying network.
             */
            override fun onAvailable(network: Network) {
                Log.d(TAG, "onAvailable: $network")
                val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
                val isInternet = networkCapabilities?.hasCapability(NET_CAPABILITY_INTERNET)
                Log.d(TAG, "onAvailable: ${network}, $isInternet")
                if (isInternet == true) {
                    validNetworks.add(network)
                }
                checkValidNetworks()
            }

            /**
             * Called when the framework has a hard loss of the network or when the
             * graceful failure ends.
             *
             * @param network The [Network] lost.
             */
            override fun onLost(network: Network) {
                Log.d(TAG, "onLost: $network")
                validNetworks.remove(network)
                checkValidNetworks()
            }
        }
    }
}