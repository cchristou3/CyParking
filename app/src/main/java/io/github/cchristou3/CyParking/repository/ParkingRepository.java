package io.github.cchristou3.CyParking.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import io.github.cchristou3.CyParking.pojo.parking.PrivateParking;
import io.github.cchristou3.CyParking.pojo.parking.booking.PrivateParkingBooking;

/**
 * purpose: contain all methods to access cloud / local database
 *
 * @author Charalambos Christou
 * @version 1.0 29/10/20
 */
public class ParkingRepository {

    // Firebase Firestore paths (nodes)
    private static final String PRIVATE_PARKING = "private_parking";
    private static final String PRIVATE_PARKING_BOOKING = "private_parking_bookings";

    /**
     * Stores to the database's PRIVATE_PARKING node the specified object
     *
     * @param privateParkingToBeStored Stores all necessary info about the private parking
     */
    public static void addParking(@NotNull PrivateParking privateParkingToBeStored) {
        // Add the info to the database
        FirebaseFirestore.getInstance().collection(PRIVATE_PARKING).add(privateParkingToBeStored);
    }

    /**
     * Stores the specified object to the database's PRIVATE_PARKING_BOOKING node.
     *
     * @param privateParkingBookingToBeStored Holds all necessary info about a booking of a private parking
     * @return A Task<DocumentReference> object which listeners can be attached to
     */
    public static Task<DocumentReference> bookParking(@NotNull PrivateParkingBooking privateParkingBookingToBeStored) {
        // Add the booking info to the database
        return FirebaseFirestore.getInstance().collection(PRIVATE_PARKING_BOOKING).add(privateParkingBookingToBeStored);
    }

    /**
     * Deletes the specified document using the document ID
     *
     * @param idOfBookingToBeCancelled The id of the document which we want to delete
     */
    public static void cancelParking(@NotNull String idOfBookingToBeCancelled) {
        // Delete the booking info to the database
        FirebaseFirestore.getInstance().collection(PRIVATE_PARKING_BOOKING).document(idOfBookingToBeCancelled).delete();
    }

    // Keys related to the parking
    public static final String LATITUDE_KEY = "latitude";
    public static final String LONGITUDE_KEY = "longitude";

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
                        put(LATITUDE_KEY, 34.0);
                        put(LONGITUDE_KEY, 30.0);
                    }
                }, 1, 100, 50, 10, 5, "9:00-16:00", new ArrayList<>()),
                new PrivateParking(new HashMap<String, Double>() {
                    {
                        put(LATITUDE_KEY, 34.0);
                        put(LONGITUDE_KEY, 30.0);
                    }
                }, 1, 100, 50, 10, 5, "9:00-16:00", new ArrayList<>()),
                new PrivateParking(new HashMap<String, Double>() {
                    {
                        put(LATITUDE_KEY, 34.0);
                        put(LONGITUDE_KEY, 30.0);
                    }
                }, 1, 100, 50, 10, 5, "9:00-16:00", new ArrayList<>())));
        for (PrivateParking parking : privateParkingList) {
            addParking(parking);
        }
    }
}
