package io.github.cchristou3.CyParking.ui.widgets.alertDialog;

import android.view.View;

import androidx.fragment.app.FragmentManager;

import org.jetbrains.annotations.NotNull;

/**
 * Purpose: handle the instantiation of {@link AppAlertDialog}.
 * Follows the builder pattern to allow chaining calls
 * of set methods.
 *
 * <strong>Makes use of the Covariant type to allow the return
 * type to be the subtype of the type of the overridden method.
 * see: {@link #setTitle(int)} and {@link #setBody(int)}.</strong>
 *
 * @param <T> Any instance of type {@link Builder}.
 * @param <S> Any instance of type {@link Builder.AlertParams}.
 * @author Charalambos Christou
 * @version 1.0 07/02/21
 */
public abstract class Builder<T extends Builder<T, S>, S extends Builder.AlertParams> {

    private final S mAlertParams;

    /**
     * Create a builder for an {@link AppAlertDialog}.
     *
     * @param mAlertParams The parameters (attributes) of the {@link AppAlertDialog} instance.
     */
    Builder(S mAlertParams) {
        this.mAlertParams = mAlertParams;
    }

    /**
     * Access the {@link #mAlertParams}'s of the builder.
     *
     * @return A reference to the builder's parameters.
     */
    public S getAlertParams() {
        return mAlertParams;
    }

    /**
     * Create an {@link AppAlertDialog} with the arguments supplied to this
     * builder.
     * <p>
     * Calling this method does not display the dialog. If no additional
     * processing is needed, {@link #show(FragmentManager)} may be called instead to both
     * create and display the dialog.
     */
    public AppAlertDialog<T, S> create() {
        return new AppAlertDialog<>(this);
    }

    /**
     * Create an {@link AppAlertDialog} with the arguments supplied to this
     * builder and immediately displays the dialog.
     * <p>
     * Calling this method is functionally identical to:
     * <pre>
     *     AppAlertDialog dialog = builder.create();
     *     dialog.show();
     * </pre>
     */
    public void show(FragmentManager fragmentManager) {
        create().show(fragmentManager, AppAlertDialog.class.getCanonicalName());
    }

    /**
     * Set the the value of {@link AlertParams#title}
     * with the given argument.
     *
     * @param title The title of the dialog.
     * @return The builder itself to allow for for chaining of calls to set
     * methods.
     */
    public abstract T setTitle(int title);

    /**
     * Sets# the the value of {@link AlertParams#body}
     * with the given argument.
     *
     * @param body The body message of the dialog.
     * @return The builder itself to allow for for chaining of calls to set
     * methods.
     */
    public abstract T setBody(int body);

    /**
     * Set the the value of {@link AlertParams#title}
     * with the given argument.
     *
     * @param title The title of the dialog.
     * @return The builder itself to allow for for chaining of calls to set
     * methods.
     */
    public abstract T setTitle(String title);

    /**
     * Sets# the the value of {@link AlertParams#body}
     * with the given argument.
     *
     * @param body The body message of the dialog.
     * @return The builder itself to allow for for chaining of calls to set
     * methods.
     */
    public abstract T setBody(String body);

    /**
     * Pass the Builder's title and body arguments
     * to the given {@link AppAlertDialog} instance.
     *
     * @param dialog The dialog to access the arguments.
     * @see AppAlertDialog#setTitle(int)
     * @see AppAlertDialog#setBody(int)
     */
    public void bind(@NotNull AppAlertDialog<T, S> dialog) {
        dialog.setTitle(this.mAlertParams.title);
        dialog.setBody(this.mAlertParams.body);
    }

    /**
     * Wraps the given {@link android.view.View.OnClickListener}
     * into another one. After the given listener's invocation,
     * the dialog fragment is dismissed.
     *
     * @param dialog   The dialog to be dismissed.
     * @param listener The listener to be wrapped.
     * @return A instance of {@link android.view.View.OnClickListener}
     * containing both the given listener's logic and logic for dismissing
     * the given dialog.
     */
    public View.OnClickListener invokeHandlerThenDismiss(
            AppAlertDialog<T, S> dialog, View.OnClickListener listener
    ) {
        return v -> {
            listener.onClick(v);
            dialog.dismiss();
        };
    }

    /**
     * Purpose: provide the builder class an object to collect
     * the {@link AppAlertDialog}'s arguments.
     */
    public static class AlertParams {
        /*package-private*/ int title;
        /*package-private*/ String sTitle;
        /*package-private*/ int body;
        /*package-private*/ String sBody;
    }
}
