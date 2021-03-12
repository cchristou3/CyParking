package io.github.cchristou3.CyParking.ui.views.user.account.update;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.apiClient.model.user.LoggedInUser;
import io.github.cchristou3.CyParking.data.interfaces.Navigable;
import io.github.cchristou3.CyParking.databinding.DialogAccountUpdateBinding;
import io.github.cchristou3.CyParking.ui.helper.AlertBuilder;
import io.github.cchristou3.CyParking.ui.views.home.HomeFragment;
import io.github.cchristou3.CyParking.ui.views.host.GlobalStateViewModel;
import io.github.cchristou3.CyParking.ui.views.parking.slots.viewBooking.ViewBookingsFragment;
import io.github.cchristou3.CyParking.ui.views.user.account.AccountFragment;
import io.github.cchristou3.CyParking.ui.views.user.feedback.FeedbackFragment;
import io.github.cchristou3.CyParking.ui.views.user.login.AuthenticatorFragment;
import io.github.cchristou3.CyParking.utils.ViewUtility;

import static io.github.cchristou3.CyParking.ui.views.host.MainHostActivity.TAG;

/**
 * Purpose: Allows the users to update one of their attributes.
 *
 * @author Charalambos Christou
 * @version 6.0 02/02/21
 */
public class UpdateAccountDialog extends DialogFragment implements View.OnClickListener, TextWatcher,
        Navigable {

    // Fragment's constants
    public static final short UPDATE_DISPLAY_NAME = 144;
    public static final short UPDATE_EMAIL = 400;
    public static final short UPDATE_PASSWORD = 899;
    private static final String DIALOG_ACTION_KEY = "DIALOG_TYPE_KEY";

    // Fragment's variables
    private UpdateViewModel mUpdateViewModel;
    private GlobalStateViewModel mGlobalStateViewModel;
    private DialogAccountUpdateBinding mDialogAccountUpdateBinding;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param action The kind of action to be performed in dialog.
     * @return A new instance of fragment DescriptionDialog.
     */
    @NotNull
    public static UpdateAccountDialog newInstance(short action) {
        UpdateAccountDialog dialog = new UpdateAccountDialog(); // Instantiate an UpdateAccountDialog object
        Bundle args = new Bundle(); // Create a bundle store key information about the dialog
        args.putShort(DIALOG_ACTION_KEY, action);
        dialog.setArguments(args); // Pass in the bundle
        // Set the dialog's style
        dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.Widget_CyParking_Dialog);
        // TODO: 01/02/2021 Update Themes
        return dialog;
    }

    /**
     * Inflates our fragment's view.
     *
     * @param inflater           Inflater which will inflate our view
     * @param container          The parent view
     * @param savedInstanceState A bundle which contains info about previously stored data
     * @return The view of the fragment
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mDialogAccountUpdateBinding = DialogAccountUpdateBinding.inflate(inflater);
        return mDialogAccountUpdateBinding.getRoot();
    }

    /**
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() == null) return;
        final short action = getArguments().getShort(DIALOG_ACTION_KEY);
        initializeViewModel(action);
        initializeUi(action);
        addObserverToViewModel();
    }

    /**
     * Remove listeners.
     * Remove dialog.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getBinding().dialogAccountUpdateMbtnDismiss.setOnClickListener(null);
        getBinding().dialogAccountUpdateMbtnUpdate.setOnClickListener(null);
        getBinding().dialogAccountUpdateEtInput.removeTextChangedListener(this);
        mDialogAccountUpdateBinding = null;
    }


    /**
     * Access the {@link #mDialogAccountUpdateBinding}.
     *
     * @return A reference to {@link #mDialogAccountUpdateBinding}.
     */
    private DialogAccountUpdateBinding getBinding() {
        return mDialogAccountUpdateBinding;
    }

    /**
     * Initializes the {@link #mUpdateViewModel} and sets
     * its initial values.
     *
     * @param action The action to be performed by this instance of the dialog.
     */
    private void initializeViewModel(short action) {
        // Initialize the UpdateViewModel of the fragment and set its initial values.
        mUpdateViewModel = new ViewModelProvider(this,
                new UpdateViewModelFactory()).get(UpdateViewModel.class);
        mUpdateViewModel.setDialogType(action);

        // Instantiate the GlobalStateViewModel to access the user's state
        mGlobalStateViewModel = new ViewModelProvider(requireActivity())
                .get(GlobalStateViewModel.class);
    }

    /**
     * Returns the appropriate String id based on the given action.
     *
     * @param action        The action to be performed by the current instance of the fragment.
     * @param resIdName     The id of the string about the user's display name.
     * @param resIdEmail    The id of the string about the user's email.
     * @param resIdPassword The id of the string about the user's password.
     * @return The appropriate String based on the given action.
     */
    @NonNull
    private String getDialogAttribute(short action, int resIdName, int resIdEmail, int resIdPassword) {
        switch (action) {
            case UPDATE_DISPLAY_NAME:
                return getString(resIdName);
            case UPDATE_EMAIL:
                return getString(resIdEmail);
            case UPDATE_PASSWORD:
                return getString(resIdPassword);
            default:
                throw new IllegalArgumentException("Not a valid action.");
        }
    }

    /**
     * Attach observer to the form's state and appropriately update its display,
     * when changed.
     */
    private void addObserverToViewModel() {
        // Observe the form state //
        mUpdateViewModel.getUpdateFormState().observe(this, updateFormState -> {
            getBinding().dialogAccountUpdateMbtnUpdate.setEnabled(updateFormState.isDataValid());
            if (updateFormState.getError() != null) {
                // Show error hint
                getBinding().dialogAccountUpdateTilInput.setError(getString(updateFormState.getError()));
            } else {
                // Remove error hint if it displays
                if (getBinding().dialogAccountUpdateTilInput.getError() != null) {
                    getBinding().dialogAccountUpdateTilInput.setError(null);
                }
            }
        });

        // Observe the UpdateViewModel's LoadingBarState state //
        mUpdateViewModel.getLoadingBarState().observe(getViewLifecycleOwner(), this::updateLoadingBarVisibility);
    }

    /**
     * Initialize the Ui's contents and its listeners.
     *
     * @param action The action to be performed by the current instance of the fragment.
     */
    private void initializeUi(short action) {
        // Set the dialog's title
        requireDialog().setTitle(getDialogAttribute(action, R.string.updating_name,
                R.string.updating_email,
                R.string.updating_password));

        // Set the text of the textview above the edittext
        getBinding().dialogAccountUpdateMtvFieldTitle
                .setText(getDialogAttribute(action, R.string.update_name_text,
                        R.string.update_email_text,
                        R.string.update_password_text));

        // Attach listeners to both buttons
        attachButtonListeners();

        // Initialize the UI's content
        final short dialogAction = mUpdateViewModel.getDialogType();
        setInputTypeTo(dialogAction);
        // Display the saved value
        getBinding().dialogAccountUpdateEtInput.setText(mUpdateViewModel.getActionFieldInput());

        final String hint = getDialogAttribute(dialogAction,
                R.string.prompt_name,
                R.string.prompt_email,
                R.string.prompt_password);

        getBinding().dialogAccountUpdateTilInput.setHintEnabled(true);
        getBinding().dialogAccountUpdateTilInput.setHint(hint);

        getBinding().dialogAccountUpdateEtInput.addTextChangedListener(this);
        // Pressing the "enter" on the keyboard will automatically trigger the login method
        getBinding().dialogAccountUpdateEtInput.setOnEditorActionListener((v, actionId, event) -> {
            Log.d(TAG, "initializeUi: " + actionId);
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (mUpdateViewModel.isFormValid())
                    getBinding().dialogAccountUpdateMbtnUpdate.performClick();
            }
            return false;
        });
    }

    /**
     * Hook up all buttons with appropriate on click listeners.
     */
    private void attachButtonListeners() {
        getBinding().dialogAccountUpdateMbtnDismiss.setOnClickListener(v -> dismiss());
        getBinding().dialogAccountUpdateMbtnUpdate.setOnClickListener(this);
        getBinding().dialogAccountUpdateMbtnUpdate.setEnabled(false); // disable it
    }

    /**
     * Shows or hides the loading bar based on the given flag.
     *
     * @param shouldShowLoadingBar Indicates whether to display or hide the loading bar.
     */
    public void updateLoadingBarVisibility(boolean shouldShowLoadingBar) {
        ViewUtility.updateVisibilityOfLoadingBarTo(getBinding().dialogAccountUpdateClpbLoadingBar, shouldShowLoadingBar);
    }

    /**
     * Sets the input type of the specified TextInputEditText
     * based on the specified numeric value (dialog type).
     *
     * @param action The type of the dialog
     */
    private void setInputTypeTo(@NotNull Short action) {
        switch (action) {
            case UpdateAccountDialog.UPDATE_PASSWORD:
                // Hide the password
                getBinding().dialogAccountUpdateEtInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
                // Display the 'show/hide password' icon
                getBinding().dialogAccountUpdateTilInput.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
                break;
            case UPDATE_EMAIL:
                getBinding().dialogAccountUpdateEtInput.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                break;
            case UPDATE_DISPLAY_NAME: // nothing
                break;
        }
    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        // Access the field's info
        final String updatedField = getBinding().dialogAccountUpdateEtInput.getText().toString();
        if (mUpdateViewModel.getDialogType() == UPDATE_DISPLAY_NAME
                && mGlobalStateViewModel.getUser() != null
                && updatedField.equals(mGlobalStateViewModel.getUser().getDisplayName())) {
            Toast.makeText(requireContext(), "Must not be the same as the previous one.", Toast.LENGTH_SHORT).show();
            return;
        }

        mUpdateViewModel.showLoadingBar();

        // Update it and save a reference to its returning Task object
        Task<Void> updateTask = mUpdateViewModel.updateAccountField(
                mGlobalStateViewModel.getUser(),
                updatedField);

        if (updateTask != null) {
            updateTask.addOnCompleteListener(task -> {
                mUpdateViewModel.hideLoadingBar();

                if (task.getException() != null) {
                    Log.d(TAG, "onClick: " + task.getException());
                    handleError(task.getException());
                    return;
                }

                if (task.isSuccessful()) { // if successful
                    // Compose a message
                    final String actionItem = getBinding().dialogAccountUpdateMtvFieldTitle.getText().toString();
                    final String toastMsg = actionItem + " got updated.";
                    try {
                        // And display it to the user
                        Toast.makeText(getParentFragment().requireContext(), toastMsg, Toast.LENGTH_SHORT).show();
                        updateUserState(updatedField); // Update the user's state attribute
                    } catch (NullPointerException ignored) {
                    } finally {
                        // Hide the dialog
                        dismiss();
                    }
                }
            });
        } else {
            mUpdateViewModel.hideLoadingBar();
        }
    }

    /**
     * Updates the current {@link LoggedInUser}
     * instance.
     *
     * @param updatedField The field that got updated.
     * @throws NullPointerException if the user is not logged in.
     */
    private void updateUserState(String updatedField)
            throws NullPointerException {
        switch (mUpdateViewModel.getDialogType()) {
            case UpdateAccountDialog.UPDATE_DISPLAY_NAME:
                // Update the user state's display name
                mGlobalStateViewModel.updateAuthState(
                        mGlobalStateViewModel.getUser().setDisplayName(updatedField)
                );
                break;
            case UpdateAccountDialog.UPDATE_EMAIL:
                // Update the user state's email
                mGlobalStateViewModel.updateAuthState(
                        mGlobalStateViewModel.getUser().setEmail(updatedField)
                );
                break;
            case UpdateAccountDialog.UPDATE_PASSWORD: /* Do nothing */
                break;
        }
    }

    /**
     * Provides feedback to the user based on the specified exception.
     *
     * @param exception The exception whilst updating credentials.
     */
    private void handleError(@NotNull Exception exception) {
        Log.d(TAG, "displayErrorMessage: " + exception.getClass());
        if (exception instanceof FirebaseAuthRecentLoginRequiredException) {
            AlertBuilder.promptUserToLogIn(getChildFragmentManager(),
                    requireActivity(),
                    this, // Access the parent's Navigable interface implementation
                    R.string.login_required_exception);
            dismiss();
        }
        // Exceptions associated to both email and password
        else if (exception instanceof FirebaseAuthInvalidUserException) {
            //  if the current user's account has been disabled, deleted, or its credentials are no longer valid
            AlertBuilder.promptUserToLogIn(getChildFragmentManager(),
                    requireActivity(),
                    this, // Access the parent's Navigable interface implementation
                    R.string.invalid_user_exception_msg);
            dismiss();
        } else if (exception instanceof FirebaseAuthWeakPasswordException) {
            //  if the password is not strong enough
            Toast.makeText(requireContext(), ((FirebaseAuthWeakPasswordException) exception).getReason(), Toast.LENGTH_SHORT).show();
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            // if the email address is malformed
            Toast.makeText(requireContext(), "Failed to authenticate: "
                    + exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();

        } else if (exception instanceof FirebaseAuthUserCollisionException) {
            // if there already exists an account with the given email address
            Toast.makeText(requireContext(), "Email: " + ((FirebaseAuthUserCollisionException) exception).getEmail()
                    + " already belongs to another user", Toast.LENGTH_SHORT).show();
        } else if (exception instanceof FirebaseTooManyRequestsException) {
            // Many requests from this device can cause unusual activity
            final Snackbar tooManyRequestsSnackbar =
                    Snackbar.make(getParentFragment().requireView(), exception.getLocalizedMessage(), Snackbar.LENGTH_INDEFINITE);
            tooManyRequestsSnackbar.setAction(R.string.dismiss, v -> tooManyRequestsSnackbar.dismiss()).show();
            dismiss();
        } else {
            Toast.makeText(requireContext(), exception.getLocalizedMessage(),
                    Toast.LENGTH_LONG).show();
            // TODO: Add logger
        }

        // TODO: Test all scenarios, and polish fragment.
    }

    /**
     * Updates the ViewModel's LiveData's value with the
     * specified one. Gets triggered, whenever the EditText's text
     * gets changed.
     *
     * @param newText The new text.
     */
    @Override
    public void afterTextChanged(@NotNull Editable newText) {
        mUpdateViewModel.formDataChanged(newText.toString());
    }

    /**
     * Unused TextWatcher methods.
     */
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* ignore */ }

    public void onTextChanged(CharSequence s, int start, int before, int count) { /* ignore */ }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link AuthenticatorFragment}.
     */
    @Override
    public void toAuthenticator() {
        if (((Navigable) getTargetFragment()) != null)
            ((Navigable) getTargetFragment()).toAuthenticator();
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link ViewBookingsFragment}.
     */
    @Override
    public void toBookings() {
        if (((Navigable) getTargetFragment()) != null)
            ((Navigable) getTargetFragment()).toBookings();
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link AccountFragment}.
     */
    @Override
    public void toAccount() {
        if (getTargetFragment() != null)
            ((Navigable) getTargetFragment()).toAccount();
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link FeedbackFragment}.
     */
    @Override
    public void toFeedback() {
        if (getTargetFragment() != null)
            ((Navigable) getTargetFragment()).toFeedback();
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link HomeFragment}.
     */
    @Override
    public void toHome() {
        if (getTargetFragment() != null)
            ((Navigable) getTargetFragment()).toHome();
    }
}
