package io.github.cchristou3.CyParking.ui.views.user.account.update;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Task;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import io.github.cchristou3.CyParking.apiClient.model.data.user.LoggedInUser;
import io.github.cchristou3.CyParking.apiClient.remote.repository.AccountRepository;
import io.github.cchristou3.CyParking.ui.InstantTaskRuler;

import static io.github.cchristou3.CyParking.ui.LiveDataTestUtil.getOrAwaitValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link UpdateViewModel} class.
 */
@RunWith(AndroidJUnit4.class)
public class UpdateViewModelTest extends InstantTaskRuler {

    private final LoggedInUser mockUser = Mockito.mock(LoggedInUser.class);

    // Subject under test
    private UpdateViewModel updateViewModel;

    @Before
    public void setUp() throws Exception {
        AccountRepository mockRepo = Mockito.mock(AccountRepository.class);
        when(mockRepo.updateDisplayName("", mockUser)).thenReturn(Mockito.mock(Task.class));
        when(mockRepo.updateEmail("", mockUser)).thenReturn(Mockito.mock(Task.class));
        when(mockRepo.updatePassword("")).thenReturn(Mockito.mock(Task.class));
        updateViewModel = new UpdateViewModel(mockRepo);
    }

    @Test
    public void formDataChanged_validDisplayName_setsValidForm() throws InterruptedException {
        // When the action is to update the user name
        updateViewModel.setDialogType(UpdateAccountDialog.UPDATE_DISPLAY_NAME);
        // Given a valid display name was inputted
        updateViewModel.formDataChanged("validName");
        // Then the form should have no errors and be valid.
        assertThat(getOrAwaitValue(updateViewModel.getUpdateFormState()).getError(), is(nullValue()));
        assertThat(getOrAwaitValue(updateViewModel.getUpdateFormState()).isDataValid(), is(true));
        assertThat(updateViewModel.isFormValid(), is(true));
        assertThat(updateViewModel.getDialogType(), is(UpdateAccountDialog.UPDATE_DISPLAY_NAME));
    }

    @Test
    public void formDataChanged_invalidDisplayName_setsInvalidForm() throws InterruptedException {
        // When the action is to update the user name
        updateViewModel.setDialogType(UpdateAccountDialog.UPDATE_DISPLAY_NAME);
        // Given a invalid display name was inputted
        updateViewModel.formDataChanged("");
        // Then the form should have errors and be invalid.
        assertThat(getOrAwaitValue(updateViewModel.getUpdateFormState()).getError(), is(not(nullValue())));
        assertThat(getOrAwaitValue(updateViewModel.getUpdateFormState()).isDataValid(), is(not(true)));
        assertThat(updateViewModel.isFormValid(), is(not(true)));
    }

    @Test
    public void formDataChanged_validEmail_setsValidForm() throws InterruptedException {
        // When the action is to update the email
        updateViewModel.setDialogType(UpdateAccountDialog.UPDATE_EMAIL);
        // Given a valid email was inputted
        updateViewModel.formDataChanged("valid@gmail.com");
        // Then the form should have no errors and be valid.
        assertThat(getOrAwaitValue(updateViewModel.getUpdateFormState()).getError(), is(nullValue()));
        assertThat(getOrAwaitValue(updateViewModel.getUpdateFormState()).isDataValid(), is(true));
        assertThat(updateViewModel.isFormValid(), is(true));
        assertThat(updateViewModel.getDialogType(), is(UpdateAccountDialog.UPDATE_EMAIL));
    }

    @Test
    public void formDataChanged_invalidEmail_setsInvalidForm() throws InterruptedException {
        // When the action is to update the email
        updateViewModel.setDialogType(UpdateAccountDialog.UPDATE_EMAIL);
        // Given a invalid email was inputted
        updateViewModel.formDataChanged("");
        // Then the form should have errors and be invalid.
        assertThat(getOrAwaitValue(updateViewModel.getUpdateFormState()).getError(), is(not(nullValue())));
        assertThat(getOrAwaitValue(updateViewModel.getUpdateFormState()).isDataValid(), is(not(true)));
        assertThat(updateViewModel.isFormValid(), is(not(true)));
    }

    @Test
    public void formDataChanged_validPassword_setsValidForm() throws InterruptedException {
        // When the action is to update the password
        updateViewModel.setDialogType(UpdateAccountDialog.UPDATE_PASSWORD);
        // Given a valid password was inputted
        updateViewModel.formDataChanged("1234567");
        // Then the form should have no errors and be valid.
        assertThat(getOrAwaitValue(updateViewModel.getUpdateFormState()).getError(), is(nullValue()));
        assertThat(getOrAwaitValue(updateViewModel.getUpdateFormState()).isDataValid(), is(true));
        assertThat(updateViewModel.isFormValid(), is(true));
        assertThat(updateViewModel.getDialogType(), is(UpdateAccountDialog.UPDATE_PASSWORD));
    }

    @Test
    public void formDataChanged_invalidPassword_setsInvalidForm() throws InterruptedException {
        // When the action is to update the password
        updateViewModel.setDialogType(UpdateAccountDialog.UPDATE_PASSWORD);
        // Given a invalid password was inputted
        updateViewModel.formDataChanged("");
        // Then the form should have errors and be invalid.
        assertThat(getOrAwaitValue(updateViewModel.getUpdateFormState()).getError(), is(not(nullValue())));
        assertThat(getOrAwaitValue(updateViewModel.getUpdateFormState()).isDataValid(), is(not(true)));
        assertThat(updateViewModel.isFormValid(), is(not(true)));
    }

    @Test
    public void updateAccountField_displayName_returnsNonNull() {
        // When the action is to update the name
        updateViewModel.setDialogType(UpdateAccountDialog.UPDATE_DISPLAY_NAME);
        assertThat(updateViewModel.updateAccountField(mockUser, ""), is(not(nullValue())));
    }

    @Test
    public void updateAccountField_email_returnsNonNull() {
        // When the action is to update the email
        updateViewModel.setDialogType(UpdateAccountDialog.UPDATE_EMAIL);
        assertThat(updateViewModel.updateAccountField(mockUser, ""), is(not(nullValue())));
    }

    @Test
    public void updateAccountField_password_returnsNonNull() {
        // When the action is to update the name
        updateViewModel.setDialogType(UpdateAccountDialog.UPDATE_PASSWORD);
        assertThat(updateViewModel.updateAccountField(mockUser, ""), is(not(nullValue())));
    }

    @Test
    public void updateAccountField_invalidState_returnsNull() {
        // When the action is to update the name
        updateViewModel.setDialogType((short) 99);
        assertThat(updateViewModel.updateAccountField(mockUser, ""), is(nullValue()));
    }

    @Test
    public void getActionFieldInput_returnsPreviouslySetValue() {
        // Given the input got updated
        String attribute = "SomeAttribute";
        updateViewModel.formDataChanged(attribute);
        // Then the method should return the same as it input
        assertThat(updateViewModel.getActionFieldInput(), is(attribute));
    }
}