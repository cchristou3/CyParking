package io.github.cchristou3.CyParking.data.pojo.form.login;

import androidx.annotation.Nullable;

import io.github.cchristou3.CyParking.data.model.user.LoggedInUser;

/**
 * Purpose: <p>Authentication result : success (user details) or error message.</p>
 *
 * @author Charalambos Christou
 * @version 2.0 23/12/20
 */
public class AuthResult {
    @Nullable
    private LoggedInUser mSuccess;
    @Nullable
    private String mError;

    public AuthResult(@Nullable String error) {
        this.mError = error;
    }

    public AuthResult(@Nullable LoggedInUser success) {
        this.mSuccess = success;
    }

    @Nullable
    public LoggedInUser getSuccess() {
        return mSuccess;
    }

    @Nullable
    public String getError() {
        return mError;
    }
}