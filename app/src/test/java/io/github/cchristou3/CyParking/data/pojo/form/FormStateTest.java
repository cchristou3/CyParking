package io.github.cchristou3.CyParking.data.pojo.form;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

/**
 * Unit tests for the {@link FormState} class.
 */
public class FormStateTest {

    @Test
    public void formState_passing_true() {
        Assert.assertTrue(new FormState(true).isDataValid());
    }

    @Test
    public void formState_passing_false() {
        assertFalse(new FormState(false).isDataValid());
    }
}