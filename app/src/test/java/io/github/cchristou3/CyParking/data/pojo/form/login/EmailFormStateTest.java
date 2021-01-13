package io.github.cchristou3.CyParking.data.pojo.form.login;

import org.junit.Assert;
import org.junit.Test;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.data.pojo.form.update.UpdateFormState;

import static org.junit.Assert.assertFalse;

/**
 * Unit tests for the {@link EmailFormState} class.
 */
public class EmailFormStateTest {

    @Test
    public void emailFormState_passingTrue_returnsTrue() {
        Assert.assertTrue(new EmailFormState(true).isDataValid());
    }

    @Test
    public void emailFormState_passingFalse_returnsFalse() {
        assertFalse(new EmailFormState(false).isDataValid());
    }

    @Test
    public void updateFormState_error_errorNotNullInvalid() {
        // Given
        Integer error = R.string.lot_name_error;
        // When
        UpdateFormState state = new UpdateFormState(error);
        // Then
        Assert.assertTrue(state.getError().equals(error) && !state.isDataValid());
    }

    @Test
    public void updateFormState_withoutError_errorIsNullValidIsTrue() {
        // Given
        boolean isValid = true;
        // When
        UpdateFormState state = new UpdateFormState(isValid);
        // Then
        Assert.assertTrue(state.getError() == null && state.isDataValid());
    }
}