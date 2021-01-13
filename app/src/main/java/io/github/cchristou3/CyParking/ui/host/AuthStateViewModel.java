package io.github.cchristou3.CyParking.ui.host;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import io.github.cchristou3.CyParking.data.manager.SharedPreferencesManager;
import io.github.cchristou3.CyParking.data.model.user.LoggedInUser;
import io.github.cchristou3.CyParking.data.repository.AuthenticatorRepository;
import io.github.cchristou3.CyParking.ui.user.login.AuthenticatorFragment;

import static io.github.cchristou3.CyParking.ui.host.MainHostActivity.TAG;

/**
 * Purpose: <p>Data persistence when orientation changes.
 * The primary mean of communicating between the fragments
 * and their hosting activity in terms of the Auth State data.
 * <p>
 * <strong>Note:</strong>
 * <p>
 * The {@link #mUserState} is initially set to null.
 * Its value is initially updated inside {@link MainHostActivity}'s onCreate method
 * via the invocation of {@link #getUserInfo(Context, FirebaseUser)}.
 * The state also changes when the user is logging in or registering in {@link AuthenticatorFragment}
 * and when signing out by clicking the action bar's "sign out" option.
 * </p>
 *
 * @author Charalambos Christou
 * @version 2.0 12/01/21
 */
public class AuthStateViewModel extends ViewModel {

    // Initially set to null
    private final MutableLiveData<LoggedInUser> mUserState = new MutableLiveData<>(null);
    private final AuthenticatorRepository mAuthenticatorRepository;

    /**
     * Initialize the ViewModel's AuthenticatorRepository instance
     * with the given argument.
     *
     * @param authenticatorRepository An AuthenticatorRepository instance.
     */
    public AuthStateViewModel(AuthenticatorRepository authenticatorRepository) {
        this.mAuthenticatorRepository = authenticatorRepository;
    }

    /**
     * Access the user's state.
     *
     * @return A reference to the user's state.
     */
    public MutableLiveData<LoggedInUser> getUserState() {
        return mUserState;
    }

    /**
     * Access the current set {@link LoggedInUser} instance.
     *
     * @return A reference to current {@link LoggedInUser} instance
     * if there is one.
     */
    @Nullable
    public LoggedInUser getUser() {
        return mUserState.getValue();
    }

    /**
     * Updates the value of the user state with the given argument.
     *
     * @param user The latest {@link LoggedInUser} instance.
     */
    public void updateAuthState(LoggedInUser user) {
        mUserState.setValue(user);
    }

    /**
     * Initially retrieves the user's data locally. If not found,
     * fetches them from the backend.
     *
     * @param context The context of the screen.
     */
    public void getUserInfo(@NonNull Context context, @Nullable FirebaseUser user) {
        if (user == null) return; // If user not set (logged in), terminate the method.
        // Otherwise,
        // Access the user's role locally via the SharedPreferences using the
        // user's id as the key
        List<String> roles = new SharedPreferencesManager(context.getApplicationContext()).getValue(user.getUid());

        // Local data is found
        if (!(roles == null || roles.isEmpty())) {
            Log.d(TAG, "getUserInfo: data found locally");
            mUserState.setValue(new LoggedInUser(user, roles));
        } else {
            // fetch user's data from the database
            mAuthenticatorRepository.getUser(user)
                    .addOnCompleteListener(task -> {
                        Log.d(TAG, "getUserInfo: data found on server");
                        if (task.getException() != null) {
                            mUserState.setValue(null);
                            return;
                        }
                        if (task.isSuccessful()) {
                            try {
                                final LoggedInUser loggedInUser = task.getResult().toObject(LoggedInUser.class);
                                mUserState.setValue(loggedInUser);
                            } catch (NullPointerException e) {
                                mUserState.setValue(null);
                                Log.e(TAG, "getUserInfo: ", e);
                            }
                        }
                    });
        }
    }

    /**
     * Signs the user out.
     */
    public void signOut() {
        mAuthenticatorRepository.signOut();
        updateAuthState(null); // Update the userState to null
    }
}
