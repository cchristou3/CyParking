package io.github.cchristou3.CyParking.ui.views.user.login;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.github.cchristou3.CyParking.data.repository.AuthenticatorRepository;
import io.github.cchristou3.CyParking.ui.views.parking.slots.booking.BookingViewModel;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for the {@link AuthenticatorViewModelFactory} class.
 */
public class AuthenticatorViewModelFactoryTest {

    @Mock
    private AuthenticatorRepository mockAuthRepo;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = IllegalArgumentException.class)
    public void create_wrongClass_throwsException() {
        new AuthenticatorViewModelFactory(mockAuthRepo).create(BookingViewModel.class);
    }

    public void create_correctClass_returnsNonNull() {
        assertThat(new AuthenticatorViewModelFactory(mockAuthRepo).create(AuthenticatorViewModel.class), is(not(nullValue())));
    }
}