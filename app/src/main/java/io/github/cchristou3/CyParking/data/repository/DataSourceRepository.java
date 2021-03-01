package io.github.cchristou3.CyParking.data.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableReference;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import kotlin.NotImplementedError;

/**
 * Purpose: Provide to the application repositories
 * interfaces for communicating with the cloud Firestore
 * and accessing cloud functions.
 *
 * <strong>Note:</strong> all interfaces are package-private so that
 * only the repository classes can access them.
 *
 * @author Charalambos Christou
 * @version 4.0 27/02/21
 */
class DataSourceRepository {

    // Cloud functions
    private static final String FILTER_LOCATIONS = "filterLocations";
    private static final String GET_NEARBY_PARKING_LOTS = "getNearbyParkingLots";
    // Firebase Firestore paths (nodes)
    private static final String PARKING_LOTS = "parking_lots";

    // Additional fields for payments...
    // TODO: 06/02/2021  add database nodes related to payments
    private static final String BOOKINGS = "bookings";
    private static final String FEEDBACK = "feedback";
    private static final String USERS = "users";

    /**
     * Private access constructor.
     */
    private DataSourceRepository() {
    }

    /**
     * Get a reference to the database node with the given name.
     *
     * @param node The name of the node in the database.
     * @return A {@link CollectionReference} corresponding to the node
     * specified by the given name.
     */
    @NotNull
    private static CollectionReference getDatabaseNodeByName(String node) {
        return FirebaseFirestore.getInstance().collection(node);
    }

    /**
     * Get a reference to the cloud function of the given name.
     *
     * @param functionName The name of the cloud function.
     * @return A {@link HttpsCallableReference} of the cloud function.
     */
    @NotNull
    private static HttpsCallableReference getCallableFunctionByName(String functionName) {
        return FirebaseFunctions.getInstance().getHttpsCallable(functionName);
    }

    /**
     * Purpose: provide the implementer access to the database's
     * feedback node.
     *
     * @see #FEEDBACK
     */
    interface FeedbackHandler {
        /**
         * Access the feedback node of the database.
         *
         * @return A {@link CollectionReference} of the feedback node.
         */
        default CollectionReference getFeedbackRef() {
            return getDatabaseNodeByName(FEEDBACK);
        }
    }

    /**
     * Purpose: provide the implementer access to the database's
     * bookings node.
     *
     * @see #BOOKINGS
     */
    interface BookingHandler {

        // Database fields
        String COMPLETED = "completed";
        String BOOKING_USER_ID = "bookingUserId";
        String BOOKING_DETAILS = "bookingDetails";

        /**
         * Access the bookings node of the database.
         *
         * @return A {@link CollectionReference} of the booking node.
         */
        default CollectionReference getBookingsRef() {
            return getDatabaseNodeByName(BOOKINGS);
        }
    }

    /**
     * Purpose: provide the implementer access to the database's
     * parking lots node.
     *
     * @see #PARKING_LOTS
     */
    interface ParkingLotHandler {

        // Database fields
        String AVAILABILITY = "availability";
        String OPERATOR_ID = "operatorId";
        String AVAILABLE_SPACES = "availableSpaces";

        /**
         * Access the parking lot node of the database.
         *
         * @return A {@link CollectionReference} of the parking lot node.
         */
        default CollectionReference getParkingLotsRef() {
            return getDatabaseNodeByName(PARKING_LOTS);
        }
    }

    /**
     * Purpose: provide the implementer access to the database's
     * users node.
     *
     * @see #USER_DISPLAY_NAME
     */
    interface UserHandler {

        // Database fields
        String USER_DISPLAY_NAME = "displayName";

        /**
         * Access the users node of the database.
         *
         * @return A {@link CollectionReference} of the users node.
         */
        default CollectionReference getUserRef() {
            return getDatabaseNodeByName(USERS);
        }
    }

    /**
     * Purpose: provide the implementer access to the database's
     * cloud functions.
     *
     * @see FirebaseFunctions#getHttpsCallable(String)
     */
    interface CloudFunctionCaller {

        String LATITUDE = "latitude";
        String LONGITUDE = "longitude";

        /**
         * Access the users node of the database.
         *
         * @return A {@link HttpsCallableReference} of the users node.
         */
        default Task<HttpsCallableResult> callNearbyParkingLotsFunction(double userLatitude, double userLongitude) {
            return getCallableFunctionByName(GET_NEARBY_PARKING_LOTS)
                    .call(new HashMap<String, Double>() {{ // The request's data.
                        put(LATITUDE, userLatitude);
                        put(LONGITUDE, userLongitude);
                    }});
        }

        default HttpsCallableReference getFilterLocationsFunction() {
            return getCallableFunctionByName(FILTER_LOCATIONS);
        }

        // TODO: 06/02/2021 Add cloud functions related to payments
        default HttpsCallableReference getPaymentIntent() {
            throw new NotImplementedError();
        }
    }

    /**
     * Purpose: provide the implementer access to the Firebase
     * storage.
     *
     * @see FirebaseStorage#getReference()
     */
    interface StorageHandler {
        String LOT_PHOTOS = "parking_lot_photos";

        /**
         * Access the operators Firebase storage.
         *
         * @return A {@link StorageReference} of the operators' Firebase storage.
         */
        default StorageReference getLotPhotosStorageRef() {
            return FirebaseStorage.getInstance().getReference().child(LOT_PHOTOS);
        }
    }
}
