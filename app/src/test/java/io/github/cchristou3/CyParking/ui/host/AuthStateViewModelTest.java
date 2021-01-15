package io.github.cchristou3.CyParking.ui.host;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import io.github.cchristou3.CyParking.data.model.user.LoggedInUser;
import io.github.cchristou3.CyParking.data.repository.AuthenticatorRepository;
import io.github.cchristou3.CyParking.ui.InstantTaskRuler;

import static io.github.cchristou3.CyParking.ui.LiveDataTestUtil.getOrAwaitValue;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for the {@link AuthStateViewModel} class.
 */
@RunWith(AndroidJUnit4.class)
public class AuthStateViewModelTest extends InstantTaskRuler {

    // Subject under test
    private AuthStateViewModel authStateViewModel;

    @Before
    public void setUp() {
        AuthenticatorRepository mockRepository = Mockito.mock(AuthenticatorRepository.class);
        authStateViewModel = new AuthStateViewModel(mockRepository);
    }

    @Test
    public void getUserState_returnsNonNull() {
        assertThat(authStateViewModel.getUserState(), not(nullValue()));
    }

    @Test
    public void getUser_returnsNullInitially() {
        assertThat(authStateViewModel.getUser(), nullValue());
    }

    @Test
    public void updateAuthState_setsNewValue() throws InterruptedException {
        // When the state is updated
        authStateViewModel.updateAuthState(Mockito.mock(LoggedInUser.class));
        // Then the state's value is no longer null
        assertThat(getOrAwaitValue(authStateViewModel.getUserState()), not(nullValue()));
    }

    @Test
    public void signOut_setsUserStateToNull() throws InterruptedException {
        // When the user pressed the log out button
        authStateViewModel.signOut();
        // Then
        assertThat(getOrAwaitValue(authStateViewModel.getUserState()), nullValue());
    }
}