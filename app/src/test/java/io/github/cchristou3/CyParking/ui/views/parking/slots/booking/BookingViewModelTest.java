package io.github.cchristou3.CyParking.ui.views.parking.slots.booking;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import io.github.cchristou3.CyParking.data.model.parking.lot.SlotOffer;
import io.github.cchristou3.CyParking.data.repository.BookingRepository;
import io.github.cchristou3.CyParking.ui.InstantTaskRuler;
import io.github.cchristou3.CyParking.utilities.Utility;

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
        assertThat(getOrAwaitValue(bookingViewModel.getPickedDate()), is(not(nullValue())));
    }

    @Test
    public void getPickedStartingTime_initially_returnsNonNull() throws InterruptedException {
        assertThat(getOrAwaitValue(bookingViewModel.getPickedStartingTime()), is(not(nullValue())));
    }

    @Test
    public void updateSlotOffer_setsNewValue() throws InterruptedException {
        // Given the slot offer gets updated
        bookingViewModel.updateSlotOffer(slotOffer);
        // Then it should update the livedata's value
        assertThat(getOrAwaitValue(bookingViewModel.getPickedSlotOffer()), is(not(nullValue())));
        assertThat(getOrAwaitValue(bookingViewModel.getPickedSlotOffer()), is(slotOffer));
        assertThat(bookingViewModel.getPickedSlotOfferValue(), is(slotOffer));
    }

    @Test
    public void updatePickedDate_setsNewValue() throws InterruptedException {
        // Given the date gets updated
        bookingViewModel.updatePickedDate(1, 1, 1);
        String output = Utility.dateToString(1, 1, 1);
        // Then it should update the livedata's value
        assertThat(getOrAwaitValue(bookingViewModel.getPickedDate()), is(not(nullValue())));
        assertThat(getOrAwaitValue(bookingViewModel.getPickedDate()), is(output));
        assertThat(bookingViewModel.getPickedDateValue(), is(output));
    }


    @Test
    public void updateStartingTime_setsNewValue() throws InterruptedException {
        // Given the date gets updated
        int hours = 12, minutes = 0;
        bookingViewModel.updateStartingTime(hours, minutes);
        String output = Utility.getTimeOf(hours, minutes);
        // Then it should update the livedata's value
        assertThat(getOrAwaitValue(bookingViewModel.getPickedStartingTime()), is(not(nullValue())));
        assertThat(getOrAwaitValue(bookingViewModel.getPickedStartingTime()), is(output));
        assertThat(bookingViewModel.getPickedStartingTimeValue(), is(output));
    }
}