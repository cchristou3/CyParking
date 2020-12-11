package io.github.cchristou3.CyParking.view.ui.support.update;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.EmailAuthProvider;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.view.data.pojo.update.UpdateFormState;
import io.github.cchristou3.CyParking.view.data.repository.AccountRepository;
import io.github.cchristou3.CyParking.view.ui.login.LoginViewModel;

/**
 * Purpose: <p>Data persistence when orientation changes.
 * Used when the users try to update their information.</p>
 *
 * @author Charalambos Christou
 * @version 1.0 05/11/20
 */
public class UpdateViewModel extends ViewModel {

    final private MutableLiveData<String> title = new MutableLiveData<>();
    final private MutableLiveData<String> fieldText = new MutableLiveData<>();
    final private MutableLiveData<String> updatedFieldText = new MutableLiveData<>();
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
    public void updateDataChanged(String updatedField) {
        switch (mDialogType) {
            case UpdateAccountDialog.UPDATE_DISPLAY_NAME:
                if (updatedField != null && !updatedField.trim().isEmpty()) {
                    updateFormState.setValue(new UpdateFormState(true));
                } else {
                    updateFormState.setValue(new UpdateFormState(R.string.invalid_username));
                }
                break;
            case UpdateAccountDialog.UPDATE_EMAIL:
                if (LoginViewModel.isEmailValid(updatedField)) {
                    updateFormState.setValue(new UpdateFormState(true));
                } else {
                    updateFormState.setValue(new UpdateFormState(R.string.invalid_email));
                }
                break;
            case UpdateAccountDialog.UPDATE_PASSWORD:
                if (LoginViewModel.isPasswordValid(updatedField)) {
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
            case UpdateAccountDialog.UPDATE_DISPLAY_NAME:
                return accountRepository.updateDisplayName(updatedField);
            case UpdateAccountDialog.UPDATE_EMAIL:
                return accountRepository.updateEmail(updatedField);
            case UpdateAccountDialog.UPDATE_PASSWORD:
                return accountRepository.updatePassword(updatedField);
            default:
                return null;
        }
    }

    /**
     * Internally invokes FirebaseUser#reauthenticate
     * to reauthenticate the user. Normally, the user is prompt
     * to re enter their credentials. In this case, we use the user's
     * latest entered credentials to avoid additional steps.
     *
     * @param credentials The user's credentials
     */
    public void reauthenticateUser(String credentials) {
        accountRepository.getFirebaseUser()
                .reauthenticate(EmailAuthProvider
                        .getCredential(accountRepository.getFirebaseUser().getEmail(), credentials));
    }

    /**
     * Getters for all data members
     */
    public MutableLiveData<String> getTitle() {
        return title;
    }

    public MutableLiveData<String> getFieldText() {
        return fieldText;
    }

    public MutableLiveData<String> getUpdatedFieldText() {
        return updatedFieldText;
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
