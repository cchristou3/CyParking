package io.github.cchristou3.CyParking.data.pojo.form.login;

import androidx.annotation.Nullable;

/**
 * Purpose: <p>Data validation state of the login form.</p>
 *
 * @author Charalambos Christou
 * @version 2.0 12/12/20
 */
public class LoginFormState extends EmailFormState {

    @Nullable
    private final Integer mPasswordError;
    @Nullable
    private final Integer mRoleError;

    /**
     * Constructor used when there is an error in the LoginState (E.g. pass too short, no username, etc.)
     *
     * @param emailError    The id of the error related to the username.
     * @param passwordError The id of the error related to the password.
     * @param roleError     The id of the error related to the role.
     */
    public LoginFormState(@Nullable Integer emailError, @Nullable Integer passwordError, @Nullable Integer roleError) {
        super(emailError);
        this.mPasswordError = passwordError;
        this.mRoleError = roleError;
    }

    /**
     * Constructor used when the LoginState is valid.
     *
     * @param isDataValid true of the data in the form is valid
     */
    public LoginFormState(boolean isDataValid) {
        super(isDataValid);
        this.mPasswordError = null;
        this.mRoleError = null;
    }

    /**
     * Getters
     */

    @Nullable
    public Integer getPasswordError() {
        return mPasswordError;
    }

    @Nullable
    public Integer getRoleError() {
        return mRoleError;
    }
}