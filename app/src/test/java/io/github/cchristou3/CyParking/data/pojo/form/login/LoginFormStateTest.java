package io.github.cchristou3.CyParking.data.pojo.form.login;

import org.junit.Assert;
import org.junit.Test;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.data.pojo.form.feedback.FeedbackFormState;

/**
 * Unit tests for the {@link LoginFormState} class.
 */
public class LoginFormStateTest {

    @Test
    public void loginFormState_with_three_errors() {
        // Given
        Integer error = R.string.lot_name_error;
        // When
        LoginFormState state = new LoginFormState(error, error, error);
        // Then
        Assert.assertTrue(state.getPasswordError().equals(error)
                && state.getEmailError().equals(error)
                && state.getRoleError().equals(error)
                && !state.isDataValid());
    }

    @Test
    public void feedbackFormState_with_email_error() {
        // Given
        Integer error = R.string.lot_name_error;
        // When
        LoginFormState state = new LoginFormState(error, null, null);
        // Then
        Assert.assertTrue(state.getPasswordError() == null
                && state.getEmailError().equals(error)
                && state.getRoleError() == null
                && !state.isDataValid());
    }

    @Test
    public void feedbackFormState_with_password_error() {
        // Given
        Integer error = R.string.lot_name_error;
        // When
        LoginFormState state = new LoginFormState(null, error, null);
        // Then
        Assert.assertTrue(state.getPasswordError().equals(error)
                && state.getEmailError() == null
                && state.getRoleError() == null
                && !state.isDataValid());
    }

    @Test
    public void feedbackFormState_with_role_error() {
        // Given
        Integer error = R.string.lot_name_error;
        // When
        LoginFormState state = new LoginFormState(null, null, error);
        // Then
        Assert.assertTrue(state.getPasswordError() == null
                && state.getEmailError() == null
                && state.getRoleError().equals(error)
                && !state.isDataValid());
    }

    @Test
    public void feedbackFormState_with_email_password_error() {
        // Given
        Integer error = R.string.lot_name_error;
        // When
        LoginFormState state = new LoginFormState(error, error, null);
        // Then
        Assert.assertTrue(state.getPasswordError().equals(error)
                && state.getEmailError().equals(error)
                && state.getRoleError() == null
                && !state.isDataValid());
    }

    @Test
    public void feedbackFormState_with_email_role_error() {
        // Given
        Integer error = R.string.lot_name_error;
        // When
        LoginFormState state = new LoginFormState(error, null, error);
        // Then
        Assert.assertTrue(state.getPasswordError() == null
                && state.getEmailError().equals(error)
                && state.getRoleError().equals(error)
                && !state.isDataValid());
    }

    @Test
    public void feedbackFormState_with_password_role_error() {
        // Given
        Integer error = R.string.lot_name_error;
        // When
        LoginFormState state = new LoginFormState(null, error, error);
        // Then
        Assert.assertTrue(state.getPasswordError().equals(error)
                && state.getEmailError() == null
                && state.getRoleError().equals(error)
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