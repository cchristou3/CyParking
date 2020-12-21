package io.github.cchristou3.CyParking.data.pojo.user;

import androidx.annotation.Nullable;

import io.github.cchristou3.CyParking.data.pojo.user.login.EmailFormState;

/**
 * Purpose: <p>Data validation state of the feedback form.</p>
 *
 * @author Charalambos Christou
 * @version 2.0 11/12/20
 */
public class FeedbackFormState extends EmailFormState {

    @Nullable
    private final Integer feedbackMessageError;

    /**
     * Constructor used when there is an error in the feedbackFormState instance (E.g. no feedback message, etc.)
     *
     * @param feedbackMessageError The id of the error related to the feedback
     */
    public FeedbackFormState(@Nullable Integer emailError, @Nullable Integer feedbackMessageError) {
        super(emailError, false);
        this.feedbackMessageError = feedbackMessageError;
    }

    /**
     * Constructor used when the feedbackFormState instance is valid.
     *
     * @param isDataValid true of the data in the form is valid
     */
    public FeedbackFormState(@Nullable Integer emailError, boolean isDataValid) {
        super(emailError, isDataValid);
        this.feedbackMessageError = null;
    }

    /**
     * @return The id of the error related to the feedback
     */
    @Nullable
    public Integer getFeedbackMessageError() {
        return feedbackMessageError;
    }
}
