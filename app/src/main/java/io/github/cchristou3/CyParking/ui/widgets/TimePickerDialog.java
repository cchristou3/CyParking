package io.github.cchristou3.CyParking.ui.widgets;

import android.app.AlertDialog;
import android.content.Context;

import java.util.Calendar;

/**
 * Purpose: Encapsulate the logic related to setting the
 * time picker's initial hour and minute values.
 *
 * @author Charalambos Christou
 * @version 1.0 18/01/21
 */
public class TimePickerDialog extends android.app.TimePickerDialog {

    /**
     * Creates a new time picker dialog with the specified theme.
     * <p>
     * The theme is overlaid on top of the theme of the parent {@code context}.
     * If {@code themeResId} is 0, the dialog will be inflated using the theme
     * specified by the
     * {@link android.R.attr#timePickerDialogTheme android:timePickerDialogTheme}
     * attribute on the parent {@code context}'s theme.
     *
     * @param context  the parent context
     * @param listener the listener to call when the time is set
     */
    public TimePickerDialog(Context context, OnTimeSetListener listener) {
        super(
                context,
                // TODO: replace with android.R.style#Theme_Material_Dialog_Alert
                //  when increase target SDK version to 21
                AlertDialog.THEME_HOLO_DARK,
                listener,
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE),
                true);
    }
}
