package io.github.cchristou3.CyParking.view.data.pojo.update;

import androidx.annotation.Nullable;

public class UpdateFormState {

    @Nullable
    private final Integer error;

    private final boolean isDataValid;

    /**
     * Constructor used when there is an error in the updateState (E.g. pass too short, no username, etc.)
     *
     * @param error The id of the error related to the username|password|displayName.*
     */
    public UpdateFormState(@Nullable Integer error) {
        this.error = error;
        this.isDataValid = false;
    }

    /**
     * Constructor used when the updateState is valid.
     *
     * @param isDataValid true of the data in the form is valid
     */
    public UpdateFormState(boolean isDataValid) {
        this.error = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    public Integer getError() {
        return error;
    }

    public boolean isDataValid() {
        return isDataValid;
    }
}
