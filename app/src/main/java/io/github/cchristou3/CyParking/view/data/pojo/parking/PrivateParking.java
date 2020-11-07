package io.github.cchristou3.CyParking.view.data.pojo.parking;

import android.os.Parcel;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.github.cchristou3.CyParking.view.utility.ShaUtility;

/**
 * Purpose: <p>POJO to be used to transfer and receive data
 * via activities / fragments and HTTPS requests. Further,
 * associated with a Google Maps Marker in the ParkingMapFragment.
 * This is a Subclass of Parking.</p>
 *
 * @author Charalambos Christou
 * @version 3.0 07/11/20
 */
public class PrivateParking extends Parking {

    public static final Creator<PrivateParking> CREATOR = new Creator<PrivateParking>() {
        @Override
        public PrivateParking createFromParcel(Parcel in) {
            return new PrivateParking(in);
        }

        @Override
        public PrivateParking[] newArray(int size) {
            return new PrivateParking[size];
        }
    };

    @SerializedName("capacity")
    private int capacity;
    @SerializedName("availableSpaces")
    private int availableSpaces;
    @SerializedName("capacityForDisabled")
    private int capacityForDisabled;
    @SerializedName("availableSpacesForDisabled")
    private int availableSpacesForDisabled;
    @SerializedName("openingHours")
    private String openingHours;
    @SerializedName("pricingList")
    private ArrayList<Integer> pricingList;

    public PrivateParking() {
    }  //  no-argument constructor to be used by GSON

    public PrivateParking(HashMap<String, Double> coordinates, int parkingID, int capacity, int availableSpaces, int capacityForDisabled, int availableSpacesForDisabled, String openingHours, ArrayList<Integer> pricingList) {
        super(coordinates, parkingID);
        this.capacity = capacity;
        this.availableSpaces = availableSpaces;
        this.capacityForDisabled = capacityForDisabled;
        this.availableSpacesForDisabled = availableSpacesForDisabled;
        this.openingHours = openingHours;
        this.pricingList = pricingList;
    }

    protected PrivateParking(Parcel in) {
        super(in);
        capacity = in.readInt();
        availableSpaces = in.readInt();
        capacityForDisabled = in.readInt();
        availableSpacesForDisabled = in.readInt();
        openingHours = in.readString();
        pricingList = in.readArrayList(null);
    }

    @Override
    public void writeToParcel(@NotNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(capacity);
        dest.writeInt(availableSpaces);
        dest.writeInt(capacityForDisabled);
        dest.writeInt(availableSpacesForDisabled);
        dest.writeString(openingHours);
        dest.writeList(pricingList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @NonNull
    @Override
    public String toString() {
        return
                "Coordinates: " + getCoordinates() +
                        ", ParkingID: " + getParkingID() +
                        ", PricingList: " + getPricingList() +
                        ", Capacity: " + getCapacity() +
                        ", AvailalbleSpaces: " + getAvailableSpaces() +
                        ", CapacityForDisabled: " + getCapacityForDisabled() +
                        ", AvailalbleSpacesForDisabled: " + getAvailableSpacesForDisabled() +
                        ", OpeningHours: " + getOpeningHours();
    }

    // Getters & Setters
    public List<Integer> getPricingList() {
        return pricingList;
    }

    public void setPricingList(ArrayList<Integer> pricingList) {
        this.pricingList = pricingList;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getAvailableSpaces() {
        return availableSpaces;
    }

    public void setAvailableSpaces(int availableSpaces) {
        this.availableSpaces = availableSpaces;
    }

    public int getCapacityForDisabled() {
        return capacityForDisabled;
    }

    public void setCapacityForDisabled(int capacityForDisabled) {
        this.capacityForDisabled = capacityForDisabled;
    }

    public int getAvailableSpacesForDisabled() {
        return availableSpacesForDisabled;
    }

    public void setAvailableSpacesForDisabled(int availableSpacesForDisabled) {
        this.availableSpacesForDisabled = availableSpacesForDisabled;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    /**
     * Could be used as an alternative DocumentID for the Firestore database
     *
     * @return A digest unique to each object
     */
    @Override
    public String generateUniqueId() {
        // Create a long and unique id
        String id = getCoordinates().values().toString() + getParkingID();
        // Hash (SHA256) it to has a fixed length of 32 characters and return its value
        return ShaUtility.digest(id);
    }
}
