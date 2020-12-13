package io.github.cchristou3.CyParking.view.ui.login;

import android.content.Context;
import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.view.data.pojo.user.login.LoginFormState;
import io.github.cchristou3.CyParking.view.data.pojo.user.login.LoginResult;
import io.github.cchristou3.CyParking.view.data.repository.LoginRepository;

/**
 * Purpose: <p>Data persistence when orientation changes. Shared amongst all tab fragments.
 * Used when the user tries to login/register.</p>
 *
 * @author Charalambos Christou
 * @version 1.0 1/11/20
 */
public class LoginViewModel extends ViewModel {

    private final MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();

    private final MutableLiveData<String> emailState = new MutableLiveData<>();
    private final MutableLiveData<String> passwordState = new MutableLiveData<>();
    private final LoginRepository loginRepository;
    // First fragment which appears to the user is the "sign in" tab
    private final MutableLiveData<Boolean> isUserInSigningInTab = new MutableLiveData<>(true);

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    /**
     * Connect with our backend to log the user in.
     *
     * @param username The user name of the user.
     * @param password The password of the user.
     */
    public void login(String username, String password) {
        // can be launched in a separate asynchronous job
        loginRepository.login(username, password, loginResult);
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
            loginRepository.register(username, password, loginResult, isUser, isOperator, context);
        } catch (IllegalArgumentException e) {
            loginResult.setValue(new LoginResult(e.getMessage()));
        }
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
     * A password validation check
     *
     * @param password The password of the user.
     * @return true if it passes the criteria.
     */
    public static boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    /**
     * Validates all elements our our login / registration form.
     *
     * @param username   The user name of the user.
     * @param password   The password of the user.
     * @param isUser     true if the user selected the checkbox which corresponds to the user. Otherwise, false.
     * @param isOperator true if the user selected the checkbox which corresponds to the operator. Otherwise, false.
     */
    public void loginDataChanged(String username, String password, boolean isUser, boolean isOperator) {
        passwordState.setValue(password);
        emailState.setValue(username);
        if (!isEmailValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_email, null, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password, null));
        } else if (!AreAnyRolesSelected(isUser, isOperator) && !isUserInSigningInTab.getValue()) { // Checks only if the user is registering
            loginFormState.setValue(new LoginFormState(null, null, R.string.invalid_role_choice));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    /**
     * Getters & Setters
     */

    public MutableLiveData<Boolean> getTabState() {
        return isUserInSigningInTab;
    }

    public boolean isUserSigningIn() {
        return isUserInSigningInTab.getValue();
    }

    public void setUserSigningIn(boolean userSigningIn) {
        isUserInSigningInTab.setValue(userSigningIn);
    }

    public MutableLiveData<String> getEmailState() {
        return emailState;
    }

    public MutableLiveData<String> getPasswordState() {
        return passwordState;
    }

    MutableLiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }
}