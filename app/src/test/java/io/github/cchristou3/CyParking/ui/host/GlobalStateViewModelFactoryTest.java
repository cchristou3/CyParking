package io.github.cchristou3.CyParking.ui.host;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Test;

import io.github.cchristou3.CyParking.data.repository.AuthenticatorRepository;
import io.github.cchristou3.CyParking.ui.parking.slots.booking.BookingViewModel;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for the {@link GlobalStateViewModelFactory} class.
 */
public class GlobalStateViewModelFactoryTest {
    @Test(expected = IllegalArgumentException.class)
    public void create_wrongClass_throwsException() {
        new GlobalStateViewModelFactory().create(BookingViewModel.class);
    }

    /**
     * Exception caused by calling {@link FirebaseAuth#getInstance()}.
     *
     * @see AuthenticatorRepository#AuthenticatorRepository()
     */
    @Test(expected = ExceptionInInitializerError.class)
    public void create_correctClass_returnsNonNull() {
        assertThat(new GlobalStateViewModelFactory().create(GlobalStateViewModel.class), is(not(nullValue())));
    }
}