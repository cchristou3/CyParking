package io.github.cchristou3.CyParking.ui.widgets.alertDialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.databinding.DialogFragmentAlertAppBinding;

/**
 * Purpose: The custom {@link AlertDialog} of the application.
 * It uses its own builder classes to initialize its attributes.
 * It supports builders for:
 * <p>Single actions (e.g. ok)</p>
 * and
 * <p>Dual actions (e.g. cancel/accept)</p>
 *
 * @param <T> Any instance of type {@link Builder}.
 * @param <S> Any instance of type {@link Builder.AlertParams}.
 * @author Charalambos Christou
 * @version 1.0 07/02/21
 */
public class AppAlertDialog<T extends Builder<T, S>, S extends Builder.AlertParams> extends DialogFragment {

    private final Builder<T, S> mBuilder;
    private DialogFragmentAlertAppBinding mBinding;

    /**
     * Initialize the dialog's builder with the given {@link Builder}
     * instance and make the dialog non-cancelable.
     *
     * @param mBuilder The builder of the dialog.
     */
    AppAlertDialog(Builder<T, S> mBuilder) {
        setCancelable(false);
        this.mBuilder = mBuilder;
    }

    /**
     * Create a new {@link SingleActionBuilder} instance.
     *
     * @return a {@link SingleActionBuilder} instance.
     * @see SingleActionBuilder
     */
    @NotNull
    @Contract(" -> new")
    public static SingleActionBuilder getSingleActionBuilder() {
        return new SingleActionBuilder();
    }

    /**
     * Create a new {@link DualActionBuilder} instance.
     *
     * @return a {@link DualActionBuilder} instance.
     * @see DualActionBuilder
     */
    @NotNull
    @Contract(" -> new")
    public static DualActionBuilder getDualActionBuilder() {
        return new DualActionBuilder();
    }

    /**
     * Called before {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * Initialize the dialog's Ui, both content and listeners.
     *
     * @param savedInstanceState The last saved instance state of the Fragment,
     *                           or null if this is a freshly created Fragment.
     * @return Return a new Dialog instance to be displayed by the Fragment.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Instantiate a dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        // Inflate the binding
        mBinding = DialogFragmentAlertAppBinding.inflate(getLayoutInflater());
        // Pass the binding's root to the builder
        builder.setView(
                mBinding.getRoot()
        );

        mBuilder.bind(this);

        // Return the builder's dialog
        return builder.create();
    }

    /**
     * Remove dialog.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    /**
     * Access the {@link #mBinding}.
     *
     * @return A reference to the {@link #mBinding}.
     */
    public DialogFragmentAlertAppBinding getBinding() {
        return mBinding;
    }

    /**
     * Set the text of the dialog's title to the given argument.
     *
     * @param title The dialog's title.
     */
    public void setTitle(int title) {
        mBinding.dialogFragmentAlertAppTitle.setText(title);
    }

    /**
     * Set the text of the dialog's body to the given argument.
     *
     * @param body dialog's body.
     */
    public void setBody(int body) {
        mBinding.dialogFragmentAlertAppBody.setText(body);
    }

    /**
     * Set the text of the dialog's neutral button
     * to the given argument.
     *
     * @param buttonText The text of the dialog's neutral button.
     */
    public void setNeutralButtonText(int buttonText) {
        mBinding.buttonOk.setText(buttonText);
    }

    /**
     * Set the text of the dialog's positive button
     * to the given argument.
     *
     * @param buttonText The text of the dialog's positive button.
     */
    public void setPositiveButtonText(int buttonText) {
        mBinding.buttonPositive.setText(buttonText);
    }

    /**
     * Set the text of the dialog's negative button
     * to the given argument.
     *
     * @param buttonText The text of the dialog's negative button.
     */
    public void setNegativeButtonText(int buttonText) {
        mBinding.buttonNegative.setText(buttonText);
    }

    /**
     * Hide the neutral button.
     */
    public void hideSingleActionView() {
        getBinding().buttonOk.setVisibility(View.GONE);
    }

    /**
     * Show the neutral button.
     */
    public void showSingleActionView() {
        getBinding().buttonOk.setVisibility(View.VISIBLE);
    }

    /**
     * Hide the positive and negative buttons.
     */
    public void hideDualActionViews() {
        getBinding().dualActionLayout.setVisibility(View.GONE);
    }

    /**
     * Show the positive and negative buttons.
     */
    public void showDualActionViews() {
        getBinding().dualActionLayout.setVisibility(View.VISIBLE);
    }
}
