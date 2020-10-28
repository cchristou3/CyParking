package io.github.cchristou3.CyParking.pojo.booking;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class PrivateParkingBooking implements Parcelable {

    public static final Creator<PrivateParkingBooking> CREATOR = new Creator<PrivateParkingBooking>() {
        @Override
        public PrivateParkingBooking createFromParcel(Parcel in) {
            return new PrivateParkingBooking(in);
        }

        @Override
        public PrivateParkingBooking[] newArray(int size) {
            return new PrivateParkingBooking[size];
        }
    };
    private final String mParkingOperatorID;
    private final String mParkingName;
    private final String mUserID;
    private final String mUsername;
    private final Date mDate;
    private final String mStartingTime;
    private final String mEndingTime;
    private final double mPrice;

    public PrivateParkingBooking(String mParkingOperatorID, String mParkingName, String mUserID, String mUsername, Date mDate, String mStartingTime, String mEndingTime, double mPrice) {
        this.mParkingOperatorID = mParkingOperatorID;
        this.mParkingName = mParkingName;
        this.mUserID = mUserID;
        this.mUsername = mUsername;
        this.mDate = mDate;
        this.mStartingTime = mStartingTime;
        this.mEndingTime = mEndingTime;
        this.mPrice = mPrice;
    }

    protected PrivateParkingBooking(Parcel in) {
        mDate = new Date(in.readLong());
        mParkingOperatorID = in.readString();
        mParkingName = in.readString();
        mUserID = in.readString();
        mUsername = in.readString();
        mEndingTime = in.readString();
        mStartingTime = in.readString();
        mPrice = in.readDouble();
    }

    public String getmParkingOperatorID() {
        return mParkingOperatorID;
    }

    public String getmParkingName() {
        return mParkingName;
    }

    public String getmUserID() {
        return mUserID;
    }

    public String getmUsername() {
        return mUsername;
    }

    public Date getmDate() {
        return mDate;
    }

    public String getmStartingTime() {
        return mStartingTime;
    }

    public String getmEndingTime() {
        return mEndingTime;
    }

    public double getmPrice() {
        return mPrice;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mDate.getTime());
        dest.writeString(mParkingOperatorID);
        dest.writeString(mParkingName);
        dest.writeString(mUserID);
        dest.writeString(mUsername);
        dest.writeString(mEndingTime);
        dest.writeString(mStartingTime);
        dest.writeDouble(mPrice);
    }
}
