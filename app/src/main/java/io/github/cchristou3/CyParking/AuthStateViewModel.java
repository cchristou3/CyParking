package io.github.cchristou3.CyParking;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import io.github.cchristou3.CyParking.data.manager.SharedPreferencesManager;
import io.github.cchristou3.CyParking.data.pojo.user.LoggedInUser;
import io.github.cchristou3.CyParking.data.repository.AuthenticatorRepository;

import static io.github.cchristou3.CyParking.MainHostActivity.TAG;

/**
 * Purpose: <p>Data persistence when orientation changes.
 * The primary mean of communicating between the fragments
 * and their hosting activity in terms of the Auth State data.
 *
 * @author Charalambos Christou
 * @version 1.0 25/12/20
 */
public class AuthStateViewModel extends ViewModel {

    // Initially set to null
    private final MutableLiveData<LoggedInUser> userState = new MutableLiveData<>(null);
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
     * Acccess the user's state.
     *
     * @return A reference to the user's state.
     */
    public MutableLiveData<LoggedInUser> getUserState() {
        return userState;
    }

    /**
     * Updates the value of the user state with the given argument.
     *
     * @param user The latest {@link LoggedInUser} instance.
     */
    public void updateAuthState(LoggedInUser user) {
        userState.setValue(user);
    }

    /**
     * Initially retrieves the user's data locally. If not found,
     * fetches them from the backend.
     *
     * @param context The context of the screen.
     */
    public void getUserInfo(Context context) {
        // Access the FirebaseUser instance via FirebaseAuth
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) return; // If user not set (logged in), terminate the method.

        // Otherwise,
        // Access the user's role locally via the SharedPreferences using the
        // user's id as the key
        List<String> roles = new SharedPreferencesManager(context.getApplicationContext()).getValue(user.getUid());

        // Local data is found
        if (!(roles == null || roles.isEmpty())) {
            Log.d(TAG, "getUserInfo: data found locally");
            userState.setValue(new LoggedInUser(user, roles));
        } else {
            // fetch user's data from the database
            mAuthenticatorRepository.getUser(user)
                    .addOnCompleteListener(task -> {
                        Log.d(TAG, "getUserInfo: data found on server");
                        if (task.getException() != null) {
                            userState.setValue(null);
                            return;
                        }
                        if (task.isSuccessful()) {
                            try {
                                final LoggedInUser loggedInUser = task.getResult().toObject(LoggedInUser.class);
                                userState.setValue(loggedInUser);
                            } catch (NullPointerException e) {
                                userState.setValue(null);
                                Log.e(TAG, "getUserInfo: ", e);
                            }
                        }
                    });
        }

    }

    /**
     * @return true if the userState is set (logged in). Otherwise, false.
     */
    public boolean isLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }
}
