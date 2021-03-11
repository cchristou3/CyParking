package io.github.cchristou3.CyParking.ui.widgets.alertDialog;

import android.view.View;

import org.jetbrains.annotations.NotNull;

/**
 * Purpose: handle the instantiation of a {@link AppAlertDialog} object
 * that allows for dual action decisions.
 *
 * @author Charalambos Christou.
 * @version 1.0 07/02/21
 */
public class DualActionBuilder extends Builder<DualActionBuilder, DualActionBuilder.AlertParams> {

    /**
     * Initialize the base class' AlertParams.
     */
    DualActionBuilder() {
        super(new DualActionBuilder.AlertParams());
    }

    /**
     * Set the text of the positive button with the given text res id
     * and the positive {@link android.view.View.OnClickListener}
     * with the given listener.
     *
     * @param positiveOnClickListener The on click handler of the positive button.
     * @param positiveButtonText      The res id text of the above button.
     * @return The builder itself to allow for for chaining of calls to set
     * methods.
     */
    public DualActionBuilder setPositiveButton(View.OnClickListener positiveOnClickListener, int positiveButtonText) {
        this.getAlertParams().positiveOnClickListener = positiveOnClickListener;
        this.getAlertParams().positiveButtonText = positiveButtonText;
        return this;
    }

    /**
     * Set the text of the negative button with the given text res id
     * and the negative {@link android.view.View.OnClickListener}
     * with the given listener.
     *
     * @param negativeOnClickListener The on click handler of the negative button.
     * @param negativeButtonText      The res id text of the above button.
     * @return The builder itself to allow for for chaining of calls to set
     * methods.
     */
    public DualActionBuilder setNegativeButton(View.OnClickListener negativeOnClickListener, int negativeButtonText) {
        this.getAlertParams().negativeOnClickListener = negativeOnClickListener;
        this.getAlertParams().negativeButtonText = negativeButtonText;
        return this;
    }


    /**
     * @see Builder#setTitle(int)
     */
    @Override
    public DualActionBuilder setTitle(int title) {
        this.getAlertParams().title = title;
        return this;
    }

    /**
     * @see Builder#setBody(int)
     */
    @Override
    public DualActionBuilder setBody(int body) {
        this.getAlertParams().body = body;
        return this;
    }

    /**
     * @see Builder#setTitle(String)
     */
    @Override
    public DualActionBuilder setTitle(String title) {
        this.getAlertParams().sTitle = title;
        return this;
    }

    /**
     * @see Builder#setBody(String)
     */
    @Override
    public DualActionBuilder setBody(String body) {
        this.getAlertParams().sBody = body;
        return this;
    }

    /**
     * Pass the Builder's arguments
     * to the given {@link AppAlertDialog} instance.
     *
     * @param dialog The dialog to access the arguments.
     */
    @Override
    public void bind(@NotNull AppAlertDialog<DualActionBuilder, AlertParams> dialog) {
        super.bind(dialog);

        // Attach the listeners to the buttons
        dialog.getBinding().buttonPositive.setOnClickListener(
                invokeHandlerThenDismiss(
                        dialog,
                        getAlertParams().positiveOnClickListener
                )
        );
        dialog.getBinding().buttonNegative.setOnClickListener(
                invokeHandlerThenDismiss(
                        dialog,
                        getAlertParams().positiveOnClickListener
                )
        );
        // Set the buttons' text
        dialog.setPositiveButtonText(getAlertParams().positiveButtonText);
        dialog.setNegativeButtonText(getAlertParams().negativeButtonText);
        // Prepare the Ui
        dialog.hideSingleActionView();
        dialog.showDualActionViews();
    }

    /**
     * Purpose: provide the builder class an extended object to collect
     * the {@link AppAlertDialog}'s arguments.
     */
    public static class AlertParams extends Builder.AlertParams {

        /*package-private*/ View.OnClickListener positiveOnClickListener;
        /*package-private*/ View.OnClickListener negativeOnClickListener;

        /*package-private*/ int positiveButtonText;
        /*package-private*/ int negativeButtonText;
    }
}
