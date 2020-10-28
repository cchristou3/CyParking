package io.github.cchristou3.CyParking.repository;

import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import io.github.cchristou3.CyParking.pojo.booking.PrivateParkingBooking;
import io.github.cchristou3.CyParking.pojo.parking.PrivateParking;

public class ParkingRepository {

    // Keys related to the parking
    public static final String LATITUDE_KEY = "latitude";
    public static final String LONGITUDE_KEY = "longitude";
    private static final String PRICING_LIST_KEY = "PricingList";
    private static final String CAPACITY_KEY = "Capacity";
    private static final String AVAILABLE_SPACES_KEY = "AvailableSpaces";
    private static final String CAPACITY_FOR_DISABLED_KEY = "CapacityForDisabled";
    private static final String AVAILABLE_SPACES_FOR_DISABLED_KEY = "AvailableSpacesForDisabled";
    private static final String OPENING_HOURS_KEY = "OpeningHours";
    private static final String COORDINATES_KEY = "Coordinates";
    private static final String PARKING_ID_KEY = "ParkingID";

    // Keys related to booking a parking
    private static final String PARKING_OPERATOR_ID_KEY = "mParkingOperatorID";
    private static final String PARKING_NAME_KEY = "mParkingName";
    private static final String USER_ID_KEY = "mUserID";
    private static final String USERNAME_KEY = "mUsername";
    private static final String DATE_KEY = "mDate";
    private static final String STARTING_TIME_KEY = "mStartingTime";
    private static final String ENDING_TIME_KEY = "mEndingTime";
    private static final String PRICE_KEY = "mPrice";

    // Firebase Firestore paths (nodes)
    private static final String PRIVATE_PARKING = "private_parking";
    private static final String PRIVATE_PARKING_BOOKING = "private_parking_bookings";

    public static void addParking(@NotNull PrivateParking privateParking) {
        // Create a new hash map object to hold the values of the parking to be added
        HashMap<String, Object> dataToSave = new HashMap<>();
        dataToSave.put(PRICING_LIST_KEY, privateParking.getmPricingList());
        dataToSave.put(CAPACITY_KEY, privateParking.getmCapacity());
        dataToSave.put(AVAILABLE_SPACES_KEY, privateParking.getmAvailableSpaces());
        dataToSave.put(CAPACITY_FOR_DISABLED_KEY, privateParking.getmCapacityForDisabled());
        dataToSave.put(AVAILABLE_SPACES_FOR_DISABLED_KEY, privateParking.getmAvailableSpacesForDisabled());
        dataToSave.put(OPENING_HOURS_KEY, privateParking.getmOpeningHours());
        dataToSave.put(COORDINATES_KEY, privateParking.getmCoordinates());
        dataToSave.put(PARKING_ID_KEY, privateParking.getmParkingID());

        // Add the info to the database
        FirebaseFirestore.getInstance().collection(PRIVATE_PARKING).add(dataToSave);
    }

    public void bookParking(@NotNull PrivateParkingBooking privateParkingBooking) {
        // Create a new hash map object to hold the values of the parking to be booked
        HashMap<String, Object> dataToSave = new HashMap<>();
        dataToSave.put(PARKING_OPERATOR_ID_KEY, privateParkingBooking.getmParkingOperatorID());
        dataToSave.put(PARKING_NAME_KEY, privateParkingBooking.getmParkingName());
        dataToSave.put(USER_ID_KEY, privateParkingBooking.getmUserID());
        dataToSave.put(USERNAME_KEY, privateParkingBooking.getmUsername());
        dataToSave.put(DATE_KEY, privateParkingBooking.getmDate());
        dataToSave.put(STARTING_TIME_KEY, privateParkingBooking.getmStartingTime());
        dataToSave.put(ENDING_TIME_KEY, privateParkingBooking.getmEndingTime());
        dataToSave.put(PRICE_KEY, privateParkingBooking.getmPrice());

        // Add the info to the database
        FirebaseFirestore.getInstance().collection(PRIVATE_PARKING_BOOKING).add(dataToSave);
    }


    public static void addDummyParkingData() {
        // (HashMap<String, Double> mCoordinates, int mParkingID, List<Integer> mPricingList, int mCapacity, int mAvailalbleSpaces, int mCapacityForDisabled, int mAvailalbleSpacesForDisabled, String mOpeningHours)
        List<PrivateParking> privateParkingList = new ArrayList<>(Arrays.asList(
                new PrivateParking(new HashMap<String, Double>() {
                    {
                        put(LATITUDE_KEY, 34.9214056);
                        put(LONGITUDE_KEY, 33.621935);
                    }
                }, 1, new ArrayList<Integer>(), 100, 50, 10, 5, "9:00-16:00"),
                new PrivateParking(new HashMap<String, Double>() {
                    {
                        put(LATITUDE_KEY, 34.9214672);
                        put(LONGITUDE_KEY, 33.6227833);
                    }
                }, 1, new ArrayList<Integer>(), 100, 50, 10, 5, "9:00-16:00"),
                new PrivateParking(new HashMap<String, Double>() {
                    {
                        put(LATITUDE_KEY, 34.9210801);
                        put(LONGITUDE_KEY, 33.6236309);
                    }
                }, 1, new ArrayList<Integer>(), 100, 50, 10, 5, "9:00-16:00"),
                new PrivateParking(new HashMap<String, Double>() {
                    {
                        put(LATITUDE_KEY, 34.0);
                        put(LONGITUDE_KEY, 30.0);
                    }
                }, 1, new ArrayList<Integer>(), 100, 50, 10, 5, "9:00-16:00"),
                new PrivateParking(new HashMap<String, Double>() {
                    {
                        put(LATITUDE_KEY, 34.0);
                        put(LONGITUDE_KEY, 30.0);
                    }
                }, 1, new ArrayList<Integer>(), 100, 50, 10, 5, "9:00-16:00"),
                new PrivateParking(new HashMap<String, Double>() {
                    {
                        put(LATITUDE_KEY, 34.0);
                        put(LONGITUDE_KEY, 30.0);
                    }
                }, 1, new ArrayList<Integer>(), 100, 50, 10, 5, "9:00-16:00")));
        for (PrivateParking parking : privateParkingList) {
            addParking(parking);
        }
    }
}
