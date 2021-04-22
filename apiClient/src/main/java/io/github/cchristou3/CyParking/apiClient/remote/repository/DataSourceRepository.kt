package io.github.cchristou3.CyParking.apiClient.remote.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableReference
import com.google.firebase.functions.HttpsCallableResult
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

/**
 * Purpose: Provide to the application repositories
 * interfaces for communicating with the cloud Firestore
 * and accessing cloud functions.
 *
 * **Note:** all interfaces are [internal] so that
 * only the repository classes can access them.
 *
 * @author Charalambos Christou
 * @version 6.0 22/04/21
 * @see <a href='https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-default/'>Interface default implementation</a>
 */
internal object DataSourceRepository {

    // Cloud functions
    private const val GET_NEARBY_PARKING_LOTS = "getNearbyParkingLots"
    private const val UPDATE_EMAIL = "updateEmail"
    private const val CREATE_EPHEMERAL_KEY = "createEphemeralKey"

    // Firebase Firestore paths (nodes)
    private const val BOOKINGS = "bookings"
    private const val FEEDBACK = "feedback"
    private const val USERS = "users"
    private const val PARKING_LOTS = "parking_lots"

    // Additional fields for payments...
    private const val STRIPE_CUSTOMERS = "stripe_customers"

    /**
     * Get a reference to the database node with the given name.
     *
     * @param node The name of the node in the database.
     * @return A [CollectionReference] corresponding to the node
     * specified by the given name.
     */
    private fun getDatabaseNodeByName(node: String): CollectionReference {
        return FirebaseFirestore.getInstance().collection(node)
    }

    /**
     * Get a reference to the cloud function of the given name.
     *
     * @param functionName The name of the cloud function.
     * @return A [HttpsCallableReference] of the cloud function.
     */
    private fun getCallableFunctionByName(functionName: String): HttpsCallableReference {
        return FirebaseFunctions.getInstance().getHttpsCallable(functionName)
    }

    /**
     * Purpose: provide the implementer access to the database's
     * feedback node.
     *
     * @see .FEEDBACK
     */
    internal interface FeedbackHandler {
        /**
         * Access the feedback node of the database.
         *
         * @return A [CollectionReference] of the feedback node.
         */
        @JvmDefault
        val feedbackRef: CollectionReference
            get() = getDatabaseNodeByName(FEEDBACK)
    }

    /**
     * Purpose: provide the implementer access to the database's
     * bookings node.
     *
     * @see .BOOKINGS
     */
    internal interface BookingHandler {
        /**
         * Access the bookings node of the database.
         *
         * @return A [CollectionReference] of the booking node.
         */
        @JvmDefault
        val bookingsRef: CollectionReference
            get() = getDatabaseNodeByName(BOOKINGS)

        /**
         * Returns the pending bookings of the user with the specified userId.
         *
         * @param userId The is of the Firebase user
         * @return A query which returns all the bookings of the specified userId
         */
        @JvmDefault
        fun getUserUpcomingBookings(userId: String?): Query {
            return bookingsRef
                    .whereEqualTo(BOOKING_USER_ID, userId)
                    .whereEqualTo("$BOOKING_DETAILS.$COMPLETED", false)
        }

        companion object {
            // Database fields
            const val COMPLETED = "completed"
            const val BOOKING_USER_ID = "bookingUserId"
            const val BOOKING_DETAILS = "bookingDetails"
            const val DATE_OF_BOOKING = "dateOfBooking"
            const val STARTING_TIME = "startingTime"
            const val HOUR = "hour"
            const val MINUTE = "minute"
        }
    }

    /**
     * Purpose: provide the implementer access to the database's
     * parking lots node.
     *
     * @see .PARKING_LOTS
     */
    internal interface ParkingLotHandler {
        /**
         * Access the parking lot node of the database.
         *
         * @return A [CollectionReference] of the parking lot node.
         */
        @JvmDefault
        val parkingLotsRef: CollectionReference
            get() = getDatabaseNodeByName(PARKING_LOTS)

        companion object {
            // Database fields
            const val AVAILABILITY = "availability"
            const val OPERATOR_ID = "operatorId"
            const val AVAILABLE_SPACES = "availableSpaces"
        }
    }

    /**
     * Purpose: provide the implementer access to the database's
     * users node.
     *
     * @see .USER_DISPLAY_NAME
     */
    internal interface UserHandler {
        /**
         * Access the users node of the database.
         *
         * @return A [CollectionReference] of the users node.
         */
        @JvmDefault
        val userRef: CollectionReference
            get() = getDatabaseNodeByName(USERS)

        companion object {
            // Database fields
            const val USER_DISPLAY_NAME = "displayName"
        }
    }

    /**
     * Purpose: provide the implementer access to the database's
     * stripe customers node.
     */
    internal interface StripeCustomerHandler {
        /**
         * Access the users node of the database.
         *
         * @return A [CollectionReference] of the users node.
         */
        @JvmDefault
        val stripeCustomerRef: CollectionReference
            get() = getDatabaseNodeByName(STRIPE_CUSTOMERS)

        /**
         * Prepare the data that will be sent to the server to
         * create a payment intent.
         * @param slotOfferIndex the index of the selected offer.
         * @param lotDocId the document id of the lot in Firestore.
         * @param currency the currency of the payment.
         * @return a [HashMap] of the arguments.
         */
        fun preparePaymentDetails(lotDocId: String, slotOfferIndex: Int, currency: String): HashMap<String, Any> {
            return hashMapOf(
                    LOT_DOC_ID to lotDocId,
                    SLOT_OFFER_INDEX to slotOfferIndex,
                    CURRENCY to currency)
        }

        companion object {
            const val PAYMENTS = "payments"
            const val SLOT_OFFER_INDEX = "slotOfferIndex"
            const val CURRENCY = "currency"
            const val LOT_DOC_ID = "lotDocId"
        }
    }

    /**
     * Purpose: provide the implementer access to the database's
     * cloud functions.
     *
     * @see FirebaseFunctions.getHttpsCallable
     */
    internal interface CloudFunctionCaller {
        /**
         * Call a cloud function that will collect and return
         * all parking lot document ids that are nearby the given
         * coordinates.
         *
         * @param userLatitude the user's latitude.
         * @param userLongitude the user's longitude.
         * @return A [Task] of the above description.
         */
        @JvmDefault
        fun callNearbyParkingLotsFunction(userLatitude: Double, userLongitude: Double): Task<HttpsCallableResult?> {
            return getCallableFunctionByName(GET_NEARBY_PARKING_LOTS)
                    .call(object : HashMap<String?, Double?>() {
                        init { // The request's data.
                            put(LATITUDE, userLatitude)
                            put(LONGITUDE, userLongitude)
                        }
                    })
        }

        /**
         * Call a cloud function that will update the user's email in the database.
         *
         * @param newEmail The new email of the user
         * @param oldEmail The old email of the user
         * @param userId  The user id
         * @return A [Task] of the above description.
         */
        @JvmDefault
        fun callUpdateEmailFunction(userId: String, oldEmail: String, newEmail: String): Task<HttpsCallableResult?> {
            return getCallableFunctionByName(UPDATE_EMAIL)
                    .call(object : java.util.HashMap<String?, String?>() {
                        init {
                            put(NEW_EMAIL, newEmail)
                            put(OLD_EMAIL, oldEmail)
                            put(USER_ID, userId)
                        }
                    })
        }

        /**
         * Call a cloud function that will create an [EphemeralKey].
         *
         * @return A [Task] of the above description.
         */
        @JvmDefault
        fun callEphemeralKeyCreationFunction(apiVersion: String): Task<HttpsCallableResult?>? {
            return getCallableFunctionByName(CREATE_EPHEMERAL_KEY)
                    .call(hashMapOf(API_VERSION to apiVersion))
        }

        companion object {
            const val LATITUDE = "latitude"
            const val LONGITUDE = "longitude"
            const val API_VERSION = "api_version"
            const val NEW_EMAIL = "newEmail"
            const val OLD_EMAIL = "oldEmail"
            const val USER_ID = "userId"

        }
    }

    /**
     * Purpose: provide the implementer access to the Firebase
     * storage.
     *
     * @see FirebaseStorage.getReference
     */
    internal interface StorageHandler {
        /**
         * Access the operators Firebase storage.
         *
         * @return A [StorageReference] of the operators' Firebase storage.
         */
        @JvmDefault
        val lotPhotosStorageRef: StorageReference
            get() = FirebaseStorage.getInstance().reference.child(LOT_PHOTOS)

        companion object {
            const val LOT_PHOTOS = "parking_lot_photos"
        }
    }
}