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
    public void loginFormState_withoutErrors_allErrorNullValidIsTrue() {
        // Given
        boolean isValid = true;
        // When
        LoginFormState state = new LoginFormState(isValid);
        // Then
        Assert.assertTrue(state.getPasswordError() == null
                && state.getEmailError() == null
                && state.getRoleError() == null
                && state.isDataValid());
    }

    @Test
    public void loginFormState_threeErrors_allErrorsNotNullInvalid() {
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
    public void feedbackFormState_emailError_emailErrorNotNullInvalid() {
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
    public void feedbackFormState_passwordError_passwordErrorNotNullInvalid() {
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
    public void feedbackFormState_roleError_roleErrorNotNullInvalid() {
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
    public void feedbackFormState_emailPasswordErrors_thoseErrorsNotNullInvalid() {
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
    public void feedbackFormState_emailRoleErrors_thoseErrorsNotNullInvalid() {
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
    public void feedbackFormState_passwordRoleErrors_thoseErrorsNotNullInvalid() {
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
    public void feedbackFormState_withoutError_allErrorsNullValidIsTrue() {
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