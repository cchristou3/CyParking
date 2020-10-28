package io.github.cchristou3.CyParking.pojo.parking;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class Parking implements Parcelable {

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

    public static final Creator<Parking> CREATOR = new Creator<Parking>() {
        @Override
        public Parking createFromParcel(Parcel in) {
            return new Parking(in);
        }

        @Override
        public Parking[] newArray(int size) {
            return new Parking[size];
        }
    };

    protected Parking(Parcel in) {
        // Read the size of the passed hash map
        final int sizeOfMap = in.readInt();
        // Create a hash map
        HashMap<String, Double> mapToBeRead = new HashMap<>();
        // Traverse through the parcel
        for (int i = 0; i < sizeOfMap; i++) {
            // Access the passed hash map's entry's key-value pair
            final String key = in.readString();
            final double value = in.readDouble();
            // Add the pair to the hash map
            mapToBeRead.put(key, value);
        }
        // Set the coordinates to the value of the hash map
        setmCoordinates(mapToBeRead);
        mapToBeRead.clear();

        mParkingID = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // set up coordinates hash map as well
        HashMap<String, Double> map = getmCoordinates();
        dest.writeInt(map.size()); // store the size of the map
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            dest.writeString(entry.getKey()); // store the key
            dest.writeDouble(entry.getValue()); // store the value
        }
        dest.writeInt(mParkingID);
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

    @Override
    public int describeContents() {
        return 0;
    }


}
