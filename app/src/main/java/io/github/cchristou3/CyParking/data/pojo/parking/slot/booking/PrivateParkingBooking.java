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

    /**
     * Public Constructor.
     * Initialize all the attributes of the class with the given arguments.
     *
     * @param coordinates       The lot's to be booked coordinates
     * @param parkingID         The lot's id.
     * @param parkingOperatorID The lot's operator.
     * @param parkingName       The lot's name.
     * @param userID            The lot's operator's id.
     * @param username          The username of the user that booked a space in the lot.
     * @param dateOfBooking     The date of the booking.
     * @param startingTime      The starting time of the booking.
     * @param endingTime        The ending time of the booking.
     * @param price             The fee of the parking.
     */
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
        this.completed = getInitialBookingStatus();
    }

    /**
     * Constructor to be used by the Parcelable interface
     * to initialize the PrivateParkingBooking instance with the specified
     * {@link Parcel}.
     *
     * @param in Contains the contents of the PrivateParkingBooking instance.
     */
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

    /**
     * Compares the given object with the current object.
     * The {@link #generateUniqueId()} method is used
     * to produce the id of each object.
     *
     * @param obj The object to be compared with.
     * @return True, if both objects generate the same id. Otherwise, false.
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof PrivateParkingBooking)) return false;
        return this.generateUniqueId().equals(((PrivateParkingBooking) obj).generateUniqueId());
    }

    /**
     * Get the initial status of each booking.
     *
     * @return False, as to booking not completed yet.
     */
    public boolean getInitialBookingStatus() {
        return false;
    }

    /**
     * Non-implemented Parcelable method.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
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

    /**
     * Getters and Setters
     */
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
