package io.github.cchristou3.CyParking.data.manager;

import android.content.Context;
import android.content.SharedPreferences;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Purpose:
 * This class acts as a Wrapper class for the {@link SharedPreferences} interface
 * to keep both its initialization and utility (methods) code wrapped up nicely.
 * <p>
 * Used for storing data locally.
 * It is primarily used for storing critical data about the
 * user (role(s), etc.) on the device.
 * <p>
 * TODO: Migrate to DataStore
 *
 * @author Charalambos Christou
 * @version 1.0 14/12/20
 */
public class SharedPreferencesManager {

    // Constants
    private static final String APP_PREFS = "CyParkingPreferences";
    // Members
    private final SharedPreferences mSharedPrefs;

    /**
     * Public Constructor. Initializes the
     * {@link #mSharedPrefs} given the {@link Context}.
     * By calling {@link Context#getApplicationContext()}, all
     * instantiations of this class get access to the same
     * {@link SharedPreferences} instance.
     *
     * @param context The context that will be used to derive the application context.
     */
    public SharedPreferencesManager(@NotNull Context context) {
        mSharedPrefs =
                context.getApplicationContext().getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
    }

    /**
     * Access the SharedPreferences ({@link #mSharedPrefs}) instance.
     *
     * @return A reference to the class' SharedPreferences instance.
     */
    public SharedPreferences getSharedPrefs() {
        return mSharedPrefs;
    }

    /**
     * Saves the given value based on the specified key.
     * It can later be retrieved using the same key.
     *
     * @param key   The key that the value is associated with
     * @param value The value that the key is associated with
     */
    public void setValue(String key, List<String> value) {
        SharedPreferences.Editor editor = mSharedPrefs.edit();   // to store a value, we need an editor
        editor.putStringSet(key, new HashSet<>(value)); // set its value based value given
        editor.apply(); // 'apply' to ensure that the value is 'remembered'
    }

    /**
     * Access a value (i.e. a Set<String>) using a given self-defined key.
     *
     * @param key The key that the value is associated with
     * @return The Set object that is associated with the given key, if there is one,
     * converted into an ArrayList. Otherwise, return null.
     */
    public ArrayList<String> getKey(String key) {
        if (mSharedPrefs.getStringSet(key, null) != null) {
            return new ArrayList<>(mSharedPrefs.getStringSet(key, null));
        }
        return null;
    }

    /**
     * Deletes all preferences of the application
     */
    public void destroySharedPreferencesManager() {
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        editor.clear();
        editor.apply();
    }
}