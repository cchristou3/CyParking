package io.github.cchristou3.CyParking.ui.user.login;

import android.content.Context;
import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.data.pojo.form.login.LoginFormState;
import io.github.cchristou3.CyParking.data.pojo.form.login.LoginResult;
import io.github.cchristou3.CyParking.data.repository.AuthenticatorRepository;

/**
 * Purpose: <p>Data persistence when orientation changes. Shared amongst all tab fragments.
 * Used when the user tries to login/register.</p>
 *
 * @author Charalambos Christou
 * @version 1.0 1/11/20
 */
public class AuthenticatorViewModel extends ViewModel {

    private final MutableLiveData<LoginFormState> mLoginFormState = new MutableLiveData<>();
    private final MutableLiveData<LoginResult> mAuthenticatorResult = new MutableLiveData<>();

    private final MutableLiveData<String> mEmailState = new MutableLiveData<>();
    private final MutableLiveData<String> mPasswordState = new MutableLiveData<>();
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
     * A placeholder username validation check.
     *
     * @param username The username of the user.
     * @return true if it passes the criteria.
     */
    public static boolean isEmailValid(String username) {
        if (username == null || username.trim().isEmpty()) return false;

        return Patterns.EMAIL_ADDRESS.matcher(username).matches();
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
        mAuthenticatorRepository.login(context, email, password, mAuthenticatorResult);
    }

    /**
     * Connect with our backend to sign the user up.
     *
     * @param username   The user name of the user.
     * @param password   The password of the user.
     * @param isUser     true if the user selected the checkbox which corresponds to the user. Otherwise, false.
     * @param isOperator true if the user selected the checkbox which corresponds to the operator. Otherwise, false.
     * @param context    The context of the current tab.
     */
    public void register(String username, String password, boolean isUser, boolean isOperator, Context context) {
        // can be launched in a separate asynchronous job
        try {
            mAuthenticatorRepository.register(username, password, mAuthenticatorResult, isUser, isOperator, context);
        } catch (IllegalArgumentException e) {
            mAuthenticatorResult.setValue(new LoginResult(e.getMessage()));
        }
    }

    /**
     * Re-authenticates the user with the given credentials.
     *
     * @param credentials The user's credentials
     * @return A task to be handled by the view.
     */
    @NotNull
    public Task<AuthResult> reauthenticateUser(String credentials) {
        return mAuthenticatorRepository.reauthenticateUser(credentials);
    }

    /**
     * Validates whether the user picked any of the roles.
     *
     * @param isUser     true if the user selected the checkbox which corresponds to the user. Otherwise, false.
     * @param isOperator true if the user selected the checkbox which corresponds to the operator. Otherwise, false.
     * @return True if the user picked any of the roles. Otherwise, false.
     */
    private boolean AreAnyRolesSelected(boolean isUser, boolean isOperator) {
        return isUser || isOperator;
    }

    /**
     * Validates all elements our our login / registration form.
     *
     * @param email      The email of the user.
     * @param password   The password of the user.
     * @param isUser     true if the user selected the checkbox which corresponds to the user. Otherwise, false.
     * @param isOperator true if the user selected the checkbox which corresponds to the operator. Otherwise, false.
     */
    public void dataChanged(String email, String password, boolean isUser, boolean isOperator) {
        mPasswordState.setValue(password);
        mEmailState.setValue(email);
        if (!isEmailValid(email)) {
            mLoginFormState.setValue(new LoginFormState(R.string.invalid_email, null, null));
        } else if (!isPasswordValid(password)) {
            mLoginFormState.setValue(new LoginFormState(null, R.string.invalid_password, null));
        } else if (!AreAnyRolesSelected(isUser, isOperator) && !isUserSigningIn()) { // Checks only if the user is registering
            mLoginFormState.setValue(new LoginFormState(null, null, R.string.invalid_role_choice));
        } else {
            mLoginFormState.setValue(new LoginFormState(true));
        }
    }

    /**
     * Getters & Setters
     */

    public void updateEmail(String email) {
        mEmailState.setValue(email);
    }

    public MutableLiveData<Boolean> getTabState() {
        return mIsUserInSigningInTab;
    }

    public boolean isUserSigningIn() {
        if (mIsUserInSigningInTab.getValue() != null)
            return mIsUserInSigningInTab.getValue();
        else
            return false; // By default
    }

    public void setUserSigningIn(boolean userSigningIn) {
        mIsUserInSigningInTab.setValue(userSigningIn);
    }

    /**
     * Getters
     */
    public MutableLiveData<String> getEmailState() {
        return mEmailState;
    }

    public MutableLiveData<String> getPasswordState() {
        return mPasswordState;
    }

    MutableLiveData<LoginFormState> getLoginFormState() {
        return mLoginFormState;
    }

    LiveData<LoginResult> getAuthenticatorResult() {
        return mAuthenticatorResult;
    }

}