package io.github.cchristou3.CyParking.ui.host;

import org.junit.Test;

import io.github.cchristou3.CyParking.data.repository.AuthenticatorRepository;
import io.github.cchristou3.CyParking.ui.parking.slots.booking.BookingViewModel;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for the {@link AuthStateViewModelFactory} class.
 */
public class AuthStateViewModelFactoryTest {
    @Test(expected = IllegalArgumentException.class)
    public void create_wrongClass_throwsException() {
        new AuthStateViewModelFactory().create(BookingViewModel.class);
    }

    /**
     * @see AuthenticatorRepository#AuthenticatorRepository()
     */
    @Test(expected = ExceptionInInitializerError.class)
    public void create_correctClass_returnsNonNull() {
        assertThat(new AuthStateViewModelFactory().create(AuthStateViewModel.class), is(not(nullValue())));
    }
}