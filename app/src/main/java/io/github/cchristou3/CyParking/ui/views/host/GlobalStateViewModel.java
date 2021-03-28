package io.github.cchristou3.CyParking.ui.views.host;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

import io.github.cchristou3.CyParking.apiClient.model.data.user.LoggedInUser;
import io.github.cchristou3.CyParking.apiClient.remote.repository.AuthenticatorRepository;
import io.github.cchristou3.CyParking.ui.components.LoadingBarViewModel;
import io.github.cchristou3.CyParking.ui.views.user.login.AuthenticatorFragment;

import static io.github.cchristou3.CyParking.ui.views.host.MainHostActivity.TAG;

/**
 * Purpose: <p>Data persistence when configuration changes.
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
 * @version 5.0 27/03/21
 */
public class GlobalStateViewModel extends LoadingBarViewModel {

    // User state - initially set to null
    private final MutableLiveData<LoggedInUser> mUserState = new MutableLiveData<>(null);
    // connectivity state
    private final ConnectionLiveData mConnectionState;
    // no connection layout state
    private final MutableLiveData<Integer> mNoConnectionWarningState = new MutableLiveData<>();
    // The label of the screen
    private final MutableLiveData<Integer> mLabel = new MutableLiveData<>();
    // ViewModel's API
    private final AuthenticatorRepository mAuthenticatorRepository;


    /**
     * Initialize the ViewModel's AuthenticatorRepository instance
     * with the given argument. Also, creates a new instance of
     * {@link ConnectionLiveData} with the given context
     *
     * @param context                 The context to make use of.
     * @param authenticatorRepository An AuthenticatorRepository instance.
     */
    public GlobalStateViewModel(Context context, AuthenticatorRepository authenticatorRepository) {
        this.mAuthenticatorRepository = authenticatorRepository;
        this.mConnectionState = new ConnectionLiveData(context);
    }

    /**
     * Return the {@link #mNoConnectionWarningState} as a {@link LiveData} instance.
     *
     * @return A {@link LiveData} reference of {@link #mNoConnectionWarningState}.
     */
    /*package-private*/ LiveData<Integer> getNoConnectionWarningState() {
        return mNoConnectionWarningState;
    }

    /**
     * Assign the value of {@link #mNoConnectionWarningState}
     * the given argument.
     *
     * @param visibility the new value of {@link #mNoConnectionWarningState}.
     */
    /*package-private*/ void updateNoConnectionWarningState(int visibility) {
        mNoConnectionWarningState.setValue(visibility);
    }

    /**
     * Return the {@link #mConnectionState} as a {@link LiveData} instance.
     *
     * @return A {@link LiveData} reference of {@link #mConnectionState}.
     */
    public LiveData<Boolean> getConnectionState() {
        return this.mConnectionState;
    }

    /**
     * Assign the value of {@link #mLabel}
     * the given argument.
     *
     * @param label the new value of {@link #mLabel}.
     */
    public void updateLabel(Integer label) {
        mLabel.setValue(label);
    }

    /**
     * Return the {@link #mLabel} as a {@link LiveData} instance.
     *
     * @return A {@link LiveData} reference of {@link #mLabel}.
     */
    public LiveData<Integer> getLabelState() {
        return this.mLabel;
    }

    /**
     * Access the {@link #mAuthenticatorRepository}.
     *
     * @return A reference to {@link #mAuthenticatorRepository}.
     */
    public AuthenticatorRepository getAuthenticatorRepository() {
        return mAuthenticatorRepository;
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
     * @param user    the current instance of {@link FirebaseUser}.
     */
    public void getUserInfo(@NonNull Context context, @Nullable FirebaseUser user) {
        if (user == null) return; // If not logged in, then terminate method.
        mAuthenticatorRepository.getUserInfo(context, user, new AuthenticatorRepository.UserDataHandler() {
            @Override
            public void onLocalDataFound(List<String> roles) {
                Log.d(TAG, "getUserInfo: data found locally");
                mUserState.setValue(new LoggedInUser(user, roles));
            }

            @Override
            public void onRemoteDataFound(Task<DocumentSnapshot> task) {
                try {
                    final LoggedInUser loggedInUser = task.getResult().toObject(LoggedInUser.class);
                    mUserState.setValue(loggedInUser);
                } catch (NullPointerException e) {
                    onRemoteDataNotFound(e);
                    Log.e(TAG, "getUserInfo: ", e);
                }
            }

            @Override
            public void onRemoteDataNotFound(Exception exception) {
                // No records found neither locally, nor remotely, so sign him/her out.
                signOut();
            }
        });
    }

    /**
     * Signs the user out.
     */
    public void signOut() {
        mAuthenticatorRepository.signOut();
        updateAuthState(null); // Update the userState to null
    }
}
