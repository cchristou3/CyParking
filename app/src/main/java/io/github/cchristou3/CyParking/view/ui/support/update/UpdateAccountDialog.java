package io.github.cchristou3.CyParking.view.ui.support.update;

import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;

import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.R;

import static io.github.cchristou3.CyParking.view.ui.ParkingMapFragment.TAG;
import static io.github.cchristou3.CyParking.view.ui.support.DescriptionDialog.getStyleConfiguration;

// TODO: Add log out button. Test update password functionality
/**
 * Purpose: TODO: Add further comments
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Initialize ViewModel and its LiveData data members
            mUpdateViewModel = new ViewModelProvider(this, new UpdateViewModelFactory()).get(UpdateViewModel.class);
            mUpdateViewModel.getTitle().setValue(getArguments().getString(TITLE_KEY));
            mUpdateViewModel.getFieldText().setValue(getArguments().getString(FIELD_TO_BE_UPDATED_KEY));
            mUpdateViewModel.getDialogState().setValue(getArguments().getShort(DIALOG_TYPE_KEY));
            mUpdateViewModel.getUpdatedFieldText().setValue("");
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

        // Initialize the UI's content
        final TextInputEditText textInputEditText = view.findViewById(R.id.dialog_account_update_met_input);
        setInputTypeTo(mUpdateViewModel.getDialogState().getValue(), textInputEditText);
        final short mUpdateState = mUpdateViewModel.getDialogState().getValue();
        textInputEditText.setHint("Enter new " +
                ((mUpdateState == UPDATE_DISPLAY_NAME) ? "name" : (mUpdateState == UPDATE_EMAIL) ? "email" : "password") + "...");

        // Attach listeners to both buttons
        view.findViewById(R.id.dialog_account_update_mbtn_dismiss).setOnClickListener(v -> dismiss());
        final Button updateButton = view.findViewById(R.id.dialog_account_update_mbtn_update);
        updateButton.setOnClickListener(this);

        mUpdateViewModel.getUpdateFormState().observe(this, updateFormState -> {
            updateButton.setEnabled(updateFormState.isDataValid());
            if (updateFormState.getError() != null) {
                // Show error hint
                textInputEditText.setError(getResources().getString(updateFormState.getError()));
            } else {
                // Remove error hint if it displays
                if (textInputEditText.getError() != null) {
                    textInputEditText.setError(null, null);
                }
            }
        });
    }

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
        final String updatedField = ((TextInputEditText) requireView().findViewById(R.id.dialog_account_update_met_input))
                .getText().toString();
        Task<Void> updateTask = mUpdateViewModel.updateField(updatedField);

        if (updateTask != null) {
            updateTask.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    short updateState = mUpdateViewModel.getDialogState().getValue();
                    String actionItem = (updateState == UPDATE_DISPLAY_NAME) ? "name" : (updateState == UPDATE_EMAIL) ? "email" : "password";
                    final String toastMsg = "User " + actionItem + (task.isSuccessful() ? " updated." : "failed to update.");
                    Toast.makeText(requireContext(), toastMsg, Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    // https://firebase.google.com/docs/auth/android/manage-users#re-authenticate_a_user
                    // Re-authenticate the user automatically
                    if (task.getException() instanceof FirebaseAuthRecentLoginRequiredException) {
                        mUpdateViewModel.reauthenticateUser(updatedField);
                    }
                }
            });
        }
    }

    /**
     * Called when the fragment is no longer in use.  This is called
     * after {@link #onStop()} and before {@link #onDetach()}.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Dialog destroyed!");
    }
}
