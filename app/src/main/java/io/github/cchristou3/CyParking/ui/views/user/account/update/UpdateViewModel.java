package io.github.cchristou3.CyParking.ui.views.user.account.update;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.data.model.user.LoggedInUser;
import io.github.cchristou3.CyParking.data.pojo.form.update.UpdateFormState;
import io.github.cchristou3.CyParking.data.repository.AccountRepository;
import io.github.cchristou3.CyParking.ui.components.LoadingBarViewModel;

import static io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot.isNameValid;
import static io.github.cchristou3.CyParking.ui.views.user.login.AuthenticatorViewModel.isEmailValid;
import static io.github.cchristou3.CyParking.ui.views.user.login.AuthenticatorViewModel.isPasswordValid;

/**
 * Purpose: <p>Data persistence when orientation changes.
 * Used when the users try to update their information.</p>
 *
 * @author Charalambos Christou
 * @version 3.0 02/02/21
 */
public class UpdateViewModel extends LoadingBarViewModel {

    final private MutableLiveData<UpdateFormState> mUpdateFormState = new MutableLiveData<>();
    final private MutableLiveData<String> mActionFieldInput = new MutableLiveData<>("");

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
                if (isNameValid(updatedField)) {
                    updateFormState(new UpdateFormState(true));
                } else {
                    updateFormState(new UpdateFormState(R.string.invalid_username));
                }
                break;
            case UpdateAccountDialog.UPDATE_EMAIL:
                if (isEmailValid(updatedField)) {
                    updateFormState(new UpdateFormState(true));
                } else {
                    updateFormState(new UpdateFormState(R.string.invalid_email));
                }
                break;
            case UpdateAccountDialog.UPDATE_PASSWORD:
                if (isPasswordValid(updatedField)) {
                    updateFormState(new UpdateFormState(true));
                } else {
                    updateFormState(new UpdateFormState(R.string.invalid_password));
                }
                break;
        }
    }

    /**
     * Assigns the value of {@link #mUpdateFormState} to
     * the given argument.
     *
     * @param newFormState The new state of the form.
     */
    private void updateFormState(UpdateFormState newFormState) {
        mUpdateFormState.setValue(newFormState);
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
                return mAccountRepository.updateDisplayName(updatedField, user);
            case UpdateAccountDialog.UPDATE_EMAIL:
                return mAccountRepository.updateEmail(updatedField, user);
            case UpdateAccountDialog.UPDATE_PASSWORD:
                return mAccountRepository.updatePassword(updatedField);
            default:
                return null;
        }
    }

    /**
     * Checks the state of the form.
     *
     * @return True, if the form is in valid state. Otherwise, false.
     */
    public boolean isFormValid() {
        if (getUpdateFormState().getValue() != null)
            return getUpdateFormState().getValue().isDataValid();
        else
            return false;
    }

    /**
     * Access the value of {@link #mActionFieldInput} of the ViewModel.
     *
     * @return The value of {@link #mActionFieldInput}.
     */
    public String getActionFieldInput() {
        return mActionFieldInput.getValue();
    }

    /**
     * Access the {@link #mDialogType} of the ViewModel.
     *
     * @return A reference to {@link #mDialogType}.
     */
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

    /**
     * Access the {@link #mUpdateFormState} of the ViewModel.
     *
     * @return A reference to {@link #mDialogType}.
     */
    public LiveData<UpdateFormState> getUpdateFormState() {
        return mUpdateFormState;
    }
}
