package io.github.cchristou3.CyParking.data.pojo.form.feedback;

import org.junit.Assert;
import org.junit.Test;

import io.github.cchristou3.CyParking.R;

/**
 * Unit tests for the {@link FeedbackFormState} class.
 */
public class FeedbackFormStateTest {

    @Test
    public void feedbackFormState_twoErrors__thoseErrorsNotNullInvalid() {
        // Given
        Integer error = R.string.lot_name_error;
        // When
        FeedbackFormState state = new FeedbackFormState(error, error);
        // Then
        Assert.assertTrue(state.getFeedbackMessageError().equals(error)
                && state.getEmailError().equals(error)
                && !state.isDataValid());
    }

    @Test
    public void feedbackFormState_emailError_emailErrorNotNullInvalid() {
        // Given
        Integer error = R.string.lot_name_error;
        // When
        FeedbackFormState state = new FeedbackFormState(error, null);
        // Then
        Assert.assertTrue(state.getFeedbackMessageError() == null
                && state.getEmailError().equals(error)
                && !state.isDataValid());
    }

    @Test
    public void feedbackFormState_feedbackMessageError_feedbackMessageErrorNotNullInvalid() {
        // Given
        Integer error = R.string.lot_name_error;
        // When
        FeedbackFormState state = new FeedbackFormState(null, error);
        // Then
        Assert.assertTrue(state.getFeedbackMessageError().equals(error)
                && state.getEmailError() == null
                && !state.isDataValid());
    }

    @Test
    public void feedbackFormState_withoutErrors_allErrorsNullValidIsTrue() {
        // Given
        boolean isValid = true;
        // When
        FeedbackFormState state = new FeedbackFormState(isValid);
        // Then
        Assert.assertTrue(state.getFeedbackMessageError() == null
                && state.getEmailError() == null
                && state.isDataValid());
    }
}