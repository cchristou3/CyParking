package io.github.cchristou3.CyParking.view.data.pojo.parking;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

/**
 * Purpose: <p>POJO to be used to transfer and receive data
 * via activities / fragments and HTTPS requests.
 * This is a Base class.</p>
 *
 * @author Charalambos Christou
 * @version 2.0 29/10/20
 */
public class Parking implements Parcelable {

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
    @SerializedName("coordinates")
    private HashMap<String, Double> coordinates;
    @SerializedName("parkingID")
    private int parkingID;

    public Parking() {
    }

    public Parking(HashMap<String, Double> coordinates, int parkingID) {
        this.coordinates = coordinates;
        this.parkingID = parkingID;
    }

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
        setCoordinates(mapToBeRead);
        mapToBeRead.clear();

        parkingID = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // set up coordinates hash map as well
        HashMap<String, Double> map = getCoordinates();
        dest.writeInt(map.size()); // store the size of the map
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            dest.writeString(entry.getKey()); // store the key
            dest.writeDouble(entry.getValue()); // store the value
        }
        dest.writeInt(parkingID);
    }

    public HashMap<String, Double> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(HashMap<String, Double> coordinates) {
        this.coordinates = coordinates;
    }

    public int getParkingID() {
        return parkingID;
    }

    public void setParkingID(int parkingID) {
        this.parkingID = parkingID;
    }

    @Override
    public int describeContents() {
        return 0;
    }


}
