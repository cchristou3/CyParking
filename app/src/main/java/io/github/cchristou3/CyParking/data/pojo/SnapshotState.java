package io.github.cchristou3.CyParking.data.pojo;

import io.github.cchristou3.CyParking.ui.parking.lots.map.ParkingMapFragment;
import io.github.cchristou3.CyParking.ui.parking.slots.viewBooking.ViewBookingsFragment;

/**
 * Purpose: Represent the state of a given fragment in terms of its
 * data.
 * <p>
 * Used by {@link ParkingMapFragment} and {@link ViewBookingsFragment}.
 * {@link #INITIAL_DATA_RETRIEVAL}: refers to the state when the fragment initially
 * loads its data via an API (aka 1st wave).
 * {@link #LISTENING_TO_DATA_CHANGES}: refers to the state when the fragment has already
 * loaded all necessary data for its view, and is observing the database's nodes for changes
 * (aka any subsequent waves).
 *
 * @author Charalambos Christou
 * @version 02/01/21
 */
public class SnapshotState {

    // Constant variables
    public static final byte INITIAL_DATA_RETRIEVAL = 0;
    public static final byte LISTENING_TO_DATA_CHANGES = 1;

    private byte mState;

    /**
     * Initializes the {@link #mState}
     * with the given argument.
     *
     * @param state The initial state of the {@link SnapshotState} instance.
     */
    public SnapshotState(byte state) {
        setState(state);
    }

    /**
     * Access the current state of the object.
     *
     * @return The state of the object.
     */
    public byte getState() {
        return mState;
    }

    /**
     * Initializes the {@link #mState}
     * with the given argument.
     *
     * @param state The new state of the {@link SnapshotState} instance.
     */
    public void setState(byte state) {
        this.mState = state;
    }
}
