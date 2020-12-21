package io.github.cchristou3.CyParking.data.pojo.parking.slot.booking;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.HashMap;

import io.github.cchristou3.CyParking.data.pojo.parking.slot.Parking;
import io.github.cchristou3.CyParking.utilities.ShaUtility;

/**
 * Purpose: <p>POJO to be used to transfer and receive data
 * via activities / fragments and HTTPS requests. Holds needed
 * info to execute a "booking transaction" in ParkingBookingFragment.
 * This is a Subclass of Parking.</p>
 *
 * @author Charalambos Christou
 * @version 3.0 07/11/20
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
    private boolean completed;

    public PrivateParkingBooking() {
        super();
    }


    public PrivateParkingBooking(HashMap<String, Double> coordinates, int parkingID, String parkingOperatorID, String parkingName, String userID, String username, Date dateOfBooking, String startingTime, String endingTime, double price, boolean completed) {
        this.coordinates = coordinates;
        this.parkingID = parkingID;
        this.parkingOperatorID = parkingOperatorID;
        this.parkingName = parkingName;
        this.userID = userID;
        this.username = username;
        this.dateOfBooking = dateOfBooking;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.price = price;
        this.completed = completed;
    }

    public PrivateParkingBooking(HashMap<String, Double> coordinates, int parkingID, String parkingOperatorID, String parkingName, String userID, String username, Date dateOfBooking, String startingTime, String endingTime, double price) {
        this.coordinates = coordinates;
        this.parkingID = parkingID;
        this.parkingOperatorID = parkingOperatorID;
        this.parkingName = parkingName;
        this.userID = userID;
        this.username = username;
        this.dateOfBooking = dateOfBooking;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.price = price;
        this.completed = getInitialBookingStatus();
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
        completed = (in.readInt() == 1);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof PrivateParkingBooking)) return false;
        return this.generateUniqueId().equals(((PrivateParkingBooking) obj).generateUniqueId());
    }

    public void updateContents(PrivateParkingBooking privateParkingBooking) {
        this.coordinates = privateParkingBooking.coordinates;
        this.parkingID = privateParkingBooking.parkingID;
        this.parkingOperatorID = privateParkingBooking.parkingOperatorID;
        this.parkingName = privateParkingBooking.parkingName;
        this.userID = privateParkingBooking.userID;
        this.username = privateParkingBooking.username;
        this.dateOfBooking = privateParkingBooking.dateOfBooking;
        this.startingTime = privateParkingBooking.startingTime;
        this.endingTime = privateParkingBooking.endingTime;
        this.price = privateParkingBooking.price;
        this.completed = privateParkingBooking.completed;
    }

    public boolean getInitialBookingStatus() {
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NotNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(dateOfBooking.getTime());
        dest.writeString(parkingOperatorID);
        dest.writeString(parkingName);
        dest.writeString(userID);
        dest.writeString(username);
        dest.writeString(endingTime);
        dest.writeString(startingTime);
        dest.writeDouble(price);
        dest.writeInt(completed ? 1 : 0);
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
        return completed;
    }

    /**
     * Create a new string which consists of the following attributes:
     * {@link #parkingID}, {@link #parkingOperatorID}, {@link #parkingName}, {@link #userID}, {@link #username},
     * {@link #dateOfBooking}, {@link #startingTime}, {@link #endingTime}, {@link #price}, {@link #coordinates}.
     * <p>
     * Not Included: {@link #completed}
     * <p>
     * Then, hash the generated string and return it.
     * <p>
     * NOTE: Two PrivateParkingBooking objects which have the same values for the data members
     * mentioned above but different "completed" values, will indeed generate the same digest.
     * E.g. The following Objects have the same data values, except for the "completed" attribute.
     * p1   =    new PrivateParkingBooking(coordinates, ... , completed = true)
     * p2   =    new PrivateParkingBooking(coordinates, ... , completed = false)
     * p1.generateUniqueId() == p2.generateUniqueId() -> true
     *
     * @return the hashed version of the parking's generated id
     */
    @Override
    public String generateUniqueId() {
        // Create a long and unique id
        String id = getParkingID() + parkingOperatorID + parkingName + userID + username + dateOfBooking + startingTime + endingTime +
                price + getCoordinates().values().toString();
        // Hash (SHA256) it to has a fixed length of 32 characters
        return ShaUtility.digest(id);
    }
}
