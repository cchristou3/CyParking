package io.github.cchristou3.CyParking.data.pojo.user.update;

import androidx.annotation.Nullable;

import io.github.cchristou3.CyParking.data.pojo.user.FormState;

/**
 * Purpose: <p>Data validation state of the update credentials form.</p>
 *
 * @author Charalambos Christou
 * @version 2.0 12/12/20
 */
public class UpdateFormState extends FormState {

    @Nullable
    private final Integer error;

    /**
     * Constructor used when there is an error in the updateState (E.g. pass too short, no username, etc.)
     *
     * @param error The id of the error related to the username|password|displayName.
     */
    public UpdateFormState(@Nullable Integer error) {
        super(false);
        this.error = error;
    }

    /**
     * Constructor used when the updateState is valid.
     *
     * @param isDataValid The validity of the data of the form
     */
    public UpdateFormState(boolean isDataValid) {
        super(isDataValid);
        this.error = null;
    }

    /**
     * @return The id of the error related to the username|password|displayName.
     */
    @Nullable
    public Integer getError() {
        return error;
    }
}
