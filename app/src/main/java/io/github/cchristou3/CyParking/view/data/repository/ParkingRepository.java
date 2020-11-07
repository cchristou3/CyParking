package io.github.cchristou3.CyParking.view.data.repository;

import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import io.github.cchristou3.CyParking.view.data.pojo.parking.PrivateParking;
import io.github.cchristou3.CyParking.view.data.pojo.parking.PrivateParkingResultSet;
import io.github.cchristou3.CyParking.view.data.pojo.parking.booking.PrivateParkingBooking;

/**
 * Purpose: <p>contain all methods to access cloud / local database</p>
 *
 * @author Charalambos Christou
 * @version 2.0 07/11/20
 */
public class ParkingRepository {

    // Keys related to the parking
    public static final String LATITUDE_KEY = "latitude";
    public static final String LONGITUDE_KEY = "longitude";
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
                .collection("private_parking_bookings")
                .whereEqualTo("userID", userId)
                .orderBy("completed", Query.Direction.ASCENDING); // Show pending bookings first
    }

    /**
     * Stores to the database's PRIVATE_PARKING node the specified object
     *
     * @param privateParkingToBeStored Stores all necessary info about the private parking
     */
    public static void addParking(@NotNull PrivateParking privateParkingToBeStored) {
        // Add the info to the database
        FirebaseFirestore.getInstance().collection(PRIVATE_PARKING).document(privateParkingToBeStored.generateUniqueId()).set(privateParkingToBeStored);
    }

    /**
     * Stores the specified object to the database's PRIVATE_PARKING_BOOKING node.
     *
     * @param privateParkingBookingToBeStored Holds all necessary info about a booking of a private parking
     * @return A Task<DocumentReference> object which listeners can be attached to
     */
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
     * @param parkingAvailability The TextView which will get updated.
     * @param selectedParking     The Parking whose changes will be listen to.
     * @param owner               The Activity which will handle the listener's removal.
     */
    public static void observeSelectedParking(TextView parkingAvailability, PrivateParkingResultSet selectedParking, FragmentActivity owner) {
        FirebaseFirestore.getInstance().collection("private_parking")
                .document(selectedParking.getDocumentID())
                .addSnapshotListener(owner, (value, error) -> {
                    if (error != null) return;
                    final String updatedParkingSlots = "AvailableSpaces: " + Objects.requireNonNull(Objects.requireNonNull(value)
                            .toObject(PrivateParking.class)).getAvailableSpaces();

                    parkingAvailability.setText(updatedParkingSlots);
                });
    }

    /**
     * Adds hard-coded data to the firebase's PRIVATE_PARKING node.
     * Used for testing.
     */
    public static void addDummyParkingData() {
        List<PrivateParking> privateParkingList = new ArrayList<>(Arrays.asList(
                new PrivateParking(new HashMap<String, Double>() {
                    {
                        put(LATITUDE_KEY, 34.9214056);
                        put(LONGITUDE_KEY, 33.621935);
                    }
                }, 1, 100, 50, 10, 5, "9:00-16:00", new ArrayList<>()),
                new PrivateParking(new HashMap<String, Double>() {
                    {
                        put(LATITUDE_KEY, 34.9214672);
                        put(LONGITUDE_KEY, 33.6227833);
                    }
                }, 1, 100, 50, 10, 5, "9:00-16:00", new ArrayList<>()),
                new PrivateParking(new HashMap<String, Double>() {
                    {
                        put(LATITUDE_KEY, 34.9210801);
                        put(LONGITUDE_KEY, 33.6236309);
                    }
                }, 1, 100, 50, 10, 5, "9:00-16:00", new ArrayList<>()),
                new PrivateParking(new HashMap<String, Double>() {
                    {
                        put(LATITUDE_KEY, 34.921800);
                        put(LONGITUDE_KEY, 33.623560);
                    }
                }, 1, 100, 50, 10, 5, "9:00-16:00", new ArrayList<>())
        ));
        for (PrivateParking parking : privateParkingList) {
            addParking(parking);
        }
    }
}
