package io.github.cchristou3.CyParking.data.pojo.form.login;

import androidx.annotation.Nullable;

import io.github.cchristou3.CyParking.data.pojo.form.FormState;

/**
 * Purpose: <p>Data validation state of the email.</p>
 *
 * @author Charalambos Christou
 * @version 1.0 12/12/20
 */
public class EmailFormState extends FormState {
    @Nullable
    private final Integer mEmailError;

    /**
     * Constructor used when there is an error in the emailFormState
     * (E.g. invalid email, empty email, etc.)
     *
     * @param emailError The id of the error related to the email.
     */
    public EmailFormState(Integer emailError) {
        super(false);
        this.mEmailError = emailError;
    }

    /**
     * Constructor used when the {@link EmailFormState} object is valid.
     *
     * @param isDataValid The validity of the data of the form
     */
    public EmailFormState(boolean isDataValid) {
        super(isDataValid);
        this.mEmailError = null;
    }

    /**
     * @return The id of the error related to the email
     */
    @Nullable
    public Integer getEmailError() {
        return mEmailError;
    }
}
