package io.github.cchristou3.CyParking.data.model.parking.slot.booking;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;

import io.github.cchristou3.CyParking.data.model.parking.lot.SlotOffer;

/**
 * Purpose: contain details related the a booking.
 * It has a composition association with {@link Booking},
 * as booking details are part of a booking.
 *
 * @author Charalambos Christou
 * @version 3.0 24/02/2021
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
    public static final String REG_EX = ",";
    // Booking attributes
    private boolean completed;
    private Date dateOfBooking;
    private Time startingTime;
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
    public BookingDetails(Date dateOfBooking, Time startingTime, SlotOffer slotOffer) {
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
    public BookingDetails(Date dateOfBooking, Time startingTime, SlotOffer slotOffer, boolean completed) {
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
        startingTime = in.readParcelable(Time.class.getClassLoader());
        slotOffer = in.readParcelable(SlotOffer.class.getClassLoader());
        completed = in.readByte() != 0;
    }

    /**
     * Converts the given string into a {@link BookingDetails} instance.
     * The string contains the attributes of the object separated by ','.
     * The attributes must be given in this exact order:
     * date(in Long,see {@link Date#getTime()}),hour,minutes,duration,price.
     * E.g.
     * 6543212345,2,34,2.0,1.0.
     *
     * @param sequence A string having the exact same syntax and order as {@link #toString()}.
     * @return a {@link BookingDetails} instance.
     * @see #toString()
     */
    @NotNull
    public static BookingDetails toBookingDetails(@NotNull String sequence) {
        String[] attributes = sequence.split(REG_EX, 5);
        return new BookingDetails(
                new Date(Long.parseLong(attributes[0])), // dateOfBooking
                new Time(
                        Integer.parseInt(attributes[1]), // hour
                        Integer.parseInt(attributes[2]) // minute
                ),
                new SlotOffer(
                        Float.parseFloat(attributes[3]), // duration
                        Float.parseFloat(attributes[4]) // price
                )
        );
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
        dest.writeParcelable(startingTime, flags);
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
    public Time getStartingTime() {
        return startingTime;
    }

    /**
     * Sets the value of {@link #startingTime} with the given argument.
     *
     * @param startingTime The startingTime of the booking.
     */
    public void setStartingTime(Time startingTime) {
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
     * The string contains the attributes of the object separated by ','.
     *
     * @return a string representation of the object.
     * @see Booking#generateUniqueId()
     * @see #toBookingDetails(String)
     */
    @NonNull
    @Override
    public String toString() {
        return dateOfBooking.getTime() + REG_EX // passing the date as long
                + startingTime.hour + REG_EX
                + startingTime.minute + REG_EX
                + slotOffer.getDuration() + REG_EX
                + slotOffer.getPrice();
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
                && obj.startingTime.compareTo(this.startingTime) == 0 // Same times
                && obj.dateOfBooking.compareTo(this.dateOfBooking) == 0 // Same dates
                && obj.slotOffer.compareTo(this.slotOffer) == 0 // Same offers
        ) ? 0 : 1;
    }

    public static class Time implements Parcelable, Comparable<Time> {

        public static final Creator<Time> CREATOR = new Creator<Time>() {
            @Override
            public Time createFromParcel(Parcel in) {
                return new Time(in);
            }

            @Override
            public Time[] newArray(int size) {
                return new Time[size];
            }
        };
        int hour;
        int minute;

        /**
         * No-argument constructor to be used for deserialization.
         */
        public Time() {
        }

        public Time(int hour, int minute) {
            this.hour = hour;
            this.minute = minute;
        }

        protected Time(@NotNull Parcel in) {
            hour = in.readInt();
            minute = in.readInt();
        }

        /**
         * Computes the time of the day.
         *
         * @return A String which corresponds to the time (E.g. "12 : 30").
         */
        @NotNull
        public static Time getCurrentTime() {
            // Access the current time of the day
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            int minute = Calendar.getInstance().get(Calendar.MINUTE);
            return new Time((hour), minute);
        }

        /**
         * Checks whether the hour is in the range of 0..23 (inclusive)
         * and that the minute is in the range of 0..59 (inclusive).
         *
         * @param hours  The hours of the day represented by an int.
         * @param minute The minute of the hour represented by an int.
         * @throws IllegalArgumentException When at least one of the fields are invalid.
         */
        static void checkIfFieldsValid(int hours, int minute) throws IllegalArgumentException {
            if (!(hours >= 0 && hours <= 23) // Hours check
                    || !(minute >= 0 && minute <= 59))
                throw new IllegalArgumentException("The hours must be in range of 0..23 (inclusive)"
                        + " and the minutes in range of 0..59 (inclusive).");
        }

        /**
         * Creates the time based on specified hours and minutes (E.g. "12 : 45")
         *
         * @param finalHours the hour of the day
         * @param minute     the minute(s) of the hour
         * @return A string of the format "__ : __" where _ is a digit.
         * @throws IllegalArgumentException if the parameters are invalid.
         * @see #checkIfFieldsValid(int, int)
         */
        @NotNull
        @Contract(pure = true)
        public static String getTimeOf(int finalHours, int minute) {
            checkIfFieldsValid(finalHours, minute);
            return ((finalHours < 10) ? "0" : "") + finalHours
                    + " : "
                    + ((minute < 10) ? "0" : "") + minute;
        }

        /*
        Getters & Setters
         */
        public int getHour() {
            return hour;
        }

        public void setHour(int hour) {
            this.hour = hour;
        }

        public int getMinute() {
            return minute;
        }

        public void setMinute(int minute) {
            this.minute = minute;
        }

        /**
         * Returns a string representation of the object.
         *
         * @return a string representation of the object.
         * @see Booking#generateUniqueId()
         */
        @NonNull
        @Override
        public String toString() {
            return getTimeOf(this.hour, this.minute);
        }

        /**
         * Non-implemented {@link Parcelable} method.
         */
        @Override
        public int describeContents() {
            return 0;
        }

        /**
         * @see BookingDetails#writeToParcel(Parcel, int)
         */
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(hour);
            dest.writeInt(minute);
        }

        /**
         * Compares the contents of this object and the given
         * time object. If the same, 0 is returned. Otherwise, 1.
         *
         * @param o The time object to be compared with.
         * @return 0 If the same. Otherwise, 1.
         */
        @Override
        public int compareTo(Time o) {
            return (o != null // not null
                    && this.hour == o.hour // same hours
                    && this.minute == o.minute) // same minutes
                    ? 0 : 1;
        }
    }
}