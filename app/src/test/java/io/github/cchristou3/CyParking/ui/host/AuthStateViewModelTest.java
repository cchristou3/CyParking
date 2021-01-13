package io.github.cchristou3.CyParking.ui.host;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;

import io.github.cchristou3.CyParking.data.manager.SharedPreferencesManager;
import io.github.cchristou3.CyParking.data.model.user.LoggedInUser;
import io.github.cchristou3.CyParking.data.repository.AuthenticatorRepository;

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
public class AuthStateViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

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
        // When that the state is updated
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
    public void getUserInfo_userAlreadyLoggedIn_accessTheRoles() throws InterruptedException {
        // Given the user was already logged in
        SharedPreferencesManager manager = new SharedPreferencesManager(
                ApplicationProvider.getApplicationContext()
        );
        // thus his roles are saved locally
        FirebaseUser mockUser = Mockito.mock(FirebaseUser.class);
        when(mockUser.getUid()).thenReturn("1");
        manager.setValue(mockUser.getUid(), new ArrayList<>(Arrays.asList("User")));

        // When user comes back to the app, getUseInfo gets invoked
        // and updates the user state
        authStateViewModel.getUserInfo(
                ApplicationProvider.getApplicationContext(),
                mockUser
        );

        // Then user state's roles should be equivalent, with the ones stores locally
        assertThat(getOrAwaitValue(authStateViewModel.getUserState()).getRoles().size(),
                is(1));
    }
}