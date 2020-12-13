package io.github.cchristou3.CyParking.view.data.pojo.user.login;

import androidx.annotation.Nullable;

import io.github.cchristou3.CyParking.view.data.pojo.user.FormState;

/**
 * Purpose: <p>Data validation state of the email.</p>
 *
 * @author Charalambos Christou
 * @version 1.0 12/12/20
 */
public class EmailFormState extends FormState {
    @Nullable
    private final Integer emailError;

    /**
     * Initialize the EmailFormState instance's
     * emailError and isDataValid members with the
     * given arguments.
     *
     * @param emailError  The id of the error related to the email
     * @param isDataValid The validity of the data of the form
     */
    public EmailFormState(@Nullable Integer emailError, boolean isDataValid) {
        super(isDataValid);
        this.emailError = emailError;
    }

    /**
     * @return The id of the error related to the email
     */
    @Nullable
    public Integer getEmailError() {
        return emailError;
    }
}
