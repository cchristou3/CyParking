package io.github.cchristou3.CyParking.pojo.parking;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;

public class PrivateParking extends Parking {

    @SerializedName("PricingList")
    private List<Integer> mPricingList;
    @SerializedName("Capacity")
    private int mCapacity;
    @SerializedName("AvailableSpaces")
    private int mAvailableSpaces;
    @SerializedName("CapacityForDisabled")
    private int mCapacityForDisabled;
    @SerializedName("AvailableSpacesForDisabled")
    private int mAvailableSpacesForDisabled;
    @SerializedName("OpeningHours")
    private String mOpeningHours;

    public PrivateParking(HashMap<String, Double> mCoordinates, int mParkingID, List<Integer> mPricingList, int mCapacity, int mAvailalbleSpaces, int mCapacityForDisabled, int mAvailalbleSpacesForDisabled, String mOpeningHours) {
        super(mCoordinates, mParkingID);
        this.mPricingList = mPricingList;
        this.mCapacity = mCapacity;
        this.mAvailableSpaces = mAvailalbleSpaces;
        this.mCapacityForDisabled = mCapacityForDisabled;
        this.mAvailableSpacesForDisabled = mAvailalbleSpacesForDisabled;
        this.mOpeningHours = mOpeningHours;
    }

    @NonNull
    @Override
    public String toString() {
        return
                "mCoordinates: " + getmCoordinates() +
                        ", mParkingID: " + getmParkingID() +
                        ", mPricingList: " + getmPricingList() +
                        ", mCapacity: " + getmCapacity() +
                        ", mAvailalbleSpaces: " + getmAvailableSpaces() +
                        ", mCapacityForDisabled: " + getmCapacityForDisabled() +
                        ", mAvailalbleSpacesForDisabled: " + getmAvailableSpacesForDisabled() +
                        ", mOpeningHours: " + getmOpeningHours();
    }

    public List<Integer> getmPricingList() {
        return mPricingList;
    }

    public void setmPricingList(List<Integer> mPricingList) {
        this.mPricingList = mPricingList;
    }

    public int getmCapacity() {
        return mCapacity;
    }

    public void setmCapacity(int mCapacity) {
        this.mCapacity = mCapacity;
    }

    public int getmAvailableSpaces() {
        return mAvailableSpaces;
    }

    public void setmAvailableSpaces(int mAvailableSpaces) {
        this.mAvailableSpaces = mAvailableSpaces;
    }

    public int getmCapacityForDisabled() {
        return mCapacityForDisabled;
    }

    public void setmCapacityForDisabled(int mCapacityForDisabled) {
        this.mCapacityForDisabled = mCapacityForDisabled;
    }

    public int getmAvailableSpacesForDisabled() {
        return mAvailableSpacesForDisabled;
    }

    public void setmAvailableSpacesForDisabled(int mAvailableSpacesForDisabled) {
        this.mAvailableSpacesForDisabled = mAvailableSpacesForDisabled;
    }

    public String getmOpeningHours() {
        return mOpeningHours;
    }

    public void setmOpeningHours(String mOpeningHours) {
        this.mOpeningHours = mOpeningHours;
    }
}
