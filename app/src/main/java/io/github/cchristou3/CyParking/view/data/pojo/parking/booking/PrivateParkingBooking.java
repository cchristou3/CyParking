package io.github.cchristou3.CyParking.view.data.pojo.parking.booking;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;

import io.github.cchristou3.CyParking.view.data.pojo.parking.Parking;
import io.github.cchristou3.CyParking.view.utility.ShaUtility;

/**
 * Purpose: <p>POJO to be used to transfer and receive data
 * via activities / fragments and HTTPS requests. Holds needed
 * info to execute a "booking transaction" in ParkingBookingFragment.
 * This is a Subclass of Parking.</p>
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

    private String parkingOperatorID;
    private String parkingName;
    private String userID;
    private String username;
    private Date dateOfBooking;
    private String startingTime;
    private String endingTime;
    private double price;
    private boolean isCompleted;

    public PrivateParkingBooking() {
        super();
    }

    public PrivateParkingBooking(HashMap<String, Double> coordinates, int parkingID, String parkingOperatorID, String parkingName, String userID, String username, Date dateOfBooking, String startingTime, String endingTime, double price, boolean isCompleted) {
        super(coordinates, parkingID);
        this.parkingOperatorID = parkingOperatorID;
        this.parkingName = parkingName;
        this.userID = userID;
        this.username = username;
        this.dateOfBooking = dateOfBooking;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.price = price;
        this.isCompleted = isCompleted;
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
        isCompleted = (in.readInt() == 1);
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
        dest.writeInt(isCompleted ? 1 : 0);
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

    public boolean isCompleted() {
        return isCompleted;
    }

    public String generateUniqueId() {
        // Create a long and unique id
        String id = parkingOperatorID + parkingName + userID + username + dateOfBooking + startingTime + endingTime +
                price + getCoordinates().values().toString().trim();
        // Hash (SHA256) it to has a fixed length of 32 characters
        String hashedId = id;
        try {
            hashedId = ShaUtility.digest(id.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
        }
        return hashedId;
    }
}
