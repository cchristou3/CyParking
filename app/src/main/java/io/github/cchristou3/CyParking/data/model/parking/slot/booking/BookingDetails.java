package io.github.cchristou3.CyParking.data.model.parking.slot.booking;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

import io.github.cchristou3.CyParking.data.model.parking.lot.SlotOffer;

/**
 * Purpose: contain details related the a booking.
 * It has a composition association with {@link Booking},
 * as booking details are part of a booking.
 *
 * @author Charalambos Christou
 * @version 31/12/2020
 */
public class BookingDetails implements Parcelable, Comparable<BookingDetails> {

    public static final Creator<BookingDetails> CREATOR = new Creator<BookingDetails>() {
        @Override
        public BookingDetails createFromParcel(Parcel in) {
            return new BookingDetails(in);
        }

        @Override
        public BookingDetails[] newArray(int size) {
            return new BookingDetails[size];
        }
    };

    public static final String DATE = "dateOfBooking";
    public static final String STARTING_TIME = "startingTime";
    public static final String SLOT_OFFER = "slotOffer";

    private boolean completed;
    // Booking attributes
    private Date dateOfBooking;
    private String startingTime;
    private SlotOffer slotOffer;

    public BookingDetails() { /* no-argument constructor to be used for deserialization */}

    /**
     * Public Constructor.
     * Initialize all the attributes of the class with the given arguments.
     *
     * @param dateOfBooking The date the booking will take place.
     * @param startingTime  The starting time that the booking will take place.
     * @param slotOffer     The selected offer for the this booking.
     */
    public BookingDetails(Date dateOfBooking, String startingTime, SlotOffer slotOffer) {
        this.dateOfBooking = dateOfBooking;
        this.startingTime = startingTime;
        this.slotOffer = slotOffer;
        this.completed = getInitialBookingStatus();
    }

    /**
     * Public Constructor.
     * Initialize all the attributes of the class with the given arguments.
     * Used for deep copy.
     *
     * @param dateOfBooking The date the booking will take place.
     * @param startingTime  The starting time that the booking will take place.
     * @param slotOffer     The selected offer for the this booking.
     * @param completed     The status of the booking.
     */
    public BookingDetails(Date dateOfBooking, String startingTime, SlotOffer slotOffer, boolean completed) {
        this.dateOfBooking = dateOfBooking;
        this.startingTime = startingTime;
        this.slotOffer = slotOffer;
        this.completed = completed;
    }

    /**
     * Constructor to be used by the Parcelable interface
     * to initialize the BookingDetails instance with the specified
     * {@link Parcel}.
     *
     * @param in Contains the contents of the BookingDetails instance.
     */
    protected BookingDetails(@NotNull Parcel in) {
        dateOfBooking = new Date(in.readLong());
        startingTime = in.readString();
        slotOffer = in.readParcelable(SlotOffer.class.getClassLoader());
        completed = in.readByte() != 0;
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
        dest.writeLong(dateOfBooking.getTime());
        dest.writeString(startingTime);
        dest.writeParcelable(slotOffer, flags);
        dest.writeByte((byte) (completed ? 1 : 0));
    }

    /**
     * Non-implemented Parcelable method.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Access the {@link #dateOfBooking} data member.
     *
     * @return The value of {@link #dateOfBooking}.
     */
    public Date getDateOfBooking() {
        return dateOfBooking;
    }

    /**
     * Sets the value of {@link #dateOfBooking} with the given argument.
     *
     * @param dateOfBooking The dateOfBooking of the booking.
     */
    public void setDateOfBooking(Date dateOfBooking) {
        this.dateOfBooking = dateOfBooking;
    }

    /**
     * Access the {@link #startingTime} data member.
     *
     * @return The value of {@link #startingTime}.
     */
    public String getStartingTime() {
        return startingTime;
    }

    /**
     * Sets the value of {@link #startingTime} with the given argument.
     *
     * @param startingTime The startingTime of the booking.
     */
    public void setStartingTime(String startingTime) {
        this.startingTime = startingTime;
    }

    /**
     * Access the {@link #slotOffer} data member.
     *
     * @return The value of {@link #slotOffer}.
     */
    public SlotOffer getSlotOffer() {
        return slotOffer;
    }

    /**
     * Sets the value of {@link #slotOffer} with the given argument.
     *
     * @param slotOffer The slotOffer of the booking.
     */
    public void setSlotOffer(SlotOffer slotOffer) {
        this.slotOffer = slotOffer;
    }

    /**
     * Access the {@link #completed} data member.
     *
     * @return The value of {@link #completed}.
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Sets the value of {@link #completed} with the given argument.
     *
     * @param completed The completed of the booking.
     */
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    /**
     * Get the initial status of each booking.
     *
     * @return False, as to booking not completed yet.
     */
    @Exclude
    public boolean getInitialBookingStatus() {
        return false;
    }

    /**
     * Returns a string representation of the object.
     * Note: {@link #completed} is not included on purpose.
     *
     * @return a string representation of the object.
     * @see Booking#generateUniqueId()
     */
    @NonNull
    @Override
    public String toString() {
        return "dateOfBooking: " + dateOfBooking
                + ", startingTime: " + startingTime
                + ", slotOffer: " + slotOffer;
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
    public int compareTo(BookingDetails obj) {
        return ((obj != null)
                && obj.completed == this.completed
                && obj.dateOfBooking.compareTo(this.dateOfBooking) == 0 // Same dates
                && obj.slotOffer.compareTo(this.slotOffer) == 0 // Same offers
        ) ? 0 : 1;
    }
}
