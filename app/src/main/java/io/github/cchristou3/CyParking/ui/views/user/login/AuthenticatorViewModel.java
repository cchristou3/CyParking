package io.github.cchristou3.CyParking.ui.views.user.login;

import android.content.Context;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.apiClient.local.SharedPreferencesManager;
import io.github.cchristou3.CyParking.apiClient.model.data.user.LoggedInUser;
import io.github.cchristou3.CyParking.apiClient.remote.repository.AccountRepository;
import io.github.cchristou3.CyParking.apiClient.remote.repository.AuthenticatorRepository;
import io.github.cchristou3.CyParking.data.pojo.form.login.AuthFormState;
import io.github.cchristou3.CyParking.data.pojo.form.login.AuthResult;

import static io.github.cchristou3.CyParking.apiClient.model.data.parking.lot.ParkingLot.isNameValid;

/**
 * Purpose: <p>Data persistence when configuration changes. Shared amongst all tab fragments.
 * Used when the user tries to login/register.</p>
 *
 * @author Charalambos Christou
 * @version 4.0 24/03/21
 */
public class AuthenticatorViewModel extends ViewModel {

    private final MutableLiveData<AuthFormState> mAuthFormState = new MutableLiveData<>();
    private final MutableLiveData<AuthResult> mAuthResultState = new MutableLiveData<>();

    private final MutableLiveData<String> mEmailState = new MutableLiveData<>();
    private final MutableLiveData<String> mNameState = new MutableLiveData<>();
    private final MutableLiveData<String> mPasswordState = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mOperatorCheckedState = new MutableLiveData<>(false);

    private final AuthenticatorRepository mAuthenticatorRepository;
    // First fragment which appears to the user is the "sign in" tab
    private final MutableLiveData<Boolean> mIsUserInSigningInTab = new MutableLiveData<>(true);

    /**
     * Initialize the ViewModel's AuthenticatorRepository instance with the given
     * argument.
     *
     * @param authenticatorRepository An AuthenticatorRepository instance.
     */
    AuthenticatorViewModel(AuthenticatorRepository authenticatorRepository) {
        this.mAuthenticatorRepository = authenticatorRepository;
    }

    /**
     * A placeholder email validation check.
     *
     * @param email The email of the user.
     * @return true if it passes the criteria.
     */
    public static boolean isEmailValid(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * A password validation check
     *
     * @param password The password of the user.
     * @return true if it passes the criteria.
     */
    public static boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    /**
     * Connect with our backend to log the user in.
     *
     * @param email    The user name of the user.
     * @param password The password of the user.
     */
    public void login(Context context, String email, String password) {
        // can be launched in a separate asynchronous job
        mAuthenticatorRepository.
                login(email, password)
                .addOnCompleteListener(loginTask -> {
                    // Check whether an exception occurred
                    if (loginTask.getException() != null) {
                        updateAuthResult(loginTask.getException().getMessage());
                        return;
                    }
                    // If the user successfully signed in
                    if (loginTask.isSuccessful() && loginTask.getResult().getUser() != null) {
                        mAuthenticatorRepository.getUserInfo(context, loginTask.getResult().getUser(),
                                new AuthenticatorRepository.UserDataHandler() {
                                    @Override
                                    public void onLocalDataFound(List<String> roles) {
                                        updateAuthResult(loginTask.getResult().getUser(), roles);
                                    }

                                    @Override
                                    public void onRemoteDataFound(Task<DocumentSnapshot> retrieveUserDataTask) {
                                        // The task was successful and the user has data stored on the server
                                        LoggedInUser loggedInUser = null;
                                        try {
                                            loggedInUser = retrieveUserDataTask.getResult().toObject(LoggedInUser.class);
                                        } catch (NullPointerException ignored) {
                                        }
                                        // A user without roles will be treated as a not-logged-in user.
                                        updateAuthResult(loggedInUser);
                                    }

                                    @Override
                                    public void onRemoteDataNotFound(Exception exception) {
                                        updateAuthResult(exception.getMessage());
                                    }
                                });
                    }
                });
    }

    /**
     * Connect with our backend to sign the user up.
     *
     * @param context The context of the current tab.
     */
    public void register(Context context) {
        // can be launched in a separate asynchronous job
        try {
            mAuthenticatorRepository.register(mEmailState.getValue(), mPasswordState.getValue())
                    .addOnCompleteListener(task -> {
                        // Check whether an exception occurred
                        if (task.getException() != null) {
                            mAuthResultState.setValue(new AuthResult(task.getException().getMessage()));
                            return;
                        }

                        // If the user successfully registered
                        if (task.isSuccessful() && task.getResult().getUser() != null) {
                            // Create a list to store the user's selected role(s)
                            List<String> roles = new ArrayList<>();
                            if (isOperatorChecked()) roles.add(LoggedInUser.OPERATOR);

                            // Save the user's roles locally via SharedPreferences
                            // Each user in the database has a unique Uid. Thus, to be used as the key.
                            storeRolesLocally(context, task.getResult().getUser().getUid(), roles);

                            final LoggedInUser loggedInUser = new LoggedInUser(task.getResult().getUser(), roles);
                            loggedInUser.setDisplayName(getName());

                            // Initialize the Repository's LoggedInUser instance
                            // and trigger loginResult observer update
                            updateAuthResult(loggedInUser);

                            // Save the user's data to the server asynchronously
                            mAuthenticatorRepository.addUser(loggedInUser);

                            // Update the user's display name asynchronously
                            new AccountRepository(FirebaseAuth.getInstance())
                                    .updateDisplayName(loggedInUser.getDisplayName(), loggedInUser);
                        }
                    });
        } catch (IllegalArgumentException e) {
            mAuthResultState.setValue(new AuthResult(e.getMessage()));
        }
    }

    /**
     * Store the given list of rules locally using the user user's uid as key.
     *
     * @param context The context to make use of.
     * @param uid     The user's uid.
     * @param roles   The user's roles.
     */
    private void storeRolesLocally(@NotNull Context context, String uid, List<String> roles) {
        new SharedPreferencesManager(context.getApplicationContext())
                .setValue(uid, roles);
    }

    /**
     * Check whether the operator value got checked.
     *
     * @return True if it was checked. Otherwise, false.
     */
    private boolean isOperatorChecked() {
        return mOperatorCheckedState.getValue() != null && mOperatorCheckedState.getValue();
    }

    /**
     * Re-authenticates the user with the given credentials.
     *
     * @param credentials The user's credentials
     * @return A task to be handled by the view.
     */
    @NotNull
    public Task<com.google.firebase.auth.AuthResult> reauthenticateUser(String credentials) {
        return mAuthenticatorRepository.reauthenticateUser(credentials);
    }

    /**
     * Validates all elements our our login / registration form.
     *
     * @param email    The email of the user.
     * @param password The password of the user.
     */
    public void dataChanged(String email, String name, String password) {
        mPasswordState.setValue(password);
        if (!isUserSigningIn()) mNameState.setValue(name);
        mEmailState.setValue(email);
        if (!isEmailValid(email)) {
            updateFromState(R.string.invalid_email, null, null);
        } else if (!isUserSigningIn() && !isNameValid(name)) {
            updateFromState(null, R.string.invalid_name, null);
        } else if (!isPasswordValid(password)) {
            updateFromState(null, null, R.string.invalid_password);
        } else {
            updateFromStateToValid();
        }
    }

    /**
     * Updates the value of {@link #mOperatorCheckedState}
     * based on the given error ids.
     *
     * @param isChecked the new value of {@link #mOperatorCheckedState}.
     */
    public void updateIsOperatorChecked(boolean isChecked) {
        mOperatorCheckedState.setValue(isChecked);
    }

    /**
     * Returns the validity of {@link #mAuthFormState}.
     *
     * @return True, if {@link #mAuthFormState} is valid. Otherwise, fasle.
     */
    public boolean isFormValid() {
        if (this.mAuthFormState.getValue() != null)
            return this.mAuthFormState.getValue().isDataValid();
        else
            return false;
    }

    /**
     * Updates the value of {@link #mAuthFormState}
     * based on the given error ids.
     *
     * @param emailError    The id of the email error.
     * @param nameError     The id of the name error.
     * @param passwordError The id of the password error.
     */
    private void updateFromState(Integer emailError, Integer nameError, Integer passwordError) {
        updateFromState(new AuthFormState(emailError, nameError, passwordError));
    }

    /**
     * Updates the value of {@link #mAuthFormState}
     * to valid.
     */
    private void updateFromStateToValid() {
        updateFromState(new AuthFormState(true));
    }

    /**
     * Updates the value of {@link #mAuthFormState}
     * based on the given {@link AuthFormState} instance.
     *
     * @param authFormState The new value of {@link #mAuthFormState}.
     */
    private void updateFromState(@NonNull AuthFormState authFormState) {
        this.mAuthFormState.setValue(authFormState);
    }

    /**
     * Updates the value of {@link #mEmailState}
     * to the given string.
     *
     * @param email The new value of {@link #mEmailState}.
     */
    public void updateEmail(String email) {
        mEmailState.setValue(email);
    }

    /**
     * Access the {@link #mIsUserInSigningInTab}
     * in its LiveData form.
     *
     * @return The LiveData reference of {@link #mIsUserInSigningInTab}.
     */
    public LiveData<Boolean> getTabState() {
        return mIsUserInSigningInTab;
    }

    /**
     * Access the value of {@link #mIsUserInSigningInTab}.
     *
     * @return The value of {@link #mIsUserInSigningInTab} if not null.
     * Otherwise, false.
     */
    public boolean isUserSigningIn() {
        if (mIsUserInSigningInTab.getValue() != null)
            return mIsUserInSigningInTab.getValue();
        else
            return false; // By default
    }

    /**
     * Updates the value of {@link #mIsUserInSigningInTab}
     * to the given boolean.
     *
     * @param userSigningIn The new value of {@link #mIsUserInSigningInTab}.
     */
    public void isUserSigningIn(boolean userSigningIn) {
        mIsUserInSigningInTab.setValue(userSigningIn);
    }

    /**
     * Updates the value of loginResult
     * based on the given LoggedInUser instance.
     *
     * @param user The current {@link LoggedInUser} instance.
     */
    private void updateAuthResult(@Nullable LoggedInUser user) {
        updateAuthResult(new AuthResult(user));
    }

    /**
     * Updates the value of loginResult
     * based on the given error.
     *
     * @param error The error message from the backend.
     */
    private void updateAuthResult(String error) {
        updateAuthResult(new AuthResult(error));
    }

    /**
     * Updates the value of loginResult
     * based on the given FirebaseUser instance and
     * its list of roles.
     *
     * @param user  A {@link FirebaseUser} object.
     * @param roles A list that holds the user's roles.
     */
    private void updateAuthResult(FirebaseUser user, List<String> roles) {
        updateAuthResult(new LoggedInUser(user, roles));
    }

    /**
     * Updates the value of loginResult
     * based on the given AuthResult instance.
     *
     * @param authResult A {@link AuthResult} object.
     */
    private void updateAuthResult(AuthResult authResult) {
        this.mAuthResultState.setValue(authResult);
    }

    /**
     * Access the {@link #mEmailState}
     * in its LiveData form.
     *
     * @return The LiveData reference of {@link #mEmailState}.
     */
    public LiveData<String> getEmailState() {
        return mEmailState;
    }

    /**
     * Access the value of {@link #mEmailState}.
     *
     * @return the value of {@link #mEmailState}.
     */
    public String getEmail() {
        return mEmailState.getValue();
    }

    /**
     * Access the value of {@link #mNameState}.
     *
     * @return the value of {@link #mNameState}.
     */
    public String getName() {
        return mNameState.getValue();
    }


    /**
     * Access the {@link #mPasswordState}
     * in its LiveData form.
     *
     * @return The LiveData reference of {@link #mPasswordState}.
     */
    public LiveData<String> getPasswordState() {
        return mPasswordState;
    }

    /**
     * Access the value of {@link #mPasswordState}.
     *
     * @return the value of {@link #mPasswordState}.
     */
    public String getPassword() {
        return mPasswordState.getValue();
    }

    /**
     * Access the {@link #mAuthFormState}
     * in its LiveData form.
     *
     * @return The LiveData reference of {@link #mAuthFormState}.
     */
    LiveData<AuthFormState> getFormState() {
        return mAuthFormState;
    }

    /**
     * Access the {@link #mAuthResultState}
     * in its LiveData form.
     *
     * @return The LiveData reference of {@link #mAuthResultState}.
     */
    LiveData<AuthResult> getAuthenticatorResult() {
        return mAuthResultState;
    }
}