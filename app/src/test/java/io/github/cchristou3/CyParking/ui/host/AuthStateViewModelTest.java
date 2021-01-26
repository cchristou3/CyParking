package io.github.cchristou3.CyParking.ui.host;

import android.view.View;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import io.github.cchristou3.CyParking.data.manager.ConnectivityHelper;
import io.github.cchristou3.CyParking.data.model.user.LoggedInUser;
import io.github.cchristou3.CyParking.data.repository.AuthenticatorRepository;
import io.github.cchristou3.CyParking.ui.InstantTaskRuler;

import static io.github.cchristou3.CyParking.ui.LiveDataTestUtil.getOrAwaitValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

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

    @Test
    public void updateNoConnectionWarningState_setsNewValue() throws InterruptedException {
        // Given the connection warning state got changed
        int visibility = View.VISIBLE;
        // When updateNoConnectionWarningState gets called
        authStateViewModel.updateNoConnectionWarningState(visibility);
        // Then calling getNoConnectionWarningState should return the same value
        assertThat(getOrAwaitValue(authStateViewModel.getNoConnectionWarningState()), is(visibility));
    }

    @Test
    public void updateConnectionState_setsNewValue() throws InterruptedException {
        // Given the connection state got changed
        boolean isConnected = true;
        // When updateConnectionState gets called
        authStateViewModel.updateConnectionState(isConnected);
        // Then calling getConnectionState should return the same value
        assertThat(getOrAwaitValue(authStateViewModel.getConnectionState()), is(isConnected));
    }

    @Test
    public void setInitialConnectionState_setsNewValue() throws InterruptedException {
        // Given the connectivity helper's isConnected method returns true
        ConnectivityHelper helper = Mockito.mock(ConnectivityHelper.class);
        when(helper.isConnected()).thenReturn(true);
        // When setInitialConnectionState gets called
        authStateViewModel.setInitialConnectionState(helper);
        // Then calling getConnectionState should return the same value
        assertThat(getOrAwaitValue(authStateViewModel.getConnectionState()), is(helper.isConnected()));
    }

}