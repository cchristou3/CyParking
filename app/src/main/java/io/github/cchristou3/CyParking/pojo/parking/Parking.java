package io.github.cchristou3.CyParking.pojo.parking;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class Parking {

    @SerializedName("Coordinates")
    private HashMap<String, Double> mCoordinates;
    @SerializedName("ParkingID")
    private int mParkingID;

    Parking() {
    }

    public Parking(HashMap<String, Double> mCoordinates, int mParkingID) {
        this.mCoordinates = mCoordinates;
        this.mParkingID = mParkingID;
    }

    public HashMap<String, Double> getmCoordinates() {
        return mCoordinates;
    }

    public void setmCoordinates(HashMap<String, Double> mCoordinates) {
        this.mCoordinates = mCoordinates;
    }

    public int getmParkingID() {
        return mParkingID;
    }

    public void setmParkingID(int mParkingID) {
        this.mParkingID = mParkingID;
    }
}
