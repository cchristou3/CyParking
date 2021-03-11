package io.github.cchristou3.CyParking.ui.views.parking.slots.booking;

import org.junit.Test;
import org.mockito.Mockito;

import io.github.cchristou3.CyParking.ui.views.host.GlobalStateViewModel;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for the {@link BookingViewModelFactory} class.
 */
public class BookingViewModelFactoryTest {
    @Test(expected = IllegalArgumentException.class)
    public void create_wrongClass_throwsException() {
        new BookingViewModelFactory(Mockito.mock(BookingFragment.class)).create(GlobalStateViewModel.class);
    }

    public void create_correctClass_returnsNonNull() {
        assertThat(new BookingViewModelFactory(Mockito.mock(BookingFragment.class)).create(BookingViewModel.class), is(not(nullValue())));
    }

}