package io.github.cchristou3.CyParking

import android.util.Log
import com.google.firebase.functions.FirebaseFunctionsException
import com.stripe.android.EphemeralKey
import com.stripe.android.EphemeralKeyProvider
import com.stripe.android.EphemeralKeyUpdateListener
import io.github.cchristou3.CyParking.apiClient.remote.repository.StripeRepository

/**
 * Represents an object that can call to a server and create
 * [EphemeralKeys][EphemeralKey].
 *
 * @author Charalambos Christou
 * @since 1.0 11/03/21
 */
class FirebaseEphemeralKeyProvider(private val repository: StripeRepository) : EphemeralKeyProvider {

    /**
     * When called, talks to a client server that then communicates with Stripe's servers to
     * create an [EphemeralKey].
     *
     * @param apiVersion the Stripe API Version being used
     * @param keyUpdateListener a callback object to notify about results
     */
    override fun createEphemeralKey(apiVersion: String, keyUpdateListener: EphemeralKeyUpdateListener) {
        repository.createEphemeralKey(apiVersion)
                ?.continueWith { task ->
                    if (!task.isSuccessful) {
                        val e = task.exception
                        if (e is FirebaseFunctionsException) {
                            val code = e.code
                            val message = e.message
                            Log.e("EphemeralKey", "Ephemeral key provider returns error: $e $code $message")
                        }
                    }
                    val key = task.result?.data.toString()
                    Log.d("EphemeralKey", "Ephemeral key provider returns $key")
                    keyUpdateListener.onKeyUpdate(key)
                }
    }
}