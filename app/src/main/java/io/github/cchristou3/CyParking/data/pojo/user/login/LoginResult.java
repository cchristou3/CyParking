package io.github.cchristou3.CyParking.data.pojo.user.login;

import androidx.annotation.Nullable;

import io.github.cchristou3.CyParking.data.pojo.user.LoggedInUser;

/**
 * Purpose: <p>Authentication result : success (user details) or error message.</p>
 *
 * @author Charalambos Christou
 * @version 2.0 23/12/20
 */
public class LoginResult {
    @Nullable
    private LoggedInUser success;
    @Nullable
    private String error;

    public LoginResult(@Nullable String error) {
        this.error = error;
    }

    public LoginResult(@Nullable LoggedInUser success) {
        this.success = success;
    }

    @Nullable
    public LoggedInUser getSuccess() {
        return success;
    }

    @Nullable
    public String getError() {
        return error;
    }
}