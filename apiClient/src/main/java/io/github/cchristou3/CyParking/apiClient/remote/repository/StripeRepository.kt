package io.github.cchristou3.CyParking.apiClient.remote.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.functions.HttpsCallableResult
import io.github.cchristou3.CyParking.apiClient.remote.repository.DataSourceRepository.StripeCustomerHandler.Companion.PAYMENTS

/**
 * Purpose: Encapsulate endpoint calls to Firebase related to [Stripe].
 *
 * @author Charalambos Christou
 * @since 1.0 12/03/21
 */
class StripeRepository : DataSourceRepository.StripeCustomerHandler,
        DataSourceRepository.CloudFunctionCaller {

    /**
     * Add the given payment details (amount & currency) to the user's
     * PAYMENTS node. This will trigger an appropriate cloud function
     * that will create a Payment Intent on the server.
     * Note: observe the node to receive the payment intent's details.
     *
     * @param userUid the id of the issuer.
     * @param slotOfferIndex the index of the selected offer.
     * @param lotDocId the document id of the lot in Firestore.
     * @param currency the currency of the payment.
     * @return a [Task] to the user's payment request.
     */
    fun createPaymentIntent(userUid: String, lotDocId: String, slotOfferIndex: Int, currency: String): Task<DocumentReference> {
        return stripeCustomerRef.document(userUid).collection(PAYMENTS)
                .add(preparePaymentDetails(lotDocId, slotOfferIndex, currency))
    }

    /**
     * Communicate with the appropriate endpoint to create an [EphemeralKey]
     * that will allow the application to use the [Stripe] API.
     *
     * @param apiVersion the api version of Stripe.
     * @return A [Task] returning the client secret, or an error.
     */
    fun createEphemeralKey(apiVersion: String): Task<HttpsCallableResult?>? {
        // Call our endpoint to create an EphemeralKey
        return callEphemeralKeyCreationFunction(apiVersion = apiVersion)
    }
}