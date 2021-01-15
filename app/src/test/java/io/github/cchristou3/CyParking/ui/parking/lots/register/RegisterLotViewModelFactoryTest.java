package io.github.cchristou3.CyParking.ui.parking.lots.register;

import org.junit.Test;

import io.github.cchristou3.CyParking.ui.parking.slots.booking.BookingViewModel;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for the {@link RegisterLotViewModelFactory} class.
 */
public class RegisterLotViewModelFactoryTest {

    @Test(expected = IllegalArgumentException.class)
    public void create_wrongClass_throwsException() {
        new RegisterLotViewModelFactory().create(BookingViewModel.class);
    }

    public void create_correctClass_returnsNonNull() {
        assertThat(new RegisterLotViewModelFactory().create(RegisterLotViewModel.class), is(not(nullValue())));
    }
}