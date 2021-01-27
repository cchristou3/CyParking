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
 * Unit tests for the {@link GlobalStateViewModel} class.
 */
@RunWith(AndroidJUnit4.class)
public class GlobalStateViewModelTest extends InstantTaskRuler {

    // Subject under test
    private GlobalStateViewModel globalStateViewModel;

    @Before
    public void setUp() {
        AuthenticatorRepository mockRepository = Mockito.mock(AuthenticatorRepository.class);
        globalStateViewModel = new GlobalStateViewModel(mockRepository);
    }

    @Test
    public void getUserState_returnsNonNull() {
        assertThat(globalStateViewModel.getUserState(), not(nullValue()));
    }

    @Test
    public void getUser_returnsNullInitially() {
        assertThat(globalStateViewModel.getUser(), nullValue());
    }

    @Test
    public void updateAuthState_setsNewValue() throws InterruptedException {
        // When the state is updated
        globalStateViewModel.updateAuthState(Mockito.mock(LoggedInUser.class));
        // Then the state's value is no longer null
        assertThat(getOrAwaitValue(globalStateViewModel.getUserState()), not(nullValue()));
    }

    @Test
    public void signOut_setsUserStateToNull() throws InterruptedException {
        // When the user pressed the log out button
        globalStateViewModel.signOut();
        // Then
        assertThat(getOrAwaitValue(globalStateViewModel.getUserState()), nullValue());
    }

    @Test
    public void updateNoConnectionWarningState_setsNewValue() throws InterruptedException {
        // Given the connection warning state got changed
        int visibility = View.VISIBLE;
        // When updateNoConnectionWarningState gets called
        globalStateViewModel.updateNoConnectionWarningState(visibility);
        // Then calling getNoConnectionWarningState should return the same value
        assertThat(getOrAwaitValue(globalStateViewModel.getNoConnectionWarningState()), is(visibility));
    }

    @Test
    public void updateConnectionState_setsNewValue() throws InterruptedException {
        // Given the connection state got changed
        boolean isConnected = true;
        // When updateConnectionState gets called
        globalStateViewModel.updateConnectionState(isConnected);
        // Then calling getConnectionState should return the same value
        assertThat(getOrAwaitValue(globalStateViewModel.getConnectionState()), is(isConnected));
    }

    @Test
    public void setInitialConnectionState_setsNewValue() throws InterruptedException {
        // Given the connectivity helper's isConnected method returns true
        ConnectivityHelper helper = Mockito.mock(ConnectivityHelper.class);
        when(helper.isConnected()).thenReturn(true);
        // When setInitialConnectionState gets called
        globalStateViewModel.setInitialConnectionState(helper);
        // Then calling getConnectionState should return the same value
        assertThat(getOrAwaitValue(globalStateViewModel.getConnectionState()), is(helper.isConnected()));
    }

    @Test
    public void showLoadingBar_setsValueToTrue() throws InterruptedException {
        // When showLoadingBar gets invoked
        globalStateViewModel.showLoadingBar();
        // Then its value should be set to true
        assertThat(getOrAwaitValue(globalStateViewModel.getLoadingBarState()), is(true));
        assertThat(globalStateViewModel.isLoadingBarShowing(), is(true));
    }

    @Test
    public void hideLoadingBar_setsValueToFalse() throws InterruptedException {
        // When showLoadingBar gets invoked
        globalStateViewModel.hideLoadingBar();
        // Then its value should be set to true
        assertThat(getOrAwaitValue(globalStateViewModel.getLoadingBarState()), is(false));
        assertThat(globalStateViewModel.isLoadingBarShowing(), is(false));
    }
}