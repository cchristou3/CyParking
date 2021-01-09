package io.github.cchristou3.CyParking.data.pojo.form.feedback;

import androidx.annotation.Nullable;

import io.github.cchristou3.CyParking.data.pojo.form.login.EmailFormState;

/**
 * Purpose: <p>Data validation state of the feedback form.</p>
 *
 * @author Charalambos Christou
 * @version 2.0 11/12/20
 */
public class FeedbackFormState extends EmailFormState {

    @Nullable
    private final Integer mFeedbackMessageError;

    /**
     * Constructor used when there is an error in the feedbackFormState instance (E.g. no feedback message, etc.)
     *
     * @param feedbackMessageError The id of the error related to the feedback
     */
    public FeedbackFormState(@Nullable Integer emailError, @Nullable Integer feedbackMessageError) {
        super(emailError);
        this.mFeedbackMessageError = feedbackMessageError;
    }

    /**
     * Constructor used when the feedbackFormState instance is valid.
     *
     * @param isDataValid true of the data in the form is valid
     */
    public FeedbackFormState(boolean isDataValid) {
        super(isDataValid);
        this.mFeedbackMessageError = null;
    }

    /**
     * @return The id of the error related to the feedback
     */
    @Nullable
    public Integer getFeedbackMessageError() {
        return mFeedbackMessageError;
    }
}
