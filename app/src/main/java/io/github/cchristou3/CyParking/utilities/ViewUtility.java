package io.github.cchristou3.CyParking.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Purpose: <p>Contain all helper / utility methods which the application needs
 * related to the View.</p>
 *
 * @author Charalambos Christou
 * @version 11.0 06/03/21
 */
public class ViewUtility {

    // No instances. Static utilities only.
    private ViewUtility() {
    }

    /**
     * Displays a Toast of the given message if it is not null.
     *
     * @param context The context to make use of.
     * @param message The message to display.
     */
    public static void showToast(Context context, @Nullable Integer message) {
        if (message != null)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Updates the specified Checkboxes' error status with the given error.
     * Used in {@link io.github.cchristou3.CyParking.ui.views.user.login.AuthenticatorHosteeFragment}.
     * The "[Data type]..." syntax is useful when more roles are going to be added.
     * Assuming the user did not pick any of the roles, it will iterate through all
     * the given checkboxes to set their error messages.
     *
     * @param context    The context of the caller.
     * @param checkBoxes An array of checkboxes,
     * @param error      The id of the error associated with the specified View object.
     */
    public static void updateErrorOf(Context context, @Nullable Integer error, CheckBox... checkBoxes) {
        if (error != null) {
            for (CheckBox checkbox :
                    checkBoxes) {
                checkbox.setError(context.getString(error));
            }
        } else {
            for (CheckBox checkbox :
                    checkBoxes) {
                checkbox.setError(null, null);
            }
        }
    }

    /**
     * Updates the specified TextView's error status with the given error.
     * The method is used for TextView instances and any of its subclasses.
     *
     * @param context         The context of the caller.
     * @param viewToBeUpdated A TextView (including all its subclasses) instance.
     * @param error           The id of the error associated with the specified View object.
     * @return True if the error was non-null. Otherwise, false.
     */
    public static boolean updateErrorOf(Context context, TextView viewToBeUpdated, @Nullable Integer error) {
        if (error != null) {
            viewToBeUpdated.setError(context.getString(error));
            return true;
        } else {
            viewToBeUpdated.setError(null, null);
            return false;
        }
    }

    /**
     * Updates the specified TextInputLayout's error status with the given error.
     *
     * @param context         The context of the caller.
     * @param viewToBeUpdated A TextInputLayout instance.
     * @param error           The id of the error associated with the specified View object.
     * @return True if the error was non-null. Otherwise, false.
     */
    public static boolean updateErrorOf(Context context, TextInputLayout viewToBeUpdated, @Nullable Integer error) {
        if (error != null) {
            viewToBeUpdated.setError(context.getString(error));
            return true;
        } else {
            viewToBeUpdated.setError(null);
            return false;
        }
    }

    /**
     * Hide the virtual keyboard.
     *
     * @param fragmentActivity Any activity.
     * @param view             The view to remove the keyboard from.
     */
    public static void hideKeyboard(@NotNull FragmentActivity fragmentActivity, @NotNull View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) fragmentActivity.getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getRootView().getApplicationWindowToken(), 0);
    }

    /**
     * Scrolls towards the specified view.
     * Only works, if the parent is an instance of
     * {@link android.widget.ScrollView}.
     *
     * @param view The view to scroll to.
     */
    public static void scrollTo(@NotNull View view) {
        view.getParent().requestChildFocus(view, view);
    }

    /**
     * The parent view no longer receives touch events from the specified
     * child view. If a scrollable area (e.g. ListView) is inside a
     * ScrollView then there is an issue while scrolling
     * the scrollable area's inner contents.
     * So, when touching the scrollable area, any touch events are blocked from its parent view.
     *
     * @param view The view that should scroll smoothly without interference.
     */
    @SuppressLint("ClickableViewAccessibility")
    public static void disableParentScrollingInterferenceOf(@NotNull View view) {
        view.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                v.getParent().requestDisallowInterceptTouchEvent(false);
            }
            return false;
        });
    }

    /**
     * Changes the visibility of the given view with the specified visibility.
     * If the view already is in that visibility, then it is ignored.
     *
     * @param visibility The state of the visibility (E.g. View.Gone / View.VISIBLE / View.INVISIBLE).
     * @return true, if the given View's visibility got updated. Otherwise false.
     */
    public static boolean updateViewVisibilityTo(@NotNull View view, int visibility) {
        if (view.getVisibility() != visibility) {
            view.setVisibility(visibility);
            return true;
        }
        return false;
    }

    /**
     * Show or hide the given {@link ContentLoadingProgressBar} instance
     * based on the given flag.
     *
     * @param shouldShowLoadingBar Indicates whether to display or hide the loading bar.
     */
    public static void updateVisibilityOfLoadingBarTo(ContentLoadingProgressBar loadingBar, boolean shouldShowLoadingBar) {
        if (shouldShowLoadingBar) {
            loadingBar.show();
        } else {
            loadingBar.hide();
        }
    }

    /**
     * Show or hide the given {@link CircularProgressIndicator} instance
     * based on the given flag.
     *
     * @param shouldShowLoadingBar Indicates whether to display or hide the loading bar.
     */
    public static void updateVisibilityOfLoadingBarTo(CircularProgressIndicator loadingBar, boolean shouldShowLoadingBar) {
        if (shouldShowLoadingBar) {
            loadingBar.show();
        } else {
            loadingBar.hide();
        }
    }
}
