package io.github.cchristou3.CyParking.ui.parking.slots.viewBooking;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.Query;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import io.github.cchristou3.CyParking.data.model.parking.slot.booking.Booking;
import io.github.cchristou3.CyParking.data.repository.BookingRepository;
import io.github.cchristou3.CyParking.ui.InstantTaskRuler;

import static io.github.cchristou3.CyParking.ui.LiveDataTestUtil.getOrAwaitValue;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link ViewBookingsViewModel} class.
 */
@RunWith(AndroidJUnit4.class)
public class ViewBookingsViewModelTest extends InstantTaskRuler {

    // Subject under test
    private ViewBookingsViewModel viewBookingsViewModel;

    @Before
    public void setUp() {
        BookingRepository mockRepository = Mockito.mock(BookingRepository.class);
        when(mockRepository.retrieveUserBookings("mockId")).thenReturn(Mockito.mock(Query.class));
        viewBookingsViewModel = new ViewBookingsViewModel(mockRepository);
    }

    @Test
    public void getBookingList_returnsNonNull() {
        assertThat(viewBookingsViewModel.getBookingList(), is(not(nullValue())));
    }

    @Test
    public void updateBookingList_setsNewValue() throws InterruptedException {
        // Given a new list got received
        List<Booking> bookings = Collections.singletonList(new Booking());
        // When the booking list got updated
        viewBookingsViewModel.updateBookingList(bookings);
        // Then the LiveData's value should update
        assertThat(getOrAwaitValue(viewBookingsViewModel.getBookingList()), is(not(nullValue())));
        assertThat(getOrAwaitValue(viewBookingsViewModel.getBookingList()), is(bookings));
    }

    @Test
    public void updateBookingList_null_setsNullValue() throws InterruptedException {
        // When the booking list got updated to null
        viewBookingsViewModel.updateBookingList(null);
        // Then the LiveData's value should update
        assertThat(getOrAwaitValue(viewBookingsViewModel.getBookingList()), is(nullValue()));
    }

    @Test
    public void updateBookingList_emptyList_setsEmptyListValue() throws InterruptedException {
        // Given an empty list got received
        List<Booking> bookings = Collections.EMPTY_LIST;
        // When the booking list got updated to null
        viewBookingsViewModel.updateBookingList(bookings);
        // Then the LiveData's value should update
        assertThat(getOrAwaitValue(viewBookingsViewModel.getBookingList()), is(not(nullValue())));
        assertThat(getOrAwaitValue(viewBookingsViewModel.getBookingList()), is(bookings));
    }


    @Test
    public void retrieveUserBookings_returnsNonNull() {
        assertThat(viewBookingsViewModel.retrieveUserBookings("mockId"), is(not(nullValue())));
    }
}