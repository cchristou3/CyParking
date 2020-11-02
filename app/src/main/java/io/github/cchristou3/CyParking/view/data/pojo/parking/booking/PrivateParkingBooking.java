package io.github.cchristou3.CyParking.view.data.pojo.parking.booking;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.HashMap;

import io.github.cchristou3.CyParking.view.data.pojo.parking.Parking;

/**
 * purpose: POJO to be used to transfer and receive data
 * via activities / fragments and HTTPS requests. Holds needed
 * info to execute a "booking transaction" in ParkingBookingFragment.
 * This is a Subclass of Parking.
 *
 * @author Charalambos Christou
 * @version 2.0 29/10/20
 */
public class PrivateParkingBooking extends Parking implements Parcelable {

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

    private final String parkingOperatorID;
    private final String parkingName;
    private final String userID;
    private final String username;
    private final Date dateOfBooking;
    private final String startingTime;
    private final String endingTime;
    private final double price;

    public PrivateParkingBooking(HashMap<String, Double> coordinates, int parkingID, String parkingOperatorID, String parkingName, String userID, String username, Date dateOfBooking, String startingTime, String endingTime, double price) {
        super(coordinates, parkingID);
        this.parkingOperatorID = parkingOperatorID;
        this.parkingName = parkingName;
        this.userID = userID;
        this.username = username;
        this.dateOfBooking = dateOfBooking;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.price = price;
    }

    protected PrivateParkingBooking(Parcel in) {
        super(in);
        dateOfBooking = new Date(in.readLong());
        parkingOperatorID = in.readString();
        parkingName = in.readString();
        userID = in.readString();
        username = in.readString();
        endingTime = in.readString();
        startingTime = in.readString();
        price = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(dateOfBooking.getTime());
        dest.writeString(parkingOperatorID);
        dest.writeString(parkingName);
        dest.writeString(userID);
        dest.writeString(username);
        dest.writeString(endingTime);
        dest.writeString(startingTime);
        dest.writeDouble(price);
    }

    public String getParkingOperatorID() {
        return parkingOperatorID;
    }

    public String getParkingName() {
        return parkingName;
    }

    public String getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }

    public Date getDateOfBooking() {
        return dateOfBooking;
    }

    public String getStartingTime() {
        return startingTime;
    }

    public String getEndingTime() {
        return endingTime;
    }

    public double getPrice() {
        return price;
    }
}
