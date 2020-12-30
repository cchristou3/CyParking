package io.github.cchristou3.CyParking.data.pojo.parking.lot;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

/**
 * Purpose: <p>POJO to be used to transfer and receive data
 * via activities / fragments and HTTPS requests.
 * To be used, when operators add/update offers about their
 * parking lot.
 *
 * @author Charalambos Christou
 * @version 2.0 22/12/2020
 */
public class SlotOffer implements Parcelable {

    public static final Creator<SlotOffer> CREATOR = new Creator<SlotOffer>() {
        @Override
        public SlotOffer createFromParcel(Parcel in) {
            return new SlotOffer(in);
        }

        @Override
        public SlotOffer[] newArray(int size) {
            return new SlotOffer[size];
        }
    };

    // Data members
    @SerializedName("duration")
    private float durationInHours;
    private float price;

    /**
     * A no-argument constructor to be used by Firebase.
     */
    public SlotOffer() {
    }

    /**
     * Initialize the object's duration and
     * price attributes with the given arguments.
     *
     * @param duration The amount of hours.
     * @param price    The price.
     */
    public SlotOffer(float duration, float price) {
        this.durationInHours = duration;
        this.price = price;
    }

    /**
     * Constructor used by the Parcelable interface.
     *
     * @param in The parcel to store the class' data members' values.
     */
    protected SlotOffer(@NotNull Parcel in) {
        durationInHours = in.readFloat();
        price = in.readFloat();
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
        dest.writeFloat(durationInHours);
        dest.writeFloat(price);
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Get the duration of this slot offer
     *
     * @return The {@link SlotOffer#durationInHours}
     */
    public float getDuration() {
        return durationInHours;
    }

    /**
     * Assign the {@link SlotOffer#durationInHours} with the specified
     * argument.
     *
     * @param duration The number of hours
     */
    public void setDuration(float duration) {
        this.durationInHours = duration;
    }

    /**
     * Get the price of this slot offer
     *
     * @return The {@link SlotOffer#price}
     */
    public float getPrice() {
        return price;
    }

    /**
     * Assign the {@link SlotOffer#price} with the specified
     * argument.
     *
     * @param price The price of this slot offer.
     */
    public void setPrice(float price) {
        this.price = price;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    @NonNull
    @Override
    public String toString() {
        return "€" + this.price + " for " + this.durationInHours + " hours";
    }
}
