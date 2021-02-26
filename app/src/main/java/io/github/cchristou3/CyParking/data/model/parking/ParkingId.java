package io.github.cchristou3.CyParking.data.model.parking;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot;
import io.github.cchristou3.CyParking.data.model.parking.slot.booking.Booking;

/**
 * Purpose: abstract common attributes ({@link #parkingId})
 * and methods ({@link #generateUniqueId()}) from Parking
 * related classes.
 * {@link Booking}s have the parkingId of the lot they were issued for.
 * {@link ParkingLot}'s have a parkingId that uniquely identifies them.
 *
 * @author Charalambos Christou
 * @version 1.0 20/01/21
 */
public abstract class ParkingId {

    @SerializedName("parkingId")
    protected int parkingId;

    public ParkingId() { /* no-argument constructor to be used by GSON */ }

    /**
     * Initializes {@link #parkingId} with the given
     * argument.
     *
     * @param parkingId The id of the parking object.
     */
    public ParkingId(int parkingId) {
        this.parkingId = parkingId;
    }

    /**
     * Access the {@link #parkingId} of the coordinate.
     *
     * @return The {@link #parkingId} of the lot.
     */
    public int getParkingId() {
        return parkingId;
    }

    /**
     * Sets the value of {@link #parkingId} with the given argument.
     *
     * @param parkingId The new parkingID of the parking object.
     */
    public void setParkingId(int parkingId) {
        this.parkingId = parkingId;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    @NonNull
    @Override
    public String toString() {
        return Integer.toString(parkingId);
    }

    /**
     * To be overridden by its subclasses
     * It should combine the class' attribute values to create a unique id for the object.
     */
    public abstract String generateUniqueId();
}
