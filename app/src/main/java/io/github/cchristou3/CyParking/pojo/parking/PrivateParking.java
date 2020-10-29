package io.github.cchristou3.CyParking.pojo.parking;

import android.os.Parcel;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PrivateParking extends Parking {

    // TODO: Replace with -> @ParcelField("")

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
    @SerializedName("PricingList")
    private ArrayList<Integer> mPricingList;


    public PrivateParking(HashMap<String, Double> mCoordinates, int mParkingID, ArrayList<Integer> mPricingList, int mCapacity, int mAvailalbleSpaces, int mCapacityForDisabled, int mAvailalbleSpacesForDisabled, String mOpeningHours) {
        super(mCoordinates, mParkingID);
        this.mPricingList = mPricingList;
        this.mCapacity = mCapacity;
        this.mAvailableSpaces = mAvailalbleSpaces;
        this.mCapacityForDisabled = mCapacityForDisabled;
        this.mAvailableSpacesForDisabled = mAvailalbleSpacesForDisabled;
        this.mOpeningHours = mOpeningHours;
    }


    protected PrivateParking(Parcel in) {
        super(in);
        mCapacity = in.readInt();
        mAvailableSpaces = in.readInt();
        mCapacityForDisabled = in.readInt();
        mAvailableSpacesForDisabled = in.readInt();
        mOpeningHours = in.readString();
        mPricingList = in.readArrayList(null);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(mCapacity);
        dest.writeInt(mAvailableSpaces);
        dest.writeInt(mCapacityForDisabled);
        dest.writeInt(mAvailableSpacesForDisabled);
        dest.writeString(mOpeningHours);
        dest.writeList(mPricingList);
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

    public void setmPricingList(ArrayList<Integer> mPricingList) {
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


    @Override
    public int describeContents() {
        return 0;
    }

}
