package io.github.cchristou3.CyParking.data.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import io.github.cchristou3.CyParking.data.pojo.parking.lot.ParkingLot;
import io.github.cchristou3.CyParking.data.pojo.parking.slot.PrivateParkingResultSet;
import io.github.cchristou3.CyParking.data.pojo.parking.slot.booking.PrivateParkingBooking;

/**
 * Purpose: <p>contain all methods to access the (cloud / local) database's parking nodes.</p>
 *
 * @author Charalambos Christou
 * @version 3.0 21/12/20
 */
public class ParkingRepository {

    // Keys related to the parking
    public static final String LATITUDE_KEY = "latitude";
    public static final String LONGITUDE_KEY = "longitude";
    private static final String USER_ID = "userID";
    private static final String COMPLETED = "completed";
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
    }

    /**
     * Stores to the database's PRIVATE_PARKING node the specified object.
     * The document id used corresponds to the merge of the ParkingLot object's
     * coordinates and the given operator mobile number.
     *
     * @param parkingLotToBeStored Stores all necessary info about the private parking
     * @return The task to be handled in the active fragment
     * @see ParkingLot#generateUniqueId()
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
     * @return A Task<DocumentReference> object which listeners can be attached to
     */
    @NotNull
    public static Task<Void> bookParking(@NotNull PrivateParkingBooking privateParkingBookingToBeStored) {
        // Add the booking info to the database
        return FirebaseFirestore.getInstance()
                .collection(PRIVATE_PARKING_BOOKING)
                .document(privateParkingBookingToBeStored.generateUniqueId())
                .set(privateParkingBookingToBeStored);
    }

    /**
     * Deletes the specified document using the document ID
     *
     * @param idOfBookingToBeCancelled The id of the document which we want to delete
     */
    public static void cancelParking(@NotNull String idOfBookingToBeCancelled) {
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
    public static DocumentReference observeSelectedParking(@NotNull PrivateParkingResultSet selectedParking) {
        return FirebaseFirestore.getInstance().collection(PRIVATE_PARKING)
                .document(selectedParking.getDocumentID());
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
