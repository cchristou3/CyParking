package io.github.cchristou3.CyParking

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.stripe.android.*
import com.stripe.android.model.ConfirmPaymentIntentParams
import com.stripe.android.model.PaymentMethod
import com.stripe.android.view.BillingAddressFields

/**
 * Purpose: Encapsulate payment flow.
 * Important terms:
 *  <p>
 *  <p>Publishable API keys
 *  Publishable API keys are meant solely to identify your account with Stripe,
 *  they aren’t secret. In other words, they can safely be published in places
 *  like your Stripe.js JavaScript code, or in an Android or iPhone app.</p>
 *
 *  <p>Secret API keys
 *  Secret API keys should be kept confidential and only stored on your own
 *  servers. Your account’s secret API key can perform any API request
 *  to Stripe without restriction. </p>
 *
 *  <p>[EphemeralKey]
 *  For security, the Customer API is not directly accessible from the client.
 *  Instead, your server provides the SDK with an ephemeral key—a short-lived
 *  API key with restricted access to the Customer API. You can think of an
 *  ephemeral key as a session, authorizing the SDK to retrieve and update
 *  a specific Customer object s for the duration of the session. </p>
 *
 *  <p>[CustomerSession]
 *  A CustomerSession talks to your backend to retrieve an ephemeral
 *  key for your Customer with its [EphemeralKeyProvider], and uses that
 *  key to manage retrieving and updating the Customer’s payment methods
 *  on your behalf.
 *  To reduce load times, preload your customer’s information by
 *  initializing CustomerSession before they enter your payment flow.
 *  If your current user logs out of the app, clear the current
 *  CustomerSession singleton by calling CustomerSession.endCustomerSession().
 *  When a new user logs in, re-initialize the instance. On your backend,
 *  create and return a new ephemeral key for the Customer object associated
 *  with the new user. </p>
 *
 *  <p>[EphemeralKeyProvider]
 *  Represents an object that can call to a server and create EphemeralKeys.
 *  Override its [EphemeralKeyProvider.createEphemeralKey()] method
 *  to call your backend's endpoint responsible for creating an EphemeralKey.
 *  E.g. in NodeJs:
 *  <pre>
 *      const key = await stripe.ephemeralKeys.create(
 *           { customer },
 *           { apiVersion: data.api_version }
 *        )
 *  </pre> </p>
 *
 * <p>[PaymentSession]
 * Represents a single start-to-finish payment operation.
 * <a href='https://stripe.com/docs/mobile/android/basic'>
 *     Using Android basic integration for more information. </a>
 * If PaymentSessionConfig.shouldPrefetchCustomer is true,
 * and the customer has previously selected a payment method,
 * PaymentSessionData.paymentMethod will be updated with the payment
 * method and PaymentSessionListener.onPaymentSessionDataChanged will be called.
 * It uses CustomerSession to launch full-screen activities to collect
 * and store payment information, and can also be used to collect shipping info.
 * Think of it as the data source for your checkout activity—it handles
 * asynchronously retrieving the data you need, and notifies its
 * PaymentSessionListener when your UI should change. </p>
 *
 * <p>[PaymentSession.PaymentSessionListener]
 * Represents a listener for PaymentSession actions, used to update the host activity when necessary. </p>
 *
 * <p>Client Secret
 * The client secret of this PaymentIntent. Used for client-side
 * retrieval using a publishable key.
 * The client secret can be used to complete a payment from your frontend.
 * It should not be stored, logged, embedded in URLs, or exposed
 * to anyone other than the customer. Make sure that you have TLS enabled
 * on any page that includes the client secret. </p>
 *
 * @see <a href='https://stripe.dev/stripe-android/stripe/com.stripe.android/-payment-session/-payment-session-listener/index.html'>PaymentSessionListener</a>
 * @see <a href='https://stripe.com/docs/mobile/android/basic#set-up-ephemeral-key'>Android Integration</a>
 * @see <a href='https://stripe.com/docs/api/payment_intents/object#payment_intent_object-client_secret'>The PaymentIntent object</a>
 * @see <a href='https://stripe.com/docs/keys#:~:text=Publishable%20API%20keys%20are%20meant%20solely%20to%20identify,confidential%20and%20only%20stored%20on%20your%20own'>API Keys</a>
 * @see <a href='https://github.com/stripe-samples/firebase-mobile-payments'>stripe-samples/firebase-mobile-payments</a>
 *
 * </p>
 * <p>
 * Flow:
 * <p> Step 1 - Set up the customer session and its EphemeralKeyProvider
 * to retrieve the EphemeralKey that will allow us to use Stripe.
 * You can think of an ephemeral key as a session,
 * authorizing the SDK to retrieve and update a specific
 * Customer object s for the duration of the session.
 * @see PaymentSessionHelper.initCustomerSession(context)
 * </p>
 *
 * <p> Step 2 - Set up the payment session and its listener.
 * Should be called when the user is about to go into
 * payment workflow -> e.g. On the payment screen / or one screen before that.
 * @see PaymentSessionHelper.setUpPaymentSession(activity)
 * </p>
 *
 * <p> Step 3 - Let the user pick a payment method.
 * @see PaymentSessionHelper.presentPaymentMethodSelection()</p>
 *
 * <p> Step 4 - Handle the chosen method (if one was chosen).
 * @see PaymentSessionHelper.handlePaymentData(requestCode, resultCode, data)</p>
 *
 * <p> Step 5 - Initiate a payment charge.
 * @see PaymentSessionHelper.confirmPayment(activity, currentUser)</p>
 *
 * <p> Step 6 - Create a payment intent and confirm the payment.
 * By creating a new payment object in the
 * /stripe_customers/{userId}/payments/{pushId} node the
 * 'createStripePayment' cloud function is invoked which creates
 * a new PaymentIntent based on the given amount, currency and
 * customer. If the PaymentIntent was created it merges with the current
 * payment document.
 * At the same we are observing this node from the client side.
 * Once the payment intent merges, the client is notified about it and uses it
 * to access the payment intent's client secret to confirm the payment
 * via [Stripe.confirmPayment()].
 * Stripe then handles the payment and triggers one of the following events:
 * - 'payment_intent.succeeded'
 * - 'payment_intent.processing'
 * - 'payment_intent.payment_failed'
 * - 'payment_intent.canceled'
 * In such events, our 'handleWebhookEvents' endpoint (cloud function) is called
 * updating the the payment's status of the document.
 *
 * Note: The document refers to the document pushed in
 * /stripe_customers/{userId}/payments/{pushId}.
 * </p>
 *
 *<p>
 *Step 7 - End customer session if user logged out.
 *@see PaymentSessionHelper.endCustomerSession()
 *</p>
 *</p>
 *
 * @author Charalambos Christou
 * @since 1.0 10/03/21
 * @param mUiPaymentSessionListener A Ui handler for payment session updates.
 */
open class PaymentSessionHelper(val mUiPaymentSessionListener: UiPaymentSessionListener) {

    companion object {
        val TAG: String? = PaymentSessionHelper::class.java.canonicalName

        /**
         * Initialize the Customer Session singleton.
         * To reduce load times, preload your customer’s information
         * by initializing CustomerSession before they enter your payment flow.
         *
         * @param context The application context.
         */
        @JvmStatic
        fun initCustomerSession(context: Context) {
            // Step 1 - Set up the customer session and its EphemeralKeyProvider
            // to retrieve the EphemeralKey that will allow us to use Stripe.
            // You can think of an ephemeral key as a session,
            // authorizing the SDK to retrieve and update a specific
            // Customer object s for the duration of the session.

            // Set up customer session
            CustomerSession.initCustomerSession(context.applicationContext, FirebaseEphemeralKeyProvider())
        }

        /**
         * If your current user logs out of the app,
         * clear the current CustomerSession singleton by
         * calling CustomerSession.endCustomerSession().
         * When a new user logs in, re-initialize the instance.
         * On your backend, create and return a new ephemeral
         * key for the Customer object associated with the new user.
         */
        @JvmStatic
        fun endCustomerSession() {
            // Step 7 - End customer session if user logged out.
            CustomerSession.endCustomerSession()
        }
    }

    // Data members
    private lateinit var mPaymentSession: PaymentSession
    private var mListenerForPaymentIntent: ListenerRegistration? = null
    private var mSelectedPaymentMethod: PaymentMethod? = null
    private lateinit var mPaymentHandler: PaymentHandler
    private lateinit var stripe: Stripe


    /**
     * Present the payment method selection flow.
     */
    fun presentPaymentMethodSelection() {
        // Step 3 - Let the user pick a payment method.
        mPaymentSession.presentPaymentMethodSelection()
    }

    /**
     * Initialize the [PaymentSession] instance
     * and attach it a [PaymentSession.PaymentSessionListener] that will listen
     * to session data changes.
     *
     * Note: When configurations change, this method should be re-invoked
     * with the new instance of the previously killed [fragment].
     * @param fragment the current hosted fragment.
     */
    fun setUpPaymentSession(fragment: Fragment) {
        // Step 2 - Set up the payment session and its listener.
        // Should be called when the user is about to go into
        // payment workflow -> e.g. On the payment screen / or one screen before that.
        // Set up payment session
        stripe = ServiceLocator.getInstance(fragment.requireContext().applicationContext).stripeInstance
        mPaymentSession = getPaymentSession(fragment)
        mPaymentSession.init(getPaymentSessionListener())
    }

    /**
     * Create a listener for PaymentSession actions, used to update the host activity/fragment
     * when necessary.
     * @return an instance of [PaymentSession.PaymentSessionListener].
     */
    private fun getPaymentSessionListener(): PaymentSession.PaymentSessionListener =
            object : PaymentSession.PaymentSessionListener {
                override fun onCommunicatingStateChanged(isCommunicating: Boolean) {
                    Log.d("PaymentSession", "isCommunicating $isCommunicating")
                    mUiPaymentSessionListener.onCommunicatingStateChanged(isCommunicating)
                }

                override fun onError(errorCode: Int, errorMessage: String) {
                    Log.e("PaymentSession", "onError: $errorCode, $errorMessage")
                    mUiPaymentSessionListener.onError(errorCode, errorMessage)
                }

                /**
                 * Whenever the payment session data change, this callback is triggered.
                 * E.g. when a user has selected a card from [PaymentSession.presentPaymentMethodSelection]
                 * the fragment's / activity's onActivityResult method is invoked, whose data is
                 * then handled by the [PaymentSession.handlePaymentData] method. Next the listener will
                 * get notified about the session data and invoke this method.
                 *
                 * @param data the current payment session data.
                 */
                override fun onPaymentSessionDataChanged(data: PaymentSessionData) {
                    Log.d("PaymentSession", "PaymentSession has changed: $data")
                    Log.d("PaymentSession", "${data.isPaymentReadyToCharge} <> ${data.paymentMethod}")

                    // This method should get triggered after the user has selected a payment method. //
                    if (data.isPaymentReadyToCharge) {
                        // Use the data to complete your charge - see confirmPayment.
                        Log.d("PaymentSession", "Ready to charge")
                        data.paymentMethod?.let {
                            Log.d("PaymentSession", "PaymentMethod $it selected")
                            // Display the payment method to the user
                            mUiPaymentSessionListener.onPaymentMethodSelected(
                                    "${it.card?.brand} card ends with ${it.card?.last4}")
                            mSelectedPaymentMethod = it
                        }
                    }
                }
            }

    /**
     * Create an instance of [PaymentSession] with the specified arguments on its
     * [PaymentSessionConfig.Builder].
     * @return an instance of [PaymentSession].
     */
    private fun getPaymentSession(fragment: Fragment): PaymentSession = PaymentSession(fragment,
            PaymentSessionConfig.Builder()
                    .setShippingInfoRequired(false)
                    .setShippingMethodsRequired(false)
                    .setBillingAddressFields(BillingAddressFields.None)
                    .setShouldPrefetchCustomer(false)
                    .build())

    /**
     * Confirms the payment.
     * Behind the scenes, it creates a payment intent
     * and uses the intent's client secret to confirm the payment
     * via [Stripe.confirmPayment].
     *
     * @param currentUserUid The UID of current user.
     * @param fragment The current active fragment.
     */
    fun confirmPayment(fragment: Fragment, currentUserUid: String?) {
        // Step 5 - Initiate a payment charge.
        val paymentCollection: CollectionReference = FirebaseFirestore.getInstance()
                .collection("stripe_customers")
                .document(currentUserUid ?: "")
                .collection("payments")

        paymentCollection.add(hashMapOf(
                "amount" to 8800, // The amount should be calculated in the server side for security reasons.
                "currency" to "hkd")
        )
                // Step 6 - Create a payment intent (via cloud function) in the server side
                // and confirm the payment from the client side.
                .addOnSuccessListener { documentReference ->
                    Log.d("payment", "DocumentSnapshot added with ID: ${documentReference.id}")
                    mListenerForPaymentIntent = documentReference.addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            Log.w("payment", "Listen failed.", e)
                            return@addSnapshotListener
                        }
                        // If we come till this way then the payment intent has been established
                        if (snapshot != null && snapshot.exists()) {
                            Log.d("payment", "Current data: ${snapshot.data}")
                            val clientSecret = snapshot.data?.get("client_secret")
                            Log.d("payment", "Create paymentIntent returns $clientSecret")
                            clientSecret?.let {
                                stripe.confirmPayment(fragment, ConfirmPaymentIntentParams.createWithPaymentMethodId(
                                        this.mSelectedPaymentMethod?.id ?: "",
                                        (it as String)
                                ))
                                mPaymentHandler.onPaymentSuccess()
                                mListenerForPaymentIntent?.remove() // Do not listen to further updates
                            }
                        } else {
                            mPaymentHandler.onPaymentFailure()
                            Log.e("payment", "Current payment intent : null")
                        }
                    }
                }
                .addOnFailureListener { e ->
                    mPaymentHandler.onPaymentFailure()
                    Log.w("payment", "Error adding document", e)
                }
    }

    /**
     * Should be called by the calling fragment/activity's
     * [AppCompatActivity.onActivityResult] method and when returning from the
     * add/select card screen.
     * Invoking this method will then trigger the
     * [PaymentSession.PaymentSessionListener.onPaymentSessionDataChanged]
     * that has been initialized in [setUpPaymentSession].
     */
    fun handlePaymentData(requestCode: Int, resultCode: Int, data: Intent) {
        // Step 4 - Handle the chosen method (if one was chosen).
        // Check the if it was CANCELLED or OK
        mPaymentSession.handlePaymentData(requestCode, resultCode, data)
    }

    /**
     * Set the object's [PaymentHandler] with the given argument
     * @param paymentHandler the new [PaymentHandler] of the [PaymentSessionHelper].
     */
    fun setPaymentHandler(paymentHandler: PaymentHandler) {
        this.mPaymentHandler = paymentHandler
    }

    /**
     * Purpose: handle events related to the payment intent.
     * Events are as follows:
     * - The server side successfully created a payment intent
     * and sent it client secret to the client side.
     * - The server failed to create a payment intent or
     * the initial request failed.
     */
    interface PaymentHandler {
        /**
         * Invoked once a [PaymentIntent] object has been successfully
         * created on the server and its client secret is non-null.
         */
        fun onPaymentSuccess()

        /**
         * Invoked once the server failed to create a [PaymentIntent]
         * or when the server failed to handle the initial request.
         * @see Step_5
         */
        fun onPaymentFailure()
    }

    /**
     * Purpose: handle events related to the payment session.
     * Events are as follows:
     * - When the client is trying to connect to the Stripe Server via a
     * payment session.
     * - When the client failed to connect to the network.
     * - When the client successfully selected a valid payment method.
     */
    interface UiPaymentSessionListener {

        /**
         * This method corresponds to the [PaymentSession.PaymentSessionListener.onPaymentSessionDataChanged]
         * but is only triggered when the [PaymentSessionData] is in [PaymentSessionData.isPaymentReadyToCharge]
         * state.
         *
         * @param paymentMethodDetails the selected payment method's details.
         * @see getPaymentSessionListener
         */
        fun onPaymentMethodSelected(paymentMethodDetails: String)

        /**
         *  This method corresponds to the [PaymentSession.PaymentSessionListener.onCommunicatingStateChanged]
         *  Called when network communication is beginning or ending.
         *
         * if [isCommunicating] is set to true then:
         *  update UI to indicate that network communication is in progress
         * Otherwise:
         *  update UI to indicate that network communication has completed
         *  @param isCommunicating `true` if communication is starting, `false` if it is stopping.
         */
        fun onCommunicatingStateChanged(isCommunicating: Boolean)

        /**
         * Notification method called when an error has occurred.
         * The error messages should be user -surfaceable,
         * so displaying them in an alert dialog is recommended.
         *
         * @param errorCode a network code associated with the error
         * @param errorMessage a message associated with the error
         */
        fun onError(errorCode: Int, errorMessage: String)
    }
}