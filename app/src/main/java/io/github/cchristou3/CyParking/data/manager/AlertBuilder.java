package io.github.cchristou3.CyParking.data.manager;

import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.data.interfaces.Navigable;
import io.github.cchristou3.CyParking.ui.widgets.alertDialog.AppAlertDialog;

/**
 * Purpose: Contain all logic related to creating an {@link AppAlertDialog}.
 *
 * @author Charalambos Christou
 * @version 3.0 07/02/21
 */
public class AlertBuilder {

    /**
     * Display an {@link AppAlertDialog} in the given context with the specified title,
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
    public static void showDualActionAlert(
            @NotNull FragmentManager fragmentManager, int title, int message, int positiveButtonText, int negativeButtonText,
            @Nullable View.OnClickListener positiveActionHandler, @Nullable View.OnClickListener negativeActionHandler
    ) {
        AppAlertDialog.getDualActionBuilder()
                .setTitle(title)
                .setBody(message)
                .setPositiveButton(positiveActionHandler, positiveButtonText)
                .setNegativeButton(negativeActionHandler, negativeButtonText)
                .show(fragmentManager);
    }

    /**
     * Display an {@link AppAlertDialog} in the given context with the specified title,
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
    public static void showSingleActionAlert(@NotNull FragmentManager fragmentManager, int title, int message,
                                             @Nullable View.OnClickListener neutralActionHandler) {

        AppAlertDialog.getSingleActionBuilder()
                .setTitle(title)
                .setBody(message)
                .setNeutralButton(
                        neutralActionHandler, // listener
                        android.R.string.ok // text
                )
                .show(fragmentManager);
    }

    /**
     * Display an {@link AppAlertDialog} in the given context.
     * The dialog prompts the user, that the current screen
     * requires authentication. Thus, the user has the options
     * to either log in, or to return to previous screen.
     *
     * @param context   The context of the fragment/activity.
     * @param activity  The hosting activity.
     * @param navigable An implementation of the {@link Navigable} interface.
     * @param msg       The body of the alert dialog.
     */
    public static void promptUserToLogIn(
            @NotNull FragmentManager fragmentManager, final FragmentActivity activity,
            final Navigable navigable, int msg
    ) {
        showDualActionAlert(
                fragmentManager,
                R.string.logout_of_auth_required_screen,
                msg,
                R.string.log_in,
                R.string.go_back,
                (v) -> navigable.toAuthenticator(), // Go to log in screen
                (v) -> // Navigate one screen back
                        navigable.goBack(activity)
        );
    }
}
