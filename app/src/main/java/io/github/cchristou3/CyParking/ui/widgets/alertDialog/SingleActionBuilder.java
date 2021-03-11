package io.github.cchristou3.CyParking.ui.widgets.alertDialog;

import android.view.View;

import org.jetbrains.annotations.NotNull;

/**
 * Purpose: handle the instantiation of a {@link AppAlertDialog} object
 * that allows for only a single action.
 *
 * @author Charalambos Christou.
 * @version 1.0 07/02/21
 */
public class SingleActionBuilder extends Builder<SingleActionBuilder, SingleActionBuilder.AlertParams> {

    /**
     * Initialize the base class' AlertParams.
     */
    SingleActionBuilder() {
        super(new SingleActionBuilder.AlertParams());
    }

    /**
     * Set the text of the neutral button with the given text res id
     * and the neutral {@link android.view.View.OnClickListener}
     * with the given listener.
     *
     * @param neutralOnClickListener The on click handler of the neutral button.
     * @param neutralButtonText      The res id text of the above button.
     * @return The builder itself to allow for for chaining of calls to set
     * methods.
     */
    public SingleActionBuilder setNeutralButton(View.OnClickListener neutralOnClickListener, int neutralButtonText) {
        this.getAlertParams().neutralOnClickListener = neutralOnClickListener;
        this.getAlertParams().neutralButtonText = neutralButtonText;
        return this;
    }

    /**
     * @see Builder#setTitle(int)
     */
    @Override
    public SingleActionBuilder setTitle(int title) {
        this.getAlertParams().title = title;
        return this;
    }

    /**
     * @see Builder#setBody(int)
     */
    @Override
    public SingleActionBuilder setBody(int body) {
        this.getAlertParams().body = body;
        return this;
    }

    /**
     * @see Builder#setTitle(intString
     */
    @Override
    public SingleActionBuilder setTitle(String title) {
        this.getAlertParams().sTitle = title;
        return this;
    }

    /**
     * @see Builder#setBody(String)
     */
    @Override
    public SingleActionBuilder setBody(String body) {
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
    public void bind(@NotNull AppAlertDialog<SingleActionBuilder, AlertParams> dialog) {
        super.bind(dialog); // set title and body via the super call

        // Attach the on click listener
        dialog.getBinding().buttonOk.setOnClickListener(
                invokeHandlerThenDismiss(
                        dialog,
                        getAlertParams().neutralOnClickListener
                )
        );
        // Set the button's text
        dialog.setNeutralButtonText(this.getAlertParams().neutralButtonText);
        // Prepare the Ui
        dialog.hideDualActionViews();
        dialog.showSingleActionView();
    }

    /**
     * Purpose: provide the builder class an extended object to collect
     * the {@link AppAlertDialog}'s arguments.
     */
    public static class AlertParams extends Builder.AlertParams {
        /*package-private*/ View.OnClickListener neutralOnClickListener;

        /*package-private*/ int neutralButtonText;
    }
}