package io.github.cchristou3.CyParking.data.pojo.form.login;

import com.google.firebase.auth.FirebaseUser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.LinkedList;

import io.github.cchristou3.CyParking.data.model.user.LoggedInUser;

import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link LoginResult} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class LoginResultTest {

    private LoggedInUser mockLoggedInUser;

    @Mock
    private FirebaseUser mockFirebaseUser;

    @Before
    public void setUp() {
        when(mockFirebaseUser.getUid()).thenReturn("123123");
        when(mockFirebaseUser.getDisplayName()).thenReturn("Name");
        when(mockFirebaseUser.getEmail()).thenReturn("a@gmail.com");
        mockLoggedInUser = new LoggedInUser(mockFirebaseUser, new LinkedList<>());
    }

    @Test
    public void loginResult_with_success() {
        // Given
        LoggedInUser user = mockLoggedInUser;
        // When
        LoginResult result = new LoginResult(user);
        // Then
        Assert.assertTrue(result.getError() == null && result.getSuccess() != null);
    }

    @Test
    public void loginResult_with_error() {
        // Given
        String error = "any_error";
        // When
        LoginResult result = new LoginResult(error);
        // Then
        Assert.assertTrue(result.getError() != null && result.getSuccess() == null);
    }
}