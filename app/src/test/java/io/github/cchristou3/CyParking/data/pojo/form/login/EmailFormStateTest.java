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
    public void emailFormState_passing_true() {
        Assert.assertTrue(new EmailFormState(true).isDataValid());
    }

    @Test
    public void emailFormState_passing_false() {
        assertFalse(new EmailFormState(false).isDataValid());
    }

    @Test
    public void updateFormState_with_error() {
        // Given
        Integer error = R.string.lot_name_error;
        // When
        UpdateFormState state = new UpdateFormState(error);
        // Then
        Assert.assertTrue(state.getError().equals(error) && !state.isDataValid());
    }

    @Test
    public void updateFormState_without_error() {
        // Given
        boolean isValid = true;
        // When
        UpdateFormState state = new UpdateFormState(isValid);
        // Then
        Assert.assertTrue(state.getError() == null && state.isDataValid());
    }
}