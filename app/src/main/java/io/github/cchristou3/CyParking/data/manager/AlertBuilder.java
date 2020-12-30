package io.github.cchristou3.CyParking.data.manager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.data.interfaces.Navigable;

/**
 * Purpose: Contain all logic related to creating an AlertDialog.
 *
 * @author Charalambos Christou
 * @version 1.0 28/12/20
 */
public class AlertBuilder {

    /**
     * Display an {@link AlertDialog} in the given context with the specified title,
     * message, positiveButtonText, negativeButtonText, positiveActionHandler,
     * negativeActionHandler. This kind of alert supports only positive and negative
     * responses, no neutral ones.
     *
     * @param context               The context of the fragment/activity.
     * @param title                 The title of the dialog.
     * @param message               The message body of the dialog.
     * @param positiveButtonText    The text of the positive button.
     * @param negativeButtonText    The text of the negative button.
     * @param positiveActionHandler The handler for the positive response.
     * @param negativeActionHandler The handler for the negative response.
     */
    public static void showAlert(Context context, int title, int message, int positiveButtonText, int negativeButtonText,
                                 @Nullable DialogInterface.OnClickListener positiveActionHandler,
                                 @Nullable DialogInterface.OnClickListener negativeActionHandler) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setTitle(title)
                .setPositiveButton(positiveButtonText,
                        positiveActionHandler) // To be provided by the programmer
                .setNegativeButton(negativeButtonText,
                        negativeActionHandler) // To be provided by the programmer
                .create()
                .show();
    }

    /**
     * Display an {@link AlertDialog} in the given context with the specified title,
     * message, neutralButtonText, neutralActionHandler.
     * This kind of alert supports only neutral
     * responses, no positive and negative ones.
     *
     * @param context              The context of the fragment/activity.
     * @param title                The title of the dialog.
     * @param message              The message body of the dialog.
     * @param neutralButtonText    The text of the neutral button.
     * @param neutralActionHandler The handler for the negative response.
     */
    public static void showAlert(Context context, int title, int message, int neutralButtonText,
                                 @Nullable DialogInterface.OnClickListener neutralActionHandler) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setTitle(title)
                .setNeutralButton(neutralButtonText,
                        neutralActionHandler) // To be provided by the programmer
                .create()
                .show();
    }

    /**
     * Display an {@link AlertDialog} in the given context.
     * The dialog prompts the user, that the current screen
     * requires authentication. Thus, the user has the options
     * to either log in, or to return to previous screen.
     *
     * @param context   The context of the fragment/activity.
     * @param activity  The hosting activity.
     * @param navigable An implementation of the {@link Navigable} interface.
     * @param msg       The body of the alert dialog.
     */
    public static void promptUserToLogIn(final Context context, final FragmentActivity activity,
                                         final Navigable navigable, int msg) {
        AlertBuilder.showAlert(context,
                R.string.logout_of_auth_required_screen,
                msg,
                R.string.log_in,
                R.string.go_back,
                (dialog, which) -> navigable.toAuthenticator(), // Go to log in screen
                (dialog, which) -> // Navigate one screen back
                        navigable.goBack(activity
                                .findViewById(R.id.fragment_main_host_nv_nav_view))
        );
    }
}
