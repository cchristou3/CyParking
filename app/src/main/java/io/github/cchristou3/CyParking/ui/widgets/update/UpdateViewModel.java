package io.github.cchristou3.CyParking.ui.widgets.update;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.data.pojo.user.update.UpdateFormState;
import io.github.cchristou3.CyParking.data.repository.AccountRepository;
import io.github.cchristou3.CyParking.ui.user.login.AuthenticatorViewModel;

/**
 * Purpose: <p>Data persistence when orientation changes.
 * Used when the users try to update their information.</p>
 *
 * @author Charalambos Christou
 * @version 2.0 29/12/20
 */
public class UpdateViewModel extends ViewModel {

    final private MutableLiveData<String> dialogTitle = new MutableLiveData<>();
    final private MutableLiveData<String> actionFieldTitle = new MutableLiveData<>();
    final private MutableLiveData<String> actionFieldInput = new MutableLiveData<>("");
    final private MutableLiveData<UpdateFormState> updateFormState = new MutableLiveData<>();

    final private AccountRepository accountRepository;
    private short mDialogType = UpdateAccountDialog.UPDATE_DISPLAY_NAME; // By default

    /**
     * Initializes the object's AccountRepository instance
     * with the specified argument.
     *
     * @param accountRepository An AccountRepository instance
     */
    public UpdateViewModel(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Updates the value of the UpdateFormState member based on the
     * specified string.
     *
     * @param updatedField The text value of the UI element
     */
    public void formDataChanged(String updatedField) {
        actionFieldInput.setValue(updatedField);
        switch (mDialogType) {
            case UpdateAccountDialog.UPDATE_DISPLAY_NAME:
                if (updatedField != null && !updatedField.trim().isEmpty()) {
                    updateFormState.setValue(new UpdateFormState(true));
                } else {
                    updateFormState.setValue(new UpdateFormState(R.string.invalid_username));
                }
                break;
            case UpdateAccountDialog.UPDATE_EMAIL:
                if (AuthenticatorViewModel.isEmailValid(updatedField)) {
                    updateFormState.setValue(new UpdateFormState(true));
                } else {
                    updateFormState.setValue(new UpdateFormState(R.string.invalid_email));
                }
                break;
            case UpdateAccountDialog.UPDATE_PASSWORD:
                if (AuthenticatorViewModel.isPasswordValid(updatedField)) {
                    updateFormState.setValue(new UpdateFormState(true));
                } else {
                    updateFormState.setValue(new UpdateFormState(R.string.invalid_password));
                }
                break;
        }
    }

    /**
     * Invokes the corresponding API call based on the dialog's type.
     * Its parameter is the specified String.
     *
     * @param updatedField The new value of a user's attribute
     * @return An instance of Task<Void> to handle the UI changes back to the fragment.
     */
    public Task<Void> updateField(String updatedField) {
        switch (mDialogType) {
            // TODO: For email and display name,
            //  use another continueWithTask to
            //  update their fields in the Firestore database as well.
            case UpdateAccountDialog.UPDATE_DISPLAY_NAME:
                return accountRepository.updateDisplayName(updatedField);
            case UpdateAccountDialog.UPDATE_EMAIL:
                return accountRepository.updateEmail(updatedField)
                        .continueWithTask(getContinuation(updatedField));
            case UpdateAccountDialog.UPDATE_PASSWORD:
                return accountRepository.updatePassword(updatedField)
                        .continueWithTask(getContinuation(updatedField));
            default:
                return null;
        }
    }

    /**
     * src: https://firebase.google.com/docs/auth/android/manage-users#re-authenticate_a_user
     * Re-authenticate the user automatically
     *
     * @param updatedField The user's field that got updated
     * @return A Continuation instance.
     */
    @NotNull
    @Contract(pure = true)
    private Continuation<Void, Task<Void>> getContinuation(String updatedField) {
        return task -> {
            if (task.getException() instanceof FirebaseAuthRecentLoginRequiredException) {
                // TODO: Test
                return reauthenticateUser(updatedField);
            }
            return task;
        };
    }

    /**
     * Internally invokes FirebaseUser#reauthenticate
     * to reauthenticate the user. Normally, the user is prompt
     * to re enter their credentials. In this case, we use the user's
     * latest entered credentials to avoid additional steps.
     *
     * @param credentials The user's credentials
     */
    public Task<Void> reauthenticateUser(String credentials) throws IllegalStateException {
        // TODO: Prompt user to re-enter details (e.g. in another dialog)
        return accountRepository.reauthenticateUser(credentials);
    }

    /**
     * Getters for all data members
     */
    public MutableLiveData<String> getDialogTitle() {
        return dialogTitle;
    }

    public MutableLiveData<String> getActionFieldTitle() {
        return actionFieldTitle;
    }

    public MutableLiveData<String> getActionFieldInput() {
        return actionFieldInput;
    }

    public short getDialogType() {
        return mDialogType;
    }

    /**
     * Setter for {@link UpdateViewModel#mDialogType}
     *
     * @param dialogState The type of the dialog
     */
    public void setDialogType(short dialogState) {
        this.mDialogType = dialogState;
    }

    public MutableLiveData<UpdateFormState> getUpdateFormState() {
        return updateFormState;
    }
}
