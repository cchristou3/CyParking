@file:JvmName("ViewUtility")
@file:JvmMultifileClass

package io.github.cchristou3.CyParking.utils

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.FragmentActivity
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputLayout

/**
 * Purpose:
 *
 * Contain all helper / utility methods which the application needs
 * related to the View.
 *
 * @author Charalambos Christou
 * @version 12.0 26/03/21
 */

/**
 * Displays a Toast of the given message.
 *
 * @param context The context to make use of.
 * @param message The message to display.
 */
fun showToast(context: Context?, message: Int) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

/**
 * Updates the specified Checkboxes' error status with the given error.
 * Assuming the user did not pick any of the roles, it will iterate through all
 * the given checkboxes to set their error messages.
 *
 * @param context    The context of the caller.
 * @param checkBoxes An array of checkboxes,
 * @param error      The id of the error associated with the specified View object.
 */
fun updateErrorOf(context: Context, error: Int?, vararg checkBoxes: CheckBox) {
    if (error != null) {
        for (checkbox in checkBoxes) {
            checkbox.error = context.getString(error)
        }
    } else {
        for (checkbox in checkBoxes) {
            checkbox.setError(null, null)
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
fun updateErrorOf(context: Context, viewToBeUpdated: TextView, error: Int?): Boolean {
    return if (error != null) {
        viewToBeUpdated.error = context.getString(error)
        true
    } else {
        viewToBeUpdated.setError(null, null)
        false
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
fun updateErrorOf(context: Context, viewToBeUpdated: TextInputLayout, error: Int?): Boolean {
    return if (error != null) {
        viewToBeUpdated.error = context.getString(error)
        true
    } else {
        viewToBeUpdated.error = null
        false
    }
}

/**
 * Hide the virtual keyboard.
 *
 * @param fragmentActivity Any activity.
 * @param view             The view to remove the keyboard from.
 */
fun hideKeyboard(fragmentActivity: FragmentActivity, view: View) {
    val inputMethodManager = fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.rootView.applicationWindowToken, 0)
}

/**
 * Scrolls towards the specified view.
 * Only works, if the parent is an instance of
 * [android.widget.ScrollView].
 *
 * @param view The view to scroll to.
 */
fun scrollTo(view: View) {
    view.parent.requestChildFocus(view, view)
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
fun disableParentScrollingInterferenceOf(view: View) {
    view.setOnTouchListener { v: View, event: MotionEvent ->
        v.parent.requestDisallowInterceptTouchEvent(true)
        if (event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_UP) {
            v.parent.requestDisallowInterceptTouchEvent(false)
        }
        false
    }
}

/**
 * Changes the visibility of the given view with the specified visibility.
 * If the view already is in that visibility, then it is ignored.
 *
 * @param visibility The state of the visibility (E.g. View.Gone / View.VISIBLE / View.INVISIBLE).
 * @return true, if the given View's visibility got updated. Otherwise false.
 */
fun updateViewVisibilityTo(view: View, visibility: Int): Boolean {
    if (view.visibility != visibility) {
        view.visibility = visibility
        return true
    }
    return false
}

/**
 * Show or hide the given [ContentLoadingProgressBar] instance
 * based on the given flag.
 *
 * @param shouldShowLoadingBar Indicates whether to display or hide the loading bar.
 */
fun updateLoadingBarVisibilityTo(loadingBar: ContentLoadingProgressBar, shouldShowLoadingBar: Boolean) {
    if (shouldShowLoadingBar) {
        loadingBar.show()
    } else {
        loadingBar.hide()
    }
}

/**
 * Show or hide the given [CircularProgressIndicator] instance
 * based on the given flag.
 *
 * @param shouldShowLoadingBar Indicates whether to display or hide the loading bar.
 */
fun updateLoadingBarVisibilityTo(loadingBar: CircularProgressIndicator, shouldShowLoadingBar: Boolean) {
    if (shouldShowLoadingBar) {
        loadingBar.show()
    } else {
        loadingBar.hide()
    }
}

/**
 * Access the string object of the given editText, if there is one. Otherwise
 * return an empty string.
 *
 * @param editText the edittext to extract its string from
 */
fun getStringOrEmpty(editText: EditText): String {
    return if (editText.text != null) {
        editText.text.toString()
    } else ""
}