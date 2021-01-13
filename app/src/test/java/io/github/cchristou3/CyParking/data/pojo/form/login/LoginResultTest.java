package io.github.cchristou3.CyParking.data.pojo.form.login;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import io.github.cchristou3.CyParking.data.model.user.LoggedInUser;


/**
 * Unit tests for the {@link LoginResult} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class LoginResultTest {

    @Mock
    private LoggedInUser mockLoggedInUser;

    @Before
    public void setUp() {
        mockLoggedInUser = Mockito.mock(LoggedInUser.class);
    }

    @Test
    public void loginResult_success_ErrorIsNullSuccessIsNotNull() {
        // Given
        LoggedInUser user = mockLoggedInUser;
        // When
        LoginResult result = new LoginResult(user);
        // Then
        Assert.assertTrue(result.getError() == null && result.getSuccess() != null);
    }

    @Test
    public void loginResult_error_ErrorIsNotNullSuccessIsNull() {
        // Given
        String error = "any_error";
        // When
        LoginResult result = new LoginResult(error);
        // Then
        Assert.assertTrue(result.getError() != null && result.getSuccess() == null);
    }
}