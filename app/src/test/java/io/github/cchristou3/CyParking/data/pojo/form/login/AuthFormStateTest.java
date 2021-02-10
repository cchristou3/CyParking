package io.github.cchristou3.CyParking.data.pojo.form.login;

import org.junit.Assert;
import org.junit.Test;

import io.github.cchristou3.CyParking.R;

/**
 * Unit tests for the {@link AuthFormState} class.
 */
public class AuthFormStateTest {

    @Test
    public void loginFormState_withoutErrors_allErrorNullValidIsTrue() {
        // Given
        boolean isValid = true;
        // When
        AuthFormState state = new AuthFormState(isValid);
        // Then
        Assert.assertTrue(state.getPasswordError() == null
                && state.getEmailError() == null
                && state.isDataValid());
    }

    @Test
    public void loginFormState_threeErrors_allErrorsNotNullInvalid() {
        // Given
        Integer error = R.string.lot_name_error;
        // When
        AuthFormState state = new AuthFormState(error, error, error);
        // Then
        Assert.assertTrue(state.getPasswordError().equals(error)
                && state.getNameError().equals(error)
                && state.getEmailError().equals(error)
                && !state.isDataValid());
    }

    @Test
    public void loginFormState_emailError_emailErrorNotNullInvalid() {
        // Given
        Integer error = R.string.lot_name_error;
        // When
        AuthFormState state = new AuthFormState(error, null, null);
        // Then
        Assert.assertTrue(state.getPasswordError() == null
                && state.getNameError() == null
                && state.getEmailError().equals(error)
                && !state.isDataValid());
    }

    @Test
    public void loginFormState_nameError_emailErrorNotNullInvalid() {
        // Given
        Integer error = R.string.lot_name_error;
        // When
        AuthFormState state = new AuthFormState(null, error, null);
        // Then
        Assert.assertTrue(state.getPasswordError() == null
                && state.getNameError().equals(error)
                && state.getEmailError() == null
                && !state.isDataValid());
    }

    @Test
    public void loginFormState_passwordError_passwordErrorNotNullInvalid() {
        // Given
        Integer error = R.string.lot_name_error;
        // When
        AuthFormState state = new AuthFormState(null, null, error);
        // Then
        Assert.assertTrue(state.getPasswordError().equals(error)
                && state.getNameError() == null
                && state.getEmailError() == null
                && !state.isDataValid());
    }

    @Test
    public void loginFormState_emailPasswordErrors_thoseErrorsNotNullInvalid() {
        // Given
        Integer error = R.string.lot_name_error;
        // When
        AuthFormState state = new AuthFormState(error, null, error);
        // Then
        Assert.assertTrue(state.getPasswordError().equals(error)
                && state.getNameError() == null
                && state.getEmailError().equals(error)
                && !state.isDataValid());
    }

    @Test
    public void loginFormState_passwordNameErrors_thoseErrorsNotNullInvalid() {
        // Given
        Integer error = R.string.lot_name_error;
        // When
        AuthFormState state = new AuthFormState(null, error, error);
        // Then
        Assert.assertTrue(state.getPasswordError().equals(error)
                && state.getNameError().equals(error)
                && state.getEmailError() == null
                && !state.isDataValid());
    }

    @Test
    public void loginFormState_emailNameErrors_thoseErrorsNotNullInvalid() {
        // Given
        Integer error = R.string.lot_name_error;
        // When
        AuthFormState state = new AuthFormState(error, error, null);
        // Then
        Assert.assertTrue(state.getPasswordError() == null
                && state.getNameError().equals(error)
                && state.getEmailError().equals(error)
                && !state.isDataValid());
    }

    @Test
    public void loginFormState_emailNamePasswordErrors_thoseErrorsNotNullInvalid() {
        // Given
        Integer error = R.string.lot_name_error;
        // When
        AuthFormState state = new AuthFormState(error, error, error);
        // Then
        Assert.assertTrue(
                state.getEmailError().equals(error)
                        && state.getNameError().equals(error)
                        && state.getPasswordError().equals(error)
                        && !state.isDataValid());
    }

    @Test
    public void loginFormState_withoutError_allErrorsNullValidIsTrue() {
        // Given
        boolean isValid = true;
        // When
        AuthFormState state = new AuthFormState(isValid);
        // Then
        Assert.assertTrue(state.getPasswordError() == null
                && state.getNameError() == null
                && state.getEmailError() == null
                && state.isDataValid());
    }
}