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
    private int mParkingID;
    private String mParkingName;
    private Date mDate;
    private String mStartingTime;
    private String mEndingTime;
    private double mPrice;

    public PrivateParkingBooking(int mParkingID, String mParkingName, Date mDate, String mStartingTime, String mEndingTime, double mPrice) {
        this.mParkingID = mParkingID;
        this.mParkingName = mParkingName;
        this.mDate = mDate;
        this.mStartingTime = mStartingTime;
        this.mEndingTime = mEndingTime;
        this.mPrice = mPrice;
    }

    protected PrivateParkingBooking(Parcel in) {
        mDate = new Date(in.readLong());
        mParkingID = in.readInt();
        mParkingName = in.readString();
        mEndingTime = in.readString();
        mStartingTime = in.readString();
        mPrice = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mDate.getTime());
        dest.writeInt(mParkingID);
        dest.writeString(mParkingName);
        dest.writeString(mEndingTime);
        dest.writeString(mStartingTime);
        dest.writeDouble(mPrice);
    }
}
