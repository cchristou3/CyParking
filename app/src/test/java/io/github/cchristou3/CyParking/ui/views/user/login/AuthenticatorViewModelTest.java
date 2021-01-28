package io.github.cchristou3.CyParking.ui.views.user.login;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import io.github.cchristou3.CyParking.data.pojo.form.login.AuthFormState;
import io.github.cchristou3.CyParking.data.repository.AuthenticatorRepository;
import io.github.cchristou3.CyParking.ui.InstantTaskRuler;

import static io.github.cchristou3.CyParking.ui.LiveDataTestUtil.getOrAwaitValue;
import static io.github.cchristou3.CyParking.ui.views.user.login.AuthenticatorViewModel.isEmailValid;
import static io.github.cchristou3.CyParking.ui.views.user.login.AuthenticatorViewModel.isPasswordValid;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for the {@link AuthenticatorViewModel} class.
 */
@RunWith(AndroidJUnit4.class)
public class AuthenticatorViewModelTest extends InstantTaskRuler {

    // Subject under test
    private AuthenticatorViewModel authenticatorViewModel;

    @Before
    public void setUp() {
        authenticatorViewModel = new AuthenticatorViewModel(Mockito.mock(AuthenticatorRepository.class));
    }

    @Test
    public void isEmailValid_null_returnsFalse() {
        Assert.assertFalse(isEmailValid(null));
    }

    @Test
    public void isEmailValid_empty_returnsFalse() {
        Assert.assertFalse(isEmailValid(""));
    }


    @Test
    public void isEmailValid_whiteSpaces_returnsFalse() {
        Assert.assertFalse(isEmailValid("    "));
    }

    @Test
    public void isEmailValid_notAtSymbol_returnsFalse() {
        Assert.assertFalse(isEmailValid("aaagmail.com"));
    }

    @Test
    public void isEmailValid_validEmail_returnsTrue() {
        Assert.assertTrue(isEmailValid("aaa@gmail.com"));
    }

    @Test
    public void isPasswordValid_null_returnsFalse() {
        Assert.assertFalse(isPasswordValid(null));
    }

    @Test
    public void isPasswordValid_empty_returnsFalse() {
        Assert.assertFalse(isPasswordValid(""));
    }

    @Test
    public void isPasswordValid_over5_returnsTrue() {
        Assert.assertTrue(isPasswordValid("123456"));
    }

    @Test
    public void dataChanged_userSigningIn_validEmailValidPassword_setsFormStateToValid() throws InterruptedException {
        // Given the user is signing in
        authenticatorViewModel.isUserSigningIn(true);
        // When he inputs valid password and email
        authenticatorViewModel.dataChanged("test@gmail.com", null, "123456", false, false);
        // Then the form state should update to valid and errors should be null
        AuthFormState state = getOrAwaitValue(authenticatorViewModel.getFormState());
        assertThat(state.isDataValid(), is(true));
        assertThat(state.getPasswordError(), is(nullValue()));
        assertThat(state.getEmailError(), is(nullValue()));
    }

    @Test
    public void dataChanged_userSigningIn_invalidEmailValidPassword_setsFormStateToValid() throws InterruptedException {
        // Given the user is signing in
        authenticatorViewModel.isUserSigningIn(true);
        // When he inputs valid password and email
        authenticatorViewModel.dataChanged("test", null, "123456", false, false);
        // Then the form state should update to valid and errors should be null
        AuthFormState state = getOrAwaitValue(authenticatorViewModel.getFormState());
        assertThat(state.isDataValid(), is(not(true)));
        assertThat(state.getPasswordError(), is(nullValue()));
        assertThat(state.getEmailError(), is(not(nullValue())));
    }

    @Test
    public void dataChanged_userSigningIn_invalidEmailInvalidPassword_setsFormStateToValid() throws InterruptedException {
        // Given the user is signing in
        authenticatorViewModel.isUserSigningIn(true);
        // When he inputs valid password and email
        authenticatorViewModel.dataChanged("test", null, "16", false, false);
        // Then the form state should update to valid and errors should be null
        AuthFormState state = getOrAwaitValue(authenticatorViewModel.getFormState());
        assertThat(state.isDataValid(), is(not(true)));
        assertThat(state.getPasswordError(), is(nullValue()));
        assertThat(state.getEmailError(), is(not(nullValue())));
    }

    ////////////////////////////////////
    @Test
    public void dataChanged_userNotSigningIn_validEmailValidNameValidPasswordIsUserIsNotOper_setsFormStateToValid() throws InterruptedException {
        // Given the user is signing in
        authenticatorViewModel.isUserSigningIn(false);
        // When he inputs valid password and email
        authenticatorViewModel.dataChanged("test@gmail.com", "name", "123456", true, false);
        // Then the form state should update to valid and errors should be null
        AuthFormState state = getOrAwaitValue(authenticatorViewModel.getFormState());
        assertThat(state.isDataValid(), is(true));
        assertThat(state.getPasswordError(), is(nullValue()));
        assertThat(state.getNameError(), is(nullValue()));
        assertThat(state.getEmailError(), is(nullValue()));
        assertThat(state.getRoleError(), is(nullValue()));
    }

    @Test
    public void dataChanged_userNotSigningIn_validEmailValidNameValidPasswordIsNotUserIsNotOper_setsFormStateToValid() throws InterruptedException {
        // Given the user is signing in
        authenticatorViewModel.isUserSigningIn(false);
        // When he inputs valid password and email
        authenticatorViewModel.dataChanged("test@gmail.com", "Name", "123456", false, false);
        // Then the form state should update to valid and errors should be null
        AuthFormState state = getOrAwaitValue(authenticatorViewModel.getFormState());
        assertThat(state.isDataValid(), is(not(true)));
        assertThat(state.getPasswordError(), is(nullValue()));
        assertThat(state.getNameError(), is(nullValue()));
        assertThat(state.getEmailError(), is(nullValue()));
        assertThat(state.getRoleError(), is(not(nullValue())));
    }

    @Test
    public void dataChanged_userNotSigningIn_validEmailValidNameValidPasswordIsNotUserIsOper_setsFormStateToValid() throws InterruptedException {
        // Given the user is signing in
        authenticatorViewModel.isUserSigningIn(false);
        // When he inputs valid password and email
        authenticatorViewModel.dataChanged("test@gmail.com", "name", "123456", false, true);
        // Then the form state should update to valid and errors should be null
        AuthFormState state = getOrAwaitValue(authenticatorViewModel.getFormState());
        assertThat(state.isDataValid(), is(true));
        assertThat(state.getPasswordError(), is(nullValue()));
        assertThat(state.getNameError(), is(nullValue()));
        assertThat(state.getEmailError(), is(nullValue()));
        assertThat(state.getRoleError(), is(nullValue()));
    }

    @Test
    public void dataChanged_userNotSigningIn_invalidEmailValidNameValidPassword_setsFormStateToValid() throws InterruptedException {
        // Given the user is signing in
        authenticatorViewModel.isUserSigningIn(false);
        // When he inputs valid password and email
        authenticatorViewModel.dataChanged("test", "name", "123456", false, false);
        // Then the form state should update to valid and errors should be null
        AuthFormState state = getOrAwaitValue(authenticatorViewModel.getFormState());
        assertThat(state.isDataValid(), is(not(true)));
        assertThat(state.getNameError(), is(nullValue()));
        assertThat(state.getPasswordError(), is(nullValue()));
        assertThat(state.getEmailError(), is(not(nullValue())));
    }

    @Test
    public void dataChanged_userNotSigningIn_validEmailInvalidNameValidPasswordIsNotUserIsNotOper_setsFormStateToValid() throws InterruptedException {
        // Given the user is signing in
        authenticatorViewModel.isUserSigningIn(false);
        // When he inputs valid password and email
        authenticatorViewModel.dataChanged("test@gmail.com", null, "123456", false, false);
        // Then the form state should update to valid and errors should be null
        AuthFormState state = getOrAwaitValue(authenticatorViewModel.getFormState());
        assertThat(state.isDataValid(), is(not(true)));
        assertThat(state.getPasswordError(), is(nullValue()));
        assertThat(state.getNameError(), is(not(nullValue())));
        assertThat(state.getEmailError(), is(nullValue()));
        assertThat(state.getRoleError(), is(nullValue()));
    }

    @Test
    public void dataChanged_userNotSigningIn_invalidEmailValidNameInvalidPassword_setsFormStateToValid() throws InterruptedException {
        // Given the user is signing in
        authenticatorViewModel.isUserSigningIn(false);
        // When he inputs valid password and email
        authenticatorViewModel.dataChanged("test", "name", "16", false, false);
        // Then the form state should update to valid and errors should be null
        AuthFormState state = getOrAwaitValue(authenticatorViewModel.getFormState());
        assertThat(state.isDataValid(), is(not(true)));
        assertThat(state.getNameError(), is(nullValue()));
        assertThat(state.getPasswordError(), is(nullValue()));
        assertThat(state.getEmailError(), is(not(nullValue())));
    }

    @Test
    public void updateEmail_setsNewEmail() throws InterruptedException {
        // Given the input email got changed
        String email = "some email";
        // When the updateEmail get invoked
        authenticatorViewModel.updateEmail(email);
        // Then the LiveData's value should be the same as the input email
        assertThat(getOrAwaitValue(authenticatorViewModel.getEmailState()), is(email));
    }

    @Test
    public void updateEmail_null_setsNewEmail() throws InterruptedException {
        // Given the input email got changed
        String email = null;
        // When the updateEmail get invoked
        authenticatorViewModel.updateEmail(email);
        // Then the LiveData's value should be the same as the input email
        assertThat(getOrAwaitValue(authenticatorViewModel.getEmailState()), is(email));
    }

    @Test
    public void isUserSigningIn_true_setsNewValue() throws InterruptedException {
        // Given the input email got changed
        boolean isUserSigningIn = true;
        // When the updateEmail get invoked
        authenticatorViewModel.isUserSigningIn(isUserSigningIn);
        // Then the LiveData's value should be the same as the input email
        assertThat(getOrAwaitValue(authenticatorViewModel.getTabState()), is(isUserSigningIn));
        assertThat(authenticatorViewModel.isUserSigningIn(), is(isUserSigningIn));
    }

    @Test
    public void isUserSigningIn_false_setsNewValue() throws InterruptedException {
        // Given the input email got changed
        boolean isNotUserSigningIn = false;
        // When the updateEmail get invoked
        authenticatorViewModel.isUserSigningIn(isNotUserSigningIn);
        // Then the LiveData's value should be the same as the input email
        assertThat(getOrAwaitValue(authenticatorViewModel.getTabState()), is(isNotUserSigningIn));
        assertThat(authenticatorViewModel.isUserSigningIn(), is(isNotUserSigningIn));
    }
}
