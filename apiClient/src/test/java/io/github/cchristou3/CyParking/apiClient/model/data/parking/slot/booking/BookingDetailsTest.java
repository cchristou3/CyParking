package io.github.cchristou3.CyParking.apiClient.model.data.parking.slot.booking;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.Random;

import io.github.cchristou3.CyParking.apiClient.model.data.parking.lot.SlotOffer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the {@link BookingDetails} class.
 */
public class BookingDetailsTest {

    private static final Date DATE = new Date();
    private static final BookingDetails.Time TIME = new BookingDetails.Time(12, 30);
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
        assertEquals(details.toString(), DATE.getTime() + "," + TIME.getHour() + "," + TIME.getMinute() + "," + OFFER.getDuration() + "," + OFFER.getPrice());

    }


    ///////////////////////////////////////////////////////////////////////////
    // checkIfFieldsValid(int, int) - START
    ///////////////////////////////////////////////////////////////////////////

    @Test(expected = IllegalArgumentException.class)
    public void checkIfFieldsValid_greaterMinute_throwsException() {
        BookingDetails.Time.checkIfFieldsValid(1, 60);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkIfFieldsValid_smallerMinute_throwsException() {
        BookingDetails.Time.checkIfFieldsValid(1, -1);
    }

    ///////////////////////////////////////////////////////////////////////////
    // checkIfFieldsValid(int, int) - END
    ///////////////////////////////////////////////////////////////////////////


    @Test
    public void getEndTime_13TimeHours_2duration_returns15Hours() {
        SlotOffer offer = new SlotOffer(2, 1);
        BookingDetails.Time time = new BookingDetails.Time(13, 0);
        assertEquals(BookingDetails.Time.getEndTime(new BookingDetails(new Date(), time, offer))
                .getHour(), (int) (offer.getDuration() + time.getHour()));
    }


    @Test
    public void getEndTime_0000Time_1duration_returns0100() {
        SlotOffer offer = new SlotOffer(1, 1);
        BookingDetails.Time time = new BookingDetails.Time(0, 0);
        assertEquals(BookingDetails.Time.getEndTime(new BookingDetails(new Date(), time, offer))
                .getHour(), (int) (offer.getDuration() + time.getHour()));
    }


    ///////////////////////////////////////////////////////////////////////////
    // getTimeOf - START
    ///////////////////////////////////////////////////////////////////////////

    @Test
    public void getTimeOf_singleDigitInput_returnsExpectedString() {
        // Given
        int hours = 1, minutes = 1;
        // When
        String output = BookingDetails.Time.getTimeOf(hours, minutes);
        // Then
        Assert.assertEquals("01 : 01", output);
    }

    @Test
    public void getTimeOf_doubleDigitInput_returnsExpectedString() {
        // Given
        int hours = 10, minutes = 12;
        // When
        String output = BookingDetails.Time.getTimeOf(hours, minutes);
        // Then
        Assert.assertEquals("10 : 12", output);
    }

    @Test(expected = IllegalArgumentException.class)// Then
    public void getTimeOf_negativeHours_throwsException() {
        // Given
        int hours = -1, minutes = 12;
        // When
        String output = BookingDetails.Time.getTimeOf(hours, minutes);
    }

    @Test(expected = IllegalArgumentException.class)// Then
    public void getTimeOf_greaterMinutes_throwsException() {
        // Given
        int hours = 20, minutes = 60;
        // When
        String output = BookingDetails.Time.getTimeOf(hours, minutes);
    }

    @Test(expected = IllegalArgumentException.class)// Then
    public void getTimeOf_negativeMinutes_throwsException() {
        // Given
        int hours = 1, minutes = -1;
        // When
        String output = BookingDetails.Time.getTimeOf(hours, minutes);
    }

    ///////////////////////////////////////////////////////////////////////////
    // getTimeOf - END
    ///////////////////////////////////////////////////////////////////////////
}