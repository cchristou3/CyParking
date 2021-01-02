package io.github.cchristou3.CyParking.data.pojo.form.update;

import androidx.annotation.Nullable;

import io.github.cchristou3.CyParking.data.pojo.form.FormState;

/**
 * Purpose: <p>Data validation state of the update credentials form.</p>
 *
 * @author Charalambos Christou
 * @version 2.0 12/12/20
 */
public class UpdateFormState extends FormState {

    @Nullable
    private final Integer mError;

    /**
     * Constructor used when there is an error in the updateState (E.g. pass too short, no username, etc.)
     *
     * @param error The id of the error related to the username|password|displayName.
     */
    public UpdateFormState(@Nullable Integer error) {
        super(false);
        this.mError = error;
    }

    /**
     * Constructor used when the updateState is valid.
     *
     * @param isDataValid The validity of the data of the form
     */
    public UpdateFormState(boolean isDataValid) {
        super(isDataValid);
        this.mError = null;
    }

    /**
     * @return The id of the error related to the username|password|displayName.
     */
    @Nullable
    public Integer getError() {
        return mError;
    }
}
