package io.github.cchristou3.CyParking.data.manager;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Unit tests for the {@link SharedPreferencesManager}.
 */
public class SharedPreferencesManagerTest {

    private final Context context = ApplicationProvider.getApplicationContext();

    public SharedPreferencesManager manager = new SharedPreferencesManager(context);

    ///////////////////////////////////////////////////////////////////////////
    // setValue - START
    ///////////////////////////////////////////////////////////////////////////
    @Test
    public void set_get_Value_valid_key() {
        // Given
        ArrayList<String> input = new ArrayList<>(Arrays.asList("1", "2"));
        String key = "key";
        // When
        manager.setValue(key, input);
        // Then
        Assert.assertEquals(input, manager.getValue(key));
    }

    @Test
    public void set_get_Value_empty_key() {
        // Given
        ArrayList<String> input = new ArrayList<>(Arrays.asList("1", "2"));
        String key = "";
        // When
        manager.setValue(key, input);
        // Then
        Assert.assertEquals(input, manager.getValue(key));
    }

    @Test
    public void set_get_Value_null_key() {
        // Given
        ArrayList<String> input = new ArrayList<>(Arrays.asList("1", "2"));
        String key = null;
        // When
        manager.setValue(key, input);
        // Then
        Assert.assertEquals(input, manager.getValue(key));
    }

    @Test(expected = NullPointerException.class) // Then
    public void set_get_Value_null_value_expect_nullpointerexception() {
        // Given
        ArrayList<String> input = null;// new ArrayList<>(Arrays.asList("1", "2"));
        String key = null;
        // When
        manager.setValue(key, input);
    }

    @Test
    public void set_get_Value_value_empty_list() {
        // Given
        ArrayList<String> input = new ArrayList<>();
        String key = null;
        // When
        manager.setValue(key, input);
        // Then
        Assert.assertEquals(input, manager.getValue(key));
    }

    ///////////////////////////////////////////////////////////////////////////
    // setValue - END
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // destroySharedPreferencesManager - START
    ///////////////////////////////////////////////////////////////////////////
    @Test
    public void destroySharedPreferencesManager() {
        // Given
        ArrayList<String> input = new ArrayList<>();
        String key = null;
        // When
        manager.setValue(key, input);
        manager.destroySharedPreferencesManager();
        // Then
        Assert.assertNotEquals(input, manager.getValue(key));
    }
    ///////////////////////////////////////////////////////////////////////////
    // destroySharedPreferencesManager - END
    ///////////////////////////////////////////////////////////////////////////

}