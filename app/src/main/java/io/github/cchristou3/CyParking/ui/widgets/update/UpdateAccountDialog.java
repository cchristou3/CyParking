package io.github.cchristou3.CyParking.ui.widgets.update;

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
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.data.interfaces.Navigable;
import io.github.cchristou3.CyParking.data.manager.AlertBuilder;
import io.github.cchristou3.CyParking.databinding.DialogAccountUpdateBinding;
import io.github.cchristou3.CyParking.ui.home.HomeFragment;
import io.github.cchristou3.CyParking.ui.host.AuthStateViewModel;
import io.github.cchristou3.CyParking.ui.parking.slots.viewBooking.ViewBookingsFragment;
import io.github.cchristou3.CyParking.ui.user.AccountFragment;
import io.github.cchristou3.CyParking.ui.user.feedback.FeedbackFragment;
import io.github.cchristou3.CyParking.ui.user.login.AuthenticatorFragment;

import static io.github.cchristou3.CyParking.ui.host.MainHostActivity.TAG;
import static io.github.cchristou3.CyParking.ui.widgets.DescriptionDialog.getStyleConfiguration;

/**
 * Purpose: Allows the users to update one of their attributes.
 * TODO: Update user data on the database node as well if updating email/display name
 *
 * @author Charalambos Christou
 * @version 3.0 29/12/20
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
    private DialogAccountUpdateBinding mDialogAccountUpdateBinding;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param nightModeFlags Configurations for the system's theme.
     * @param action         The kind of action to be performed in dialog.
     * @return A new instance of fragment DescriptionDialog.
     */
    @NotNull
    public static UpdateAccountDialog newInstance(int nightModeFlags, short action) {
        UpdateAccountDialog dialog = new UpdateAccountDialog(); // Instantiate an UpdateAccountDialog object
        Bundle args = new Bundle(); // Create a bundle store key information about the dialog
        args.putShort(DIALOG_ACTION_KEY, action);
        dialog.setArguments(args); // Pass in the bundle
        // Set the dialog's style
        dialog.setStyle(DialogFragment.STYLE_NORMAL, getStyleConfiguration(nightModeFlags));
        return dialog;
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
        String actionFieldTitle, dialogTitle;
        // Set value of the dialog's title, and the TextView that is above the input
        // to its corresponding text based on the dialog's action type.
        switch (action) {
            case UPDATE_DISPLAY_NAME:
                actionFieldTitle = getString(R.string.prompt_name);
                dialogTitle = getString(R.string.updating_name);
                break;
            case UPDATE_EMAIL:
                actionFieldTitle = getString(R.string.prompt_email);
                dialogTitle = getString(R.string.updating_email);
                break;
            case UPDATE_PASSWORD:
                actionFieldTitle = getString(R.string.prompt_password);
                dialogTitle = getString(R.string.updating_password);
                break;
            default:
                throw new IllegalArgumentException("Not a valid action.");
        }
        // Initialize the UpdateViewModel of the fragment and set its initial values.
        mUpdateViewModel = new ViewModelProvider(this,
                new UpdateViewModelFactory()).get(UpdateViewModel.class);
        mUpdateViewModel.getDialogTitle().setValue(dialogTitle);
        mUpdateViewModel.getActionFieldTitle().setValue(actionFieldTitle);
        mUpdateViewModel.setDialogType(action);
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
        initializeUi();
        attachObserverToViewModel();
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
     * Attach observer to the form's state and appropriately update its display,
     * when changed.
     */
    private void attachObserverToViewModel() {
        // Add observer to out form
        mUpdateViewModel.getUpdateFormState().observe(this, updateFormState -> {
            getBinding().dialogAccountUpdateMbtnUpdate.setEnabled(updateFormState.isDataValid());
            if (updateFormState.getError() != null) {
                // Show error hint
                getBinding().dialogAccountUpdateEtInput.setError(getString(updateFormState.getError()));
            } else {
                // Remove error hint if it displays
                if (getBinding().dialogAccountUpdateEtInput.getError() != null) {
                    getBinding().dialogAccountUpdateEtInput.setError(null, null);
                }
            }
        });
    }

    /**
     * Initialize the Ui's contents and its listeners.
     */
    private void initializeUi() {
        // Initialize the dialog's title and body
        requireDialog().setTitle(mUpdateViewModel.getDialogTitle().getValue());

        getBinding().dialogAccountUpdateMtvFieldTitle
                .setText(mUpdateViewModel.getActionFieldTitle().getValue());

        // Attach listeners to both buttons
        getBinding().dialogAccountUpdateMbtnDismiss.setOnClickListener(v -> dismiss());
        getBinding().dialogAccountUpdateMbtnUpdate.setOnClickListener(this);
        getBinding().dialogAccountUpdateMbtnUpdate.setEnabled(false); // disable it

        // Initialize the UI's content
        final short dialogAction = mUpdateViewModel.getDialogType();
        setInputTypeTo(dialogAction, getBinding().dialogAccountUpdateEtInput);
        // Display the saved value
        getBinding().dialogAccountUpdateEtInput.setText(mUpdateViewModel.getActionFieldInput().getValue());

        final String hint = getString(
                ((dialogAction == UPDATE_DISPLAY_NAME) ? R.string.hint_name
                        : (dialogAction == UPDATE_EMAIL) ? R.string.hint_email
                        : R.string.hint_password)
        );
        getBinding().dialogAccountUpdateEtInput.setHint(hint);
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
     * Sets the input type of the specified TextInputEditText
     * based on the specified numeric value (dialog type).
     *
     * @param action           The type of the dialog
     * @param actionFieldInput A UI element of type TextInputEditText
     */
    private void setInputTypeTo(@NotNull Short action, EditText actionFieldInput) {
        switch (action) {
            case UpdateAccountDialog.UPDATE_PASSWORD:
                actionFieldInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
                break;
            case UPDATE_EMAIL:
                actionFieldInput.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
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
        changeLoadingBarVisibilityTo(View.VISIBLE);

        // Instantiate the AuthStateViewModel to access the user's state
        AuthStateViewModel authStateViewModel = new ViewModelProvider(requireActivity())
                .get(AuthStateViewModel.class);

        // Access the field's info
        final String updatedField = getBinding().dialogAccountUpdateEtInput.getText().toString();
        // Update it and save a reference to its returning Task object
        Task<Void> updateTask = mUpdateViewModel.updateAccountField(
                authStateViewModel.getUser(),
                updatedField);

        if (updateTask != null) {
            updateTask.addOnCompleteListener(task -> {
                changeLoadingBarVisibilityTo(View.GONE);

                if (task.getException() != null) {
                    Log.d(TAG, "onClick: " + task.getException());
                    handleError(task.getException());
                    return;
                }

                if (task.isSuccessful()) { // if successful
                    // Compose a message
                    final String actionItem = getBinding().dialogAccountUpdateMtvFieldTitle.getText().toString();
                    final String toastMsg = actionItem + " got updated.";
                    // And display it to the user
                    Toast.makeText(getParentFragment().requireContext(), toastMsg, Toast.LENGTH_SHORT).show();
                    updateUserState(authStateViewModel, updatedField); // Update the user's state attribute
                    // Hide the dialog
                    dismiss();
                }
            });
        } else {
            getBinding().dialogAccountUpdateClpbLoadingBar.setVisibility(View.GONE);
        }
    }

    private void updateUserState(final AuthStateViewModel authStateViewModel, String updatedField) {
        switch (mUpdateViewModel.getDialogType()) {
            case UpdateAccountDialog.UPDATE_DISPLAY_NAME:
                // Update the user's state's display name
                authStateViewModel.updateAuthState(
                        authStateViewModel.getUser().setDisplayName(updatedField)
                );
                break;
            case UpdateAccountDialog.UPDATE_EMAIL:
                // Update the user's state's email
                authStateViewModel.updateAuthState(
                        authStateViewModel.getUser().setEmail(updatedField)
                );
                break;
            case UpdateAccountDialog.UPDATE_PASSWORD: /* Do nothing */
                break;
        }
    }

    private void changeLoadingBarVisibilityTo(int visibility) {
        if (getBinding().dialogAccountUpdateClpbLoadingBar.getVisibility() != visibility) {
            getBinding().dialogAccountUpdateClpbLoadingBar.setVisibility(visibility);
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
            AlertBuilder.promptUserToLogIn(requireContext(),
                    requireActivity(),
                    this, // Access the parent's Navigable interface implementation
                    R.string.login_required_exception);
            dismiss();
        }
        // Exceptions associated to both email and password
        else if (exception instanceof FirebaseAuthInvalidUserException) {
            //  if the current user's account has been disabled, deleted, or its credentials are no longer valid
            AlertBuilder.promptUserToLogIn(requireContext(),
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
