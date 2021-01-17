package io.github.cchristou3.CyParking.data.model.parking.slot.booking;

import org.junit.Test;

import java.util.Date;
import java.util.Random;

import io.github.cchristou3.CyParking.data.model.parking.lot.SlotOffer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the {@link BookingDetails} class.
 */
public class BookingDetailsTest {

    private static final Date DATE = new Date();
    private static final String TIME = "12 : 30";
    private static final SlotOffer OFFER = SlotOffer.getRandomInstance(new Random());
    protected static boolean COMPLETED = true;

    @Test
    public void BookingDetails_initializesAttributesCorrectly() {
        // When a BookingDetails instance gets initialized
        BookingDetails details = new BookingDetails(DATE, TIME, OFFER);
        // Then the getters should return the appropriate values
        assertTrue(
                details.getDateOfBooking().equals(DATE)
                        && details.isCompleted() == (!COMPLETED)
                        && details.getSlotOffer().equals(OFFER)
                        && details.getStartingTime().equals(TIME)
        );
    }

    @Test
    public void getInitialBookingStatus_returnsFalse() {
        // When a BookingDetails instance gets initialized
        BookingDetails details = new BookingDetails(DATE, TIME, OFFER);
        // Then complete status should set to false
        assertFalse(details.getInitialBookingStatus());
    }

    @Test
    public void toString_returnsExpected() {
        // When a BookingDetails instance gets initialized
        BookingDetails details = new BookingDetails(DATE, TIME, OFFER);
        // Then
        assertEquals(details.toString(), "dateOfBooking: " + DATE
                + ", startingTime: " + TIME
                + ", slotOffer: " + OFFER);

    }
}