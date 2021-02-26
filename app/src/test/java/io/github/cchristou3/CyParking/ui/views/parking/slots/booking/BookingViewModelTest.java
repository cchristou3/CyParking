package io.github.cchristou3.CyParking.ui.views.parking.slots.booking;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import io.github.cchristou3.CyParking.data.model.parking.lot.SlotOffer;
import io.github.cchristou3.CyParking.data.model.parking.slot.booking.BookingDetails;
import io.github.cchristou3.CyParking.data.repository.BookingRepository;
import io.github.cchristou3.CyParking.ui.InstantTaskRuler;
import io.github.cchristou3.CyParking.utilities.DateTimeUtility;

import static io.github.cchristou3.CyParking.ui.LiveDataTestUtil.getOrAwaitValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for the {@link BookingViewModel} class.
 */
@RunWith(AndroidJUnit4.class)
public class BookingViewModelTest extends InstantTaskRuler {

    private final SlotOffer slotOffer = SlotOffer.getRandomInstance(new Random());
    // Subject under test
    private BookingViewModel bookingViewModel;

    @Before
    public void setUp() throws Exception {
        bookingViewModel = new BookingViewModel(new BookingRepository());
    }

    @Test
    public void getPickedDate_initially_returnsNonNull() throws InterruptedException {
        assertThat(getOrAwaitValue(bookingViewModel.getPickedDateState()), is(not(nullValue())));
    }

    @Test
    public void getPickedStartingTime_initially_returnsNonNull() throws InterruptedException {
        assertThat(getOrAwaitValue(bookingViewModel.getPickedStartingTimeState()), is(not(nullValue())));
    }

    @Test
    public void updateSlotOffer_setsNewValue() throws InterruptedException {
        // Given the slot offer gets updated
        bookingViewModel.updateSlotOffer(slotOffer);
        // Then it should update the livedata's value
        assertThat(getOrAwaitValue(bookingViewModel.getPickedSlotOfferState()), is(not(nullValue())));
        assertThat(getOrAwaitValue(bookingViewModel.getPickedSlotOfferState()), is(slotOffer));
        assertThat(bookingViewModel.getPickedSlotOffer(), is(slotOffer));
    }

    @Test
    public void updatePickedDate_setsNewValue() throws InterruptedException {
        // Given the date gets updated
        bookingViewModel.updatePickedDate(1, 1, 1);
        String output = DateTimeUtility.dateToString(1, 1, 1);
        // Then it should update the livedata's value
        assertThat(getOrAwaitValue(bookingViewModel.getPickedDateState()), is(not(nullValue())));
        assertThat(getOrAwaitValue(bookingViewModel.getPickedDateState()), is(output));
        assertThat(bookingViewModel.getPickedDate(), is(output));
    }

    @Test
    public void updateStartingTime_setsNewValue() throws InterruptedException {
        // Given the date gets updated
        int hours = 12, minutes = 0;
        bookingViewModel.updateStartingTime(hours, minutes);
        String output = BookingDetails.Time.getTimeOf(hours, minutes);
        // Then it should update the livedata's value
        assertThat(getOrAwaitValue(bookingViewModel.getPickedStartingTimeState()), is(not(nullValue())));
        assertThat(getOrAwaitValue(bookingViewModel.getPickedStartingTimeState()).toString(), is(output));
        assertThat(bookingViewModel.getPickedStartingTime().toString(), is(output));
    }

    @Test
    public void setQRCodeMessage_SetsNewValue() {
        // Given a new message got given
        String message = "hello";
        // When setQRCodeMessage gets invoked with the above string
        bookingViewModel.setQRCodeMessage(message);
        // Then getQRCodeMessage() should return the same message
        assertThat(bookingViewModel.getQRCodeMessage(), is(message));
    }

    @Test
    public void getQRCodeButtonState_returnsFalseInitially() throws InterruptedException {
        assertThat(getOrAwaitValue(bookingViewModel.getQRCodeButtonState()), is(false));
    }

    @Test
    public void updateQRCodeButtonState_setsTrue() throws InterruptedException {
        // Given the date gets updated
        boolean shouldShow = true;
        // When updateQRCodeButtonState gets called
        bookingViewModel.updateQRCodeButtonState(shouldShow);
        // Then it should update the livedata's value and getQRCodeButtonState should return the assigned value
        assertThat(getOrAwaitValue(bookingViewModel.getQRCodeButtonState()), is(shouldShow));
    }

    @Test
    public void updateQRCodeButtonState_setsFalse() throws InterruptedException {
        // Given the date gets updated
        boolean shouldShow = false;
        // When updateQRCodeButtonState gets called
        bookingViewModel.updateQRCodeButtonState(shouldShow);
        // Then it should update the livedata's value and getQRCodeButtonState should return the assigned value
        assertThat(getOrAwaitValue(bookingViewModel.getQRCodeButtonState()), is(shouldShow));
    }

    @Test
    public void getBookingButtonState_returnsFalseInitially() throws InterruptedException {
        assertThat(getOrAwaitValue(bookingViewModel.getBookingButtonState()), is(false));
    }

    @Test
    public void updateBookingButtonState_setsTrue() throws InterruptedException {
        // Given the date gets updated
        boolean shouldShow = true;
        // When updateBookingButtonState gets called
        bookingViewModel.updateBookingButtonState(shouldShow);
        // Then it should update the livedata's value and getBookingButtonState should return the assigned value
        assertThat(getOrAwaitValue(bookingViewModel.getBookingButtonState()), is(shouldShow));
    }

    @Test
    public void updateBookingButtonState_setsFalse() throws InterruptedException {
        // Given the date gets updated
        boolean shouldShow = false;
        // When updateBookingButtonState gets called
        bookingViewModel.updateBookingButtonState(shouldShow);
        // Then it should update the livedata's value and getBookingButtonState should return the assigned value
        assertThat(getOrAwaitValue(bookingViewModel.getBookingButtonState()), is(shouldShow));
    }
}