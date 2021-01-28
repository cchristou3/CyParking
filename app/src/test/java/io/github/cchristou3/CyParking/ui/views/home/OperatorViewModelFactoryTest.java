package io.github.cchristou3.CyParking.ui.views.home;

import org.junit.Test;

import io.github.cchristou3.CyParking.ui.views.parking.slots.booking.BookingViewModel;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for the {@link OperatorViewModelFactory} class.
 */
public class OperatorViewModelFactoryTest {
    @Test(expected = IllegalArgumentException.class)
    public void create_wrongClass_throwsException() {
        new OperatorViewModelFactory().create(BookingViewModel.class);
    }

    public void create_correctClass_returnsNonNull() {
        assertThat(new OperatorViewModelFactory().create(OperatorViewModel.class), is(not(nullValue())));
    }
}