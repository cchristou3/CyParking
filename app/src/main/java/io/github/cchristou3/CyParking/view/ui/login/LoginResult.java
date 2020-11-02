package io.github.cchristou3.CyParking.view.ui.login;

import androidx.annotation.Nullable;

/**
 * purpose: Authentication result : success (user details) or error message.
 *
 * @author Charalambos Christou
 * @version 1.0 1/11/20
 */
public class LoginResult {
    @Nullable
    private LoggedInUserView success;
    @Nullable
    private String error;

    public LoginResult(@Nullable String error) {
        this.error = error;
    }

    public LoginResult(@Nullable LoggedInUserView success) {
        this.success = success;
    }

    @Nullable
    LoggedInUserView getSuccess() {
        return success;
    }

    @Nullable
    String getError() {
        return error;
    }
}