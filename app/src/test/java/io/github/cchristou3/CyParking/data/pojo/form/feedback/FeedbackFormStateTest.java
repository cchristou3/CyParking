package io.github.cchristou3.CyParking.data.pojo.form.feedback;

import org.junit.Assert;
import org.junit.Test;

import io.github.cchristou3.CyParking.R;

/**
 * Unit tests for the {@link FeedbackFormState} class.
 */
public class FeedbackFormStateTest {

    @Test
    public void feedbackFormState_with_two_errors() {
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
    public void feedbackFormState_with_email_error() {
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
    public void feedbackFormState_with_feedback_message_error() {
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
    public void feedbackFormState_without_error() {
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