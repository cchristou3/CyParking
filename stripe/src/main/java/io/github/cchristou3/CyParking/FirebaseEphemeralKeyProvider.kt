package io.github.cchristou3.CyParking

import android.util.Log
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.stripe.android.EphemeralKey
import com.stripe.android.EphemeralKeyProvider
import com.stripe.android.EphemeralKeyUpdateListener

/**
 * Represents an object that can call to a server and create
 * [EphemeralKeys][EphemeralKey].
 *
 * @author Charalambos Christou
 * @since 1.0 11/03/21
 */
class FirebaseEphemeralKeyProvider : EphemeralKeyProvider {

    override fun createEphemeralKey(apiVersion: String, keyUpdateListener: EphemeralKeyUpdateListener) {

        // Call our endpoint to create an EphemeralKey
        FirebaseFunctions.getInstance()
                .getHttpsCallable("createEphemeralKey")
                .call(hashMapOf("api_version" to apiVersion))
                .continueWith { task ->
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