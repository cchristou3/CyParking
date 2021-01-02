package io.github.cchristou3.CyParking.ui.widgets.update;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.data.model.user.LoggedInUser;
import io.github.cchristou3.CyParking.data.pojo.form.update.UpdateFormState;
import io.github.cchristou3.CyParking.data.repository.AccountRepository;
import io.github.cchristou3.CyParking.data.repository.AuthenticatorRepository;
import io.github.cchristou3.CyParking.ui.user.login.AuthenticatorViewModel;

import static io.github.cchristou3.CyParking.ui.host.MainHostActivity.TAG;

/**
 * Purpose: <p>Data persistence when orientation changes.
 * Used when the users try to update their information.</p>
 *
 * @author Charalambos Christou
 * @version 2.0 29/12/20
 */
public class UpdateViewModel extends ViewModel {

    final private MutableLiveData<String> mDialogTitle = new MutableLiveData<>();
    final private MutableLiveData<String> mActionFieldTitle = new MutableLiveData<>();
    final private MutableLiveData<String> mActionFieldInput = new MutableLiveData<>("");
    final private MutableLiveData<UpdateFormState> mUpdateFormState = new MutableLiveData<>();

    final private AccountRepository mAccountRepository;
    private short mDialogType = UpdateAccountDialog.UPDATE_DISPLAY_NAME; // By default

    /**
     * Initializes the object's AccountRepository instance
     * with the specified argument.
     *
     * @param accountRepository An AccountRepository instance
     */
    public UpdateViewModel(AccountRepository accountRepository) {
        this.mAccountRepository = accountRepository;
    }

    /**
     * Updates the value of the UpdateFormState member based on the
     * specified string.
     *
     * @param updatedField The text value of the UI element
     */
    public void formDataChanged(String updatedField) {
        mActionFieldInput.setValue(updatedField);
        switch (mDialogType) {
            case UpdateAccountDialog.UPDATE_DISPLAY_NAME:
                if (updatedField != null && !updatedField.trim().isEmpty()) {
                    mUpdateFormState.setValue(new UpdateFormState(true));
                } else {
                    mUpdateFormState.setValue(new UpdateFormState(R.string.invalid_username));
                }
                break;
            case UpdateAccountDialog.UPDATE_EMAIL:
                if (AuthenticatorViewModel.isEmailValid(updatedField)) {
                    mUpdateFormState.setValue(new UpdateFormState(true));
                } else {
                    mUpdateFormState.setValue(new UpdateFormState(R.string.invalid_email));
                }
                break;
            case UpdateAccountDialog.UPDATE_PASSWORD:
                if (AuthenticatorViewModel.isPasswordValid(updatedField)) {
                    mUpdateFormState.setValue(new UpdateFormState(true));
                } else {
                    mUpdateFormState.setValue(new UpdateFormState(R.string.invalid_password));
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
    public Task<Void> updateAccountField(LoggedInUser user, String updatedField) {
        switch (mDialogType) {
            case UpdateAccountDialog.UPDATE_DISPLAY_NAME:
                return mAccountRepository.updateDisplayName(updatedField)
                        .continueWithTask(task -> {
                            Log.d(TAG, "New display name then: " + task.getResult());
                            if (task.isSuccessful()) {
                                return AuthenticatorRepository.getInstance(FirebaseAuth.getInstance())
                                        .updateUserDisplayName(user.getUserId(), updatedField);
                            }
                            return null;
                        });
            case UpdateAccountDialog.UPDATE_EMAIL:
                return mAccountRepository.updateEmail(updatedField)
                        .continueWithTask(task -> {
                            Log.d(TAG, "then: " + task.getResult());
                            if (task.isSuccessful()) {
                                AuthenticatorRepository.getInstance(FirebaseAuth.getInstance())
                                        .updateUserEmail(user.getUserId(), user.getEmail(), updatedField);
                                return task;
                            }
                            return null;
                        });
            case UpdateAccountDialog.UPDATE_PASSWORD:
                return mAccountRepository.updatePassword(updatedField);
            default:
                return null;
        }
    }

    public boolean isFormValid() {
        if (getUpdateFormState().getValue() != null)
            return getUpdateFormState().getValue().isDataValid();
        else
            return false;
    }

    /**
     * Getters for all data members
     */
    public MutableLiveData<String> getDialogTitle() {
        return mDialogTitle;
    }

    public MutableLiveData<String> getActionFieldTitle() {
        return mActionFieldTitle;
    }

    public MutableLiveData<String> getActionFieldInput() {
        return mActionFieldInput;
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
        return mUpdateFormState;
    }
}
