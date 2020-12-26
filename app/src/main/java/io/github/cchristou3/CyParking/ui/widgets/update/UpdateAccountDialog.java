package io.github.cchristou3.CyParking.ui.widgets.update;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;

import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.R;

import static io.github.cchristou3.CyParking.ui.widgets.DescriptionDialog.getStyleConfiguration;

/**
 * Purpose: Allows the users to update one of their attributes.
 * TODO: Update user data on the database node as well if updating email/display name
 *
 * @author Charalambos Christou
 * @version 2.0 11/12/20
 */
public class UpdateAccountDialog extends DialogFragment implements View.OnClickListener {

    // Fragment's constants
    public static final short UPDATE_DISPLAY_NAME = 144;
    public static final short UPDATE_EMAIL = 400;
    public static final short UPDATE_PASSWORD = 899;
    private static final String TITLE_KEY = "TITLE_KEY";
    private static final String FIELD_TO_BE_UPDATED_KEY = "FIELD_TO_BE_UPDATED_KEY";
    private static final String DIALOG_TYPE_KEY = "DIALOG_TYPE_KEY";

    // Fragment's variables
    private UpdateViewModel mUpdateViewModel;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DescriptionDialog.
     */
    @NotNull
    public static UpdateAccountDialog newInstance(String title, String fieldToBeUpdated, int nightModeFlags, short updateState) {
        UpdateAccountDialog dialog = new UpdateAccountDialog(); // Instantiate an UpdateAccountDialog object
        Bundle args = new Bundle(); // Create a bundle store key information about the dialog
        args.putString(TITLE_KEY, title);
        args.putString(FIELD_TO_BE_UPDATED_KEY, fieldToBeUpdated);
        args.putShort(DIALOG_TYPE_KEY, updateState);
        dialog.setArguments(args); // Pass in the bundle
        // Set the dialog's style
        dialog.setStyle(DialogFragment.STYLE_NORMAL, getStyleConfiguration(nightModeFlags));
        return dialog;
    }

    /**
     * Initializes the fragment with the specified arguments.
     *
     * @param savedInstanceState A bundle which contains info about previously stored data
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Initialize ViewModel and its LiveData data members
            mUpdateViewModel = new ViewModelProvider(this, new UpdateViewModelFactory()).get(UpdateViewModel.class);
            mUpdateViewModel.getTitle().setValue(getArguments().getString(TITLE_KEY));
            mUpdateViewModel.getFieldText().setValue(getArguments().getString(FIELD_TO_BE_UPDATED_KEY));
            mUpdateViewModel.setDialogType(getArguments().getShort(DIALOG_TYPE_KEY));
        }
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
        return inflater.inflate(R.layout.dialog_account_update, container, false);
    }

    /**
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize the dialog's title and body
        requireDialog().setTitle(mUpdateViewModel.getTitle().getValue());
        ((MaterialTextView) view
                .findViewById(R.id.dialog_account_update_mtv_field)).setText(mUpdateViewModel.getFieldText().getValue());

        // Attach listeners to both buttons
        view.findViewById(R.id.dialog_account_update_mbtn_dismiss).setOnClickListener(v -> dismiss());
        final Button updateButton = view.findViewById(R.id.dialog_account_update_mbtn_update);
        updateButton.setOnClickListener(this);
        updateButton.setEnabled(false); // disable it

        // Initialize the UI's content
        final TextInputEditText textInputEditText = view.findViewById(R.id.dialog_account_update_met_input);
        final short mUpdateState = mUpdateViewModel.getDialogType();
        setInputTypeTo(mUpdateState, textInputEditText);
        // Display the saved value
        textInputEditText.setText(mUpdateViewModel.getUpdatedFieldText().getValue());
        textInputEditText.setHint("Enter new " +
                ((mUpdateState == UPDATE_DISPLAY_NAME) ? "name" : (mUpdateState == UPDATE_EMAIL) ? "email" : "password") + "...");
        textInputEditText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* ignore */ }

            public void onTextChanged(CharSequence s, int start, int before, int count) { /* ignore */ }

            @Override
            public void afterTextChanged(Editable s) {
                mUpdateViewModel.formDataChanged(s.toString());
            }
        });

        // Add observer to out form
        mUpdateViewModel.getUpdateFormState().observe(this, updateFormState -> {
            updateButton.setEnabled(updateFormState.isDataValid());
            if (updateFormState.getError() != null) {
                // Show error hint
                textInputEditText.setError(getString(updateFormState.getError()));
            } else {
                // Remove error hint if it displays
                if (textInputEditText.getError() != null) {
                    textInputEditText.setError(null, null);
                }
            }
        });
    }

    /**
     * Sets the input type of the specified TextInputEditText
     * based on the specified numeric value (dialog type).
     *
     * @param dialogType        The type of the dialog
     * @param textInputEditText A UI element of type TextInputEditText
     */
    private void setInputTypeTo(@NotNull Short dialogType, TextInputEditText textInputEditText) {
        switch (dialogType) {
            case UpdateAccountDialog.UPDATE_PASSWORD:
                textInputEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                break;
            case UPDATE_EMAIL:
                textInputEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
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
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;

        // Access the field's info
        final String updatedField = ((TextInputEditText) requireView().findViewById(R.id.dialog_account_update_met_input))
                .getText().toString();
        // Update it and save a reference to its returning Task object
        Task<Void> updateTask = mUpdateViewModel.updateField(updatedField);

        if (updateTask != null) {
            updateTask.addOnCompleteListener(task -> {
                if (task.isSuccessful()) { // if successful
                    // Compose a message
                    short updateState = mUpdateViewModel.getDialogType();
                    final String actionItem =
                            (updateState == UPDATE_DISPLAY_NAME) ? "name" : (updateState == UPDATE_EMAIL) ? "email" : "password";
                    final String toastMsg = "User " + actionItem + (task.isSuccessful() ? " updated." : "failed to update.");
                    // And display it to the user
                    Toast.makeText(requireContext(), toastMsg, Toast.LENGTH_SHORT).show();
                    // Hide the dialog
                    dismiss();
                } else {
                    // https://firebase.google.com/docs/auth/android/manage-users#re-authenticate_a_user
                    // Re-authenticate the user automatically
                    if (task.getException() instanceof FirebaseAuthRecentLoginRequiredException) {
                        // TODO: Test
                        mUpdateViewModel.reauthenticateUser(updatedField);
                    }
                }
            });
        }
    }
}
