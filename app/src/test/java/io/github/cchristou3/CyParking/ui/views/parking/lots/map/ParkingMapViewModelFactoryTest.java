package io.github.cchristou3.CyParking.ui.views.parking.lots.map;

import org.junit.Test;

import io.github.cchristou3.CyParking.ui.views.parking.slots.booking.BookingViewModel;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for the {@link ParkingMapViewModelFactory} class.
 */
public class ParkingMapViewModelFactoryTest {
    @Test(expected = IllegalArgumentException.class)
    public void create_wrongClass_throwsException() {
        new ParkingMapViewModelFactory().create(BookingViewModel.class);
    }

    public void create_correctClass_returnsNonNull() {
        assertThat(new ParkingMapViewModelFactory().create(ParkingMapViewModel.class), is(not(nullValue())));
    }
}