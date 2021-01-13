package io.github.cchristou3.CyParking.data.manager;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Unit tests for the {@link SharedPreferencesManager}.
 */
@RunWith(AndroidJUnit4.class)
public class SharedPreferencesManagerTest {

    private final Context context = ApplicationProvider.getApplicationContext();

    public SharedPreferencesManager manager = new SharedPreferencesManager(context);

    ///////////////////////////////////////////////////////////////////////////
    // setValue - START
    ///////////////////////////////////////////////////////////////////////////
    @Test
    public void setGetValue_setValidPairKeyValue_returnsTheSame() {
        // Given
        ArrayList<String> input = new ArrayList<>(Arrays.asList("1", "2"));
        String key = "key";
        // When
        manager.setValue(key, input);
        // Then
        Assert.assertEquals(input, manager.getValue(key));
    }

    @Test
    public void setGetValue_emptyKey_returnsTheSame() {
        // Given
        ArrayList<String> input = new ArrayList<>(Arrays.asList("1", "2"));
        String key = "";
        // When
        manager.setValue(key, input);
        // Then
        Assert.assertEquals(input, manager.getValue(key));
    }

    @Test
    public void setGetValue_nullKey_returnsTheSame() {
        // Given
        ArrayList<String> input = new ArrayList<>(Arrays.asList("1", "2"));
        String key = null;
        // When
        manager.setValue(key, input);
        // Then
        Assert.assertEquals(input, manager.getValue(key));
    }

    @Test(expected = NullPointerException.class) // Then
    public void setValue_nullValue_throwsException() {
        // Given
        ArrayList<String> input = null;
        String key = null;
        // When
        manager.setValue(key, input);
    }

    @Test
    public void setGetValue_valueEmptyList_returnsEmptyList() {
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
    public void destroySharedPreferencesManager_removesAllPairs() {
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