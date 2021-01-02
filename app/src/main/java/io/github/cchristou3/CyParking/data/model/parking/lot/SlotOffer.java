package io.github.cchristou3.CyParking.data.model.parking.lot;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * Purpose: <p>POJO to be used to transfer and receive data
 * via activities / fragments and HTTP requests.
 * To be used, when operators add/update offers about their
 * parking lot.
 *
 * @author Charalambos Christou
 * @version 3.0 02/01/21
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

    public SlotOffer() { /* no-argument constructor to be used for deserialization */ }

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
     * Creates a new instance of {@link SlotOffer} with random data.
     *
     * @return An instance of {@link SlotOffer}.
     */
    @NotNull
    public static SlotOffer getRandomInstance(final Random generator) {
        return new SlotOffer(generator.nextInt(10 + 1) + 1,
                generator.nextInt(10 + 1) + 1
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

    /**
     * Calculates the ratio of the offer.
     * The smaller it is, the better the offer is
     * in terms of price and duration.
     * E.g.
     * priceA = 0.5, durationInHours = 1 -> ratio = 0.5
     * priceB = 0.5, durationInHours = 2 -> ratio = 0.25
     * PriceB is more efficient than priceA.
     *
     * @return The efficiency ration of the offer.
     */
    public float getRatio() {
        return price / durationInHours;
    }

    /**
     * Compares two {@link SlotOffer} object's efficiency ratio.
     * Returns true if the current object's ration is smaller than
     * the given ones.
     *
     * @param offer The offer to be compared with.
     * @return True, if current object's ratio is smaller than the specified ones.
     * Otherwise, false.
     */
    public boolean smallerOf(SlotOffer offer) {
        if (offer == null) return true;
        return this.getRatio() < offer.getRatio();
    }
}
