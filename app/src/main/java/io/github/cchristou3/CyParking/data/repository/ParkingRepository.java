package io.github.cchristou3.CyParking.data.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import io.github.cchristou3.CyParking.data.pojo.parking.lot.ParkingLot;
import io.github.cchristou3.CyParking.data.pojo.parking.slot.booking.PrivateParkingBooking;

/**
 * Purpose: <p>contain all methods to access the (cloud / local) database's parking nodes.</p>
 *
 * @author Charalambos Christou
 * @version 6.0 28/12/20
 */
public class ParkingRepository {

    // Keys related to the ParkingLot objects
    public static final String LATITUDE_KEY = "latitude";
    public static final String LONGITUDE_KEY = "longitude";

    // Keys related to the database fields
    private static final String USER_ID = "userID";
    private static final String COMPLETED = "completed";
    private static final String OPERATOR_EMAIL = "operatorEmail";
    private static final String AVAILABLE_SPACES = "availableSpaces";

    // Firebase Firestore paths (nodes)
    private static final String PRIVATE_PARKING = "private_parking";
    private static final String PRIVATE_PARKING_BOOKING = "private_parking_bookings";

    /**
     * Returns the bookings of the specified userId,
     * starting from the "Pending" ones and finishing with the "Completed" ones
     *
     * @param userId The is of the Firebase user
     * @return A query which returns all the bookings of the specified userId
     */
    @NotNull
    public static Query retrieveUserBookings(String userId) {
        return FirebaseFirestore.getInstance()
                .collection(PRIVATE_PARKING_BOOKING)
                .whereEqualTo(USER_ID, userId)
                .orderBy(COMPLETED, Query.Direction.ASCENDING); // Show pending bookings first
        // TODO: Make a HTTP request - filter data (get only pending bookings) on the server and send to the client
    }

    /**
     * An observer is attached to the current collection, to listen for changes
     * concerning the parking lots (available spaces, prices).
     * Removal of observer is self-managed by the hosting activity.
     *
     * @return The {@link CollectionReference} reference to be observed.
     */
    @NotNull
    public static CollectionReference observeAllParkingLots() {
        return FirebaseFirestore.getInstance().collection(PRIVATE_PARKING);
    }

    /**
     * Stores to the database's PRIVATE_PARKING node the specified object.
     * The document id used corresponds to the merge of the ParkingLot object's
     * coordinates and the given operator mobile number.
     *
     * @param parkingLotToBeStored Stores all necessary info about the private parking
     * @return The task to be handled in the active fragment
     * @throws NullPointerException in case the continuation returns null
     * @see ParkingLot#generateUniqueId()
     * @see Task#getException()
     */
    @NotNull
    public static Task<Void> registerParkingLot(@NotNull ParkingLot parkingLotToBeStored) {
        // Add the info to the database
        return FirebaseFirestore.getInstance().collection(PRIVATE_PARKING)
                .document(parkingLotToBeStored.generateUniqueId())
                .get()
                .continueWithTask(task -> {
                    // If the task was successful, then the document already exists
                    // within the database.
                    if (task.isSuccessful() && task.getResult().getData() != null) {
                        return null; // Do not do any more tasks
                        // Returning null will result into a NullPointerException("Continuation returned null")
                        // As the continueWithTask method cannot return null.
                        // Thus, in fragment check for this kind of exception and handle it appropriately
                        // TODO: what if the task failed?
                    } else {
                        // Add it to the database
                        return FirebaseFirestore.getInstance()
                                .collection(PRIVATE_PARKING)
                                .document(parkingLotToBeStored.generateUniqueId())
                                .set(parkingLotToBeStored);
                    }
                });
    }

    /**
     * Stores the specified object to the database's PRIVATE_PARKING_BOOKING node.
     *
     * @param privateParkingBookingToBeStored Holds all necessary info about a booking of a private parking
     * @return A Task<Void> object to be handled in the calling fragment.
     */
    @NotNull
    private static Task<Void> bookParkingSlot(@NotNull PrivateParkingBooking privateParkingBookingToBeStored) {
        // Add the booking info to the database
        return FirebaseFirestore.getInstance()
                .collection(PRIVATE_PARKING_BOOKING)
                .document(privateParkingBookingToBeStored.generateUniqueId())
                .set(privateParkingBookingToBeStored);
    }

    /**
     * Stores the specified object to the database's PRIVATE_PARKING_BOOKING node.
     * The user has the option to check whether or not to check of this booking has already been made.
     * If specified, the booking document is queried from the database based on the booking's id.
     * If a document is returned, then it already exists. Otherwise, if null was returned, it indicates
     * that the booking does not exist in the database.
     * For the latter, the booking gets then stored to the database.
     *
     * @param booking              Holds all necessary info about a booking of a private parking
     * @param checkIfAlreadyExists Indicates whether or not to check of this booking already exists in the database.
     * @return A Task<Void> object to be handled in the calling fragment.
     * @throws NullPointerException in case the continuation returns null
     * @see Task#getException()
     */
    public static Task<Void> bookParkingSlot(@NotNull PrivateParkingBooking booking, boolean checkIfAlreadyExists) {
        if (checkIfAlreadyExists) {
            return FirebaseFirestore.getInstance().collection(PRIVATE_PARKING_BOOKING)
                    .document(booking.generateUniqueId()).get()
                    .continueWithTask(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().getData() == null) { // If it does not already exist
                                // Store to the database
                                return bookParkingSlot(booking);
                            }
                        }
                        return null; // This should throw a NullPointerException (to be handled by the View).
                    });
        } else {
            // Store to the database immediately
            return bookParkingSlot(booking);
        }
    }

    /**
     * Deletes the specified document using the document ID
     *
     * @param idOfBookingToBeCancelled The id of the document which we want to delete
     */
    public static void cancelParkingBooking(@NotNull String idOfBookingToBeCancelled) {
        // Delete the booking info to the database
        FirebaseFirestore.getInstance()
                .collection(PRIVATE_PARKING_BOOKING)
                .document(idOfBookingToBeCancelled).delete();
    }

    /**
     * An observer is attached to the current document, to listen for changes (number of available spaces).
     * Removal of observer is self-managed by the hosting activity.
     *
     * @param selectedParking The Parking whose changes will be listen to.
     * @return The {@link DocumentReference} reference to be observed.
     */
    @NotNull
    public static DocumentReference observeParkingLot(@NotNull ParkingLot selectedParking) {
        return FirebaseFirestore.getInstance().collection(PRIVATE_PARKING)
                .document(selectedParking.generateUniqueId());
    }

    /**
     * Returns the operator's parking lot based on his/hers email address.
     *
     * @param email The email address of the operator.
     * @return A query that returns the parking lot of the operator with the specified email.
     */
    @NotNull
    public static Query observeParkingLot(String email) {
        return observeAllParkingLots()
                .whereEqualTo(OPERATOR_EMAIL, email).limit(1L);
    }

    /**
     * Increases the the number of available spaces of the current lot reference.
     *
     * @param lotReference A DocumentReference of the lot
     */
    public static void incrementAvailableSpacesOf(@NotNull final DocumentReference lotReference) {
        lotReference.update(AVAILABLE_SPACES, FieldValue.increment(1));
    }

    /**
     * Decreases the the number of available spaces of the current lot reference.
     *
     * @param lotReference A DocumentReference of the lot
     */
    public static void decrementAvailableSpacesOf(@NotNull final DocumentReference lotReference) {
        lotReference.update(AVAILABLE_SPACES, FieldValue.increment(-1));
    }

    /**
     * Adds hard-coded data to the firebase's PRIVATE_PARKING node.
     * Used for testing.
     */
    public static void addDummyParkingData() {
        List<ParkingLot> parkingLotList = new ArrayList<>(Arrays.asList(
                new ParkingLot(new HashMap<String, Double>() {{
                    put(LATITUDE_KEY, 34.9214056);
                    put(LONGITUDE_KEY, 33.621935);
                }}, "99999999", "A@gmail.com"),
                new ParkingLot(new HashMap<String, Double>() {{
                    put(LATITUDE_KEY, 34.9214672);
                    put(LONGITUDE_KEY, 33.6227833);
                }}, "88888888", "B@gmail.com"),
                new ParkingLot(new HashMap<String, Double>() {{
                    put(LATITUDE_KEY, 34.9210801);
                    put(LONGITUDE_KEY, 33.6236309);
                }}, "77777777", "C@gmail.com"),
                new ParkingLot(new HashMap<String, Double>() {{
                    put(LATITUDE_KEY, 34.921800);
                    put(LONGITUDE_KEY, 33.623560);
                }}, "66666666", "D@gmail.com"))
        );
        for (ParkingLot parking : parkingLotList) {
            registerParkingLot(parking);
        }
    }
}
