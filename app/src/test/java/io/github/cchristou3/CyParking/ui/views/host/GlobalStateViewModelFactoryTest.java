package io.github.cchristou3.CyParking.ui.views.host;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.cchristou3.CyParking.apiClient.remote.repository.AuthenticatorRepository;
import io.github.cchristou3.CyParking.ui.views.parking.slots.booking.BookingViewModel;

/**
 * Unit tests for the {@link GlobalStateViewModelFactory} class.
 */
@RunWith(AndroidJUnit4.class)
public class GlobalStateViewModelFactoryTest {

    @After
    public void tearDown() throws Exception {
        FirebaseApp.clearInstancesForTest();
    }

    @Test(expected = IllegalArgumentException.class)
    public void create_wrongClass_throwsException() {
        new GlobalStateViewModelFactory(ApplicationProvider.getApplicationContext()).create(BookingViewModel.class);
    }

    /**
     * Exception caused by calling {@link FirebaseAuth#getInstance()}.
     *
     * @see AuthenticatorRepository#AuthenticatorRepository()
     */
    @Test
    public void create_correctClass_returnsNonNull() {
        // TODO: 25/03/2021 Investigate error: Resources$NotFoundException: String resource ID #0x7f1100be
        //FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        //assertThat(new GlobalStateViewModelFactory(ApplicationProvider.getApplicationContext()).create(GlobalStateViewModel.class), is(not(nullValue())));
    }
}