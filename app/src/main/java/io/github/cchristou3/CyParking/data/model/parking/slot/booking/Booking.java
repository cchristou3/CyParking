package io.github.cchristou3.CyParking.data.model.parking.slot.booking;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;

import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.data.model.parking.ParkingId;
import io.github.cchristou3.CyParking.utilities.ShaUtility;

/**
 * Purpose: <p>POJO to be used to transfer and receive data
 * via activities / fragments and HTTP requests. Holds needed
 * info to execute a "booking transaction" in ParkingBookingFragment.
 * This is a Subclass of ParkingId - A booking
 * contains the parkingId of the lot it is booked for.
 * It also inherits its {@link ParkingId#generateUniqueId()} method
 * that is used to generate the {@link DocumentReference#getId()}
 * for the booking in the database.</p>
 *
 * @author Charalambos Christou
 * @version 5.0 23/01/21
 */
public class Booking extends ParkingId implements Parcelable, Comparable<Booking> {

    public static final Creator<Booking> CREATOR = new Creator<Booking>() {
        @Override
        public Booking createFromParcel(Parcel in) {
            return new Booking(in);
        }

        @Override
        public Booking[] newArray(int size) {
            return new Booking[size];
        }
    };
    public static final String LOT_NAME = "lotName";
    // Operator attributes
    private String operatorId;
    private String lotName;
    // User that makes the booking attributes
    private String bookingUserId;
    // Booking details
    private BookingDetails bookingDetails;

    public Booking() { /* no-argument constructor to be used for deserialization */ }

    /**
     * Public Constructor.
     * Initialize all the attributes of the class with the given arguments.
     *
     * @param parkingId     The lot's id.
     * @param operatorId    The operator's Id.
     * @param lotName       The lot's name.
     * @param bookingUserId The id the user that issued the booking.
     */
    public Booking(int parkingId, String operatorId, String lotName, String bookingUserId, BookingDetails bookingDetails) {
        this.parkingId = parkingId;
        this.operatorId = operatorId;
        this.lotName = lotName;
        this.bookingUserId = bookingUserId;
        this.bookingDetails = bookingDetails;
    }

    /**
     * Constructor to be used by the Parcelable interface
     * to initialize the Booking instance with the specified
     * {@link Parcel}.
     *
     * @param in Contains the contents of the Booking instance.
     */
    protected Booking(@NotNull Parcel in) {
        parkingId = in.readInt();
        operatorId = in.readString();
        lotName = in.readString();
        bookingUserId = in.readString();
        bookingDetails = BookingDetails.CREATOR.createFromParcel(in);
    }

    /**
     * Copy constructor for <u>deep copying</u> {@link Booking} objects.
     *
     * @param booking The {@link Booking} to have its 'contents' copied.
     */
    public Booking(@NotNull Booking booking) {
        // Deep copying the object
        setBookingDetails(
                new BookingDetails(
                        booking.getBookingDetails().getDateOfBooking(),
                        booking.getBookingDetails().getStartingTime(),
                        booking.getBookingDetails().getSlotOffer(),
                        booking.isCompleted()
                )
        );
        // Trim returns a new String - new reference
        setLotName(booking.getLotName().trim());
        setBookingUserId(booking.getBookingUserId().trim());
        setOperatorId(booking.getOperatorId().trim());
        setParkingId(booking.parkingId);
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
        dest.writeInt(parkingId);
        dest.writeString(operatorId);
        dest.writeString(lotName);
        dest.writeString(bookingUserId);
        dest.writeParcelable(bookingDetails, flags);
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    @NonNull
    @Override
    public String toString() {
        return super.toString() + ", operatorId: " + operatorId
                + ", lotName: " + lotName
                + ", bookingUserId: " + bookingUserId
                + ", bookingDetails: { " + bookingDetails + " }";
    }

    /**
     * Create a new string which consists of the following attributes:
     * {@link #parkingId}, {@link #coordinates}, {@link #operatorId}, {@link #lotName}, {@link #bookingUserId},
     * {@link #bookingDetails}.
     * <p>
     * Not Included: {@link BookingDetails#completed}
     * <p>
     * Then, hash the generated string and return it.
     * <p>
     * NOTE: Two Booking objects which have the same values for the data members
     * mentioned above but different "completed" values, will indeed generate the same digest.
     * E.g. The following Objects have the same data values, except for the "completed" attribute.
     * p1   =    new Booking(coordinates, ... , completed = true)
     * p2   =    new Booking(coordinates, ... , completed = false)
     * p1.generateUniqueId() == p2.generateUniqueId() -> true
     *
     * @return the hashed version of the parking's generated id
     * @see BookingTest
     */
    @SuppressWarnings("JavadocReference")
    @Override
    public String generateUniqueId() {
        // Hash (SHA256) it to has a fixed length of 32 characters
        return ShaUtility.digest(toString());
    }

    /**
     * Access the {@link BookingDetails#isCompleted()} data member.
     *
     * @return The value of {@link BookingDetails#isCompleted()}.
     */
    @Exclude
    public boolean isCompleted() {
        return bookingDetails.isCompleted();
    }

    /**
     * Access the {@link #operatorId} of the coordinate.
     *
     * @return operatorId coordinates of the booking.
     */
    public String getOperatorId() {
        return operatorId;
    }

    /**
     * Sets the value of {@link #operatorId} with the given argument.
     *
     * @param operatorId The operatorId of the booking.
     */
    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    /**
     * Access the {@link #lotName} of the coordinate.
     *
     * @return The coordinates of the booking.
     */
    public String getLotName() {
        return lotName;
    }

    /**
     * Sets the value of {@link #lotName} with the given argument.
     *
     * @param lotName The lotName of the booking.
     */
    public void setLotName(String lotName) {
        this.lotName = lotName;
    }

    /**
     * Access the {@link #bookingUserId} of the coordinate.
     *
     * @return The bookingUserId of the booking.
     */
    public String getBookingUserId() {
        return bookingUserId;
    }

    /**
     * Sets the value of {@link #bookingUserId} with the given argument.
     *
     * @param bookingUserId The bookingUserId of the booking.
     */
    public void setBookingUserId(String bookingUserId) {
        this.bookingUserId = bookingUserId;
    }

    /**
     * Access the {@link #bookingDetails} of the coordinate.
     *
     * @return The bookingDetails of the booking.
     */
    public BookingDetails getBookingDetails() {
        return bookingDetails;
    }

    /**
     * Sets the value of {@link #bookingDetails} with the given argument.
     *
     * @param bookingDetails The bookingDetails of the booking.
     */
    public void setBookingDetails(BookingDetails bookingDetails) {
        this.bookingDetails = bookingDetails;
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * @param obj the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(Booking obj) {
        return (obj != null
                && obj.parkingId == this.parkingId
                && obj.bookingUserId.equals(this.bookingUserId)
                && obj.operatorId.equals(this.operatorId)
                && obj.lotName.equals(this.lotName)
                && obj.bookingDetails.compareTo(this.bookingDetails) == 0
        ) ? 0 : 1;
    }
}
