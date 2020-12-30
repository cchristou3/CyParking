package io.github.cchristou3.CyParking.ui.widgets.update;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.data.manager.AlertBuilder;
import io.github.cchristou3.CyParking.data.pojo.user.LoggedInUser;
import io.github.cchristou3.CyParking.databinding.DialogAccountUpdateBinding;
import io.github.cchristou3.CyParking.ui.host.AuthStateViewModel;
import io.github.cchristou3.CyParking.ui.user.AccountFragment;

import static io.github.cchristou3.CyParking.ui.widgets.DescriptionDialog.getStyleConfiguration;

/**
 * Purpose: Allows the users to update one of their attributes.
 * TODO: Update user data on the database node as well if updating email/display name
 *
 * @author Charalambos Christou
 * @version 3.0 29/12/20
 */
public class UpdateAccountDialog extends DialogFragment implements View.OnClickListener, TextWatcher {

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
        // Access the current state of the user
        LoggedInUser currentUser = new ViewModelProvider(requireActivity())
                .get(AuthStateViewModel.class).getUser();

        // Pass it in to the UpdateViewModelFactory, which itself
        // will further pass it to the AccountRepository
        mUpdateViewModel = new ViewModelProvider(this,
                new UpdateViewModelFactory(currentUser)).get(UpdateViewModel.class);
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
        getBinding().dialogAccountUpdateMetInput.removeTextChangedListener(this);
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
                getBinding().dialogAccountUpdateMetInput.setError(getString(updateFormState.getError()));
            } else {
                // Remove error hint if it displays
                if (getBinding().dialogAccountUpdateMetInput.getError() != null) {
                    getBinding().dialogAccountUpdateMetInput.setError(null, null);
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
        setInputTypeTo(dialogAction, getBinding().dialogAccountUpdateMetInput);
        // Display the saved value
        getBinding().dialogAccountUpdateMetInput.setText(mUpdateViewModel.getActionFieldInput().getValue());

        final String hint = getString(
                ((dialogAction == UPDATE_DISPLAY_NAME) ? R.string.hint_name
                        : (dialogAction == UPDATE_EMAIL) ? R.string.hint_email
                        : R.string.hint_password)
        );
        getBinding().dialogAccountUpdateMetInput.setHint(hint);
        getBinding().dialogAccountUpdateMetInput.addTextChangedListener(this);
    }

    /**
     * Sets the input type of the specified TextInputEditText
     * based on the specified numeric value (dialog type).
     *
     * @param action           The type of the dialog
     * @param actionFieldInput A UI element of type TextInputEditText
     */
    private void setInputTypeTo(@NotNull Short action, TextInputEditText actionFieldInput) {
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
        // Access the field's info
        final String updatedField = getBinding().dialogAccountUpdateMetInput.getText().toString();
        // Update it and save a reference to its returning Task object
        Task<Void> updateTask = mUpdateViewModel.updateField(updatedField);

        if (updateTask != null) {
            updateTask.addOnCompleteListener(task -> {
                // https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseUser#updateEmail(java.lang.String)
                // Exceptions for emails
                if (task.getException() != null) {
                    displayErrorMessage(task.getException());
                    return;
                }

                if (task.isSuccessful()) { // if successful
                    // Compose a message
                    final String actionItem = getBinding().dialogAccountUpdateMtvFieldTitle.getText().toString();
                    final String toastMsg = actionItem + " got updated.";
                    // And display it to the user
                    Toast.makeText(getParentFragment().requireContext(), toastMsg, Toast.LENGTH_SHORT).show();
                    // Hide the dialog
                    dismiss();
                }
            });
        }
    }

    /**
     * Provides feedback to the user based on the specified exception.
     *
     * @param exception The exception whilst updating credentials.
     */
    private void displayErrorMessage(Exception exception) {
        String errorMessage = null;
        // Exceptions associated to both email and password
        if (exception instanceof FirebaseAuthInvalidUserException) {
            //  if the current user's account has been disabled, deleted, or its credentials are no longer valid
            AlertBuilder.promptUserToLogIn(requireContext(),
                    requireActivity(),
                    ((AccountFragment) getParentFragment()), // Access the parent's Navigable interface implementation
                    R.string.invalid_user_exception_msg);
            return;
        }
        // Exceptions associated to email
        else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            // if the email address is malformed
            errorMessage = "Failed to authenticate: "
                    + ((FirebaseAuthInvalidCredentialsException) exception);
        } else if (exception instanceof FirebaseAuthUserCollisionException) {
            // if there already exists an account with the given email address
            errorMessage = "Email: " + ((FirebaseAuthUserCollisionException) exception).getEmail()
                    + " already belongs to another user";
        }
        // Exceptions associated to password
        else if (exception instanceof FirebaseAuthWeakPasswordException) {
            //  if the password is not strong enough
            errorMessage = ((FirebaseAuthWeakPasswordException) exception).getReason();
        }

        // Display the error message to the user.
        if (errorMessage != null && !errorMessage.isEmpty()) {
            Toast.makeText(getParentFragment().requireContext(), errorMessage, Toast.LENGTH_LONG).show();
        }
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

}
