package io.github.cchristou3.CyParking.data.pojo.user.login;

import androidx.annotation.Nullable;

/**
 * Purpose: <p>Authentication result : success (user details) or error message.</p>
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
    public LoggedInUserView getSuccess() {
        return success;
    }

    @Nullable
    public String getError() {
        return error;
    }
}