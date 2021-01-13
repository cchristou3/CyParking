package io.github.cchristou3.CyParking.data.pojo.form;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

/**
 * Unit tests for the {@link FormState} class.
 */
public class FormStateTest {

    @Test
    public void formState_passingTrue_returnsTrue() {
        Assert.assertTrue(new FormState(true).isDataValid());
    }

    @Test
    public void formState_passingFalse_returnsFalse() {
        assertFalse(new FormState(false).isDataValid());
    }
}