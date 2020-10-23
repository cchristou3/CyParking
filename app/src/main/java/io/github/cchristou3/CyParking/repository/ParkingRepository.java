package io.github.cchristou3.CyParking.repository;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import io.github.cchristou3.CyParking.pojo.parking.PrivateParking;

public class ParkingRepository {

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

    public static void addParking(PrivateParking privateParking) {
        HashMap<String, Object> dataToSave = new HashMap<>();
        dataToSave.put(PRICING_LIST_KEY, privateParking.getmPricingList());
        dataToSave.put(CAPACITY_KEY, privateParking.getmCapacity());
        dataToSave.put(AVAILABLE_SPACES_KEY, privateParking.getmAvailableSpaces());
        dataToSave.put(CAPACITY_FOR_DISABLED_KEY, privateParking.getmCapacityForDisabled());
        dataToSave.put(AVAILABLE_SPACES_FOR_DISABLED_KEY, privateParking.getmAvailableSpacesForDisabled());
        dataToSave.put(OPENING_HOURS_KEY, privateParking.getmOpeningHours());
        dataToSave.put(COORDINATES_KEY, privateParking.getmCoordinates());
        dataToSave.put(PARKING_ID_KEY, privateParking.getmParkingID());

        FirebaseFirestore.getInstance().collection("private_parking").add(dataToSave);
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
