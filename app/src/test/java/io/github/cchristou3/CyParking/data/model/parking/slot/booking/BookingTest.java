package io.github.cchristou3.CyParking.data.model.parking.slot.booking;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

import io.github.cchristou3.CyParking.data.model.parking.lot.SlotOffer;
import io.github.cchristou3.CyParking.data.model.parking.slot.Parking;

/**
 * Unit tests for the {@link Booking} class.
 */
public class BookingTest {

    private static final Parking.Coordinates COORDINATES = new Parking.Coordinates(1, 1);
    private static final int PARKING_ID = 73;
    private static final String OPERATOR_ID = "255";
    private static final String LOT_NAME = "Name234";
    private static final String ISSUER_ID = "81";
    private final BookingDetails bookingDetailsA =
            new BookingDetails(new Date(),
                    "14 : 30",
                    new SlotOffer(1, 1));
    private final BookingDetails bookingDetailsB =
            new BookingDetails(new Date(),
                    "14 : 30",
                    new SlotOffer(1, 1));

    @Test
    public void generateUniqueId_same_objects_different_completed_status() {
        // Given
        Booking a = new Booking(COORDINATES, PARKING_ID, OPERATOR_ID,
                LOT_NAME, ISSUER_ID, bookingDetailsA); // Completed set false by default.

        bookingDetailsB.completed = true;
        Booking b = new Booking(COORDINATES,
                PARKING_ID, OPERATOR_ID, LOT_NAME, ISSUER_ID, bookingDetailsB);
        // When
        boolean areSame = a.equals(b);
        // Then
        Assert.assertTrue(areSame);
    }

    @Test
    public void generateUniqueId_different_objects() {
        // Given
        Booking a = new Booking(COORDINATES, PARKING_ID, OPERATOR_ID,
                LOT_NAME, ISSUER_ID, bookingDetailsA); // Completed set false by default.

        Booking b = new Booking(new Parking.Coordinates(9, 9),
                999, "OPERATOR_ID", "LOT_NAME", "ISSUER_ID", bookingDetailsB);
        // When
        boolean areSame = a.equals(b);
        // Then
        Assert.assertFalse(areSame);
    }
}