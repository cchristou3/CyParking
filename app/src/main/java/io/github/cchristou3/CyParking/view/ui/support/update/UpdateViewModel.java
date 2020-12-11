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
    final private MutableLiveData<Short> dialogState = new MutableLiveData<>();
    final private MutableLiveData<UpdateFormState> updateFormState = new MutableLiveData<>();

    private final AccountRepository accountRepository;

    public UpdateViewModel(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public MutableLiveData<String> getTitle() {
        return title;
    }

    public MutableLiveData<String> getFieldText() {
        return fieldText;
    }

    public MutableLiveData<String> getUpdatedFieldText() {
        return updatedFieldText;
    }

    public MutableLiveData<Short> getDialogState() {
        return dialogState;
    }

    public MutableLiveData<UpdateFormState> getUpdateFormState() {
        return updateFormState;
    }

    public void updateDataChanged(String updatedField) {
        switch (dialogState.getValue()) {
            case UpdateAccountDialog.UPDATE_DISPLAY_NAME:
                if (updatedField != null && !updatedField.trim().isEmpty()) {
                    updateFormState.setValue(new UpdateFormState(R.string.invalid_username));
                }
                break;
            case UpdateAccountDialog.UPDATE_EMAIL:
                if (LoginViewModel.isEmailValid(updatedField)) {
                    updateFormState.setValue(new UpdateFormState(R.string.invalid_email));
                }
                break;
            case UpdateAccountDialog.UPDATE_PASSWORD:
                if (LoginViewModel.isPasswordValid(updatedField)) {
                    updateFormState.setValue(new UpdateFormState(R.string.invalid_password));
                }
                break;
        }
    }

    public Task<Void> updateField(String updatedField) {
        switch (dialogState.getValue()) {
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

    public void reauthenticateUser(String updatedField) {
        accountRepository.getFirebaseUser()
                .reauthenticate(EmailAuthProvider
                        .getCredential(accountRepository.getFirebaseUser().getEmail(), updatedField));
    }
}
