package io.github.cchristou3.CyParking.apiClient.model.user;

import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import io.github.cchristou3.CyParking.apiClient.model.data.user.LoggedInUser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the {@link LoggedInUser} class.
 */
public class LoggedInUserTest {

    private static final String NAME = "Name";
    private static final String EMAIL = "email@gmail.com";
    private static final String UID = "qwertyuiolkjhgfdsazxcvbnxcvb";
    private static final List<String> ROLES = Arrays.asList(LoggedInUser.OPERATOR);

    @Mock
    FirebaseUser mockFirebaseUser;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Mockito.when(mockFirebaseUser.getDisplayName()).thenReturn(NAME);
        Mockito.when(mockFirebaseUser.getEmail()).thenReturn(EMAIL);
        Mockito.when(mockFirebaseUser.getUid()).thenReturn(UID);
    }

    @Test
    public void LoggedInUser_initializesCorrectValues() {
        // When initializing a LoggedInUser object with some attributes
        LoggedInUser user = new LoggedInUser(mockFirebaseUser, ROLES);
        // Then all of its getters should return the same attributes
        assertTrue(
                user.getRoles().equals(ROLES)
                        && user.getUserId().equals(UID)
                        && user.getDisplayName().equals(NAME)
                        && user.getEmail().equals(EMAIL)
        );
    }

    @Test
    public void setDisplayName_setsNewValue() {
        // Given an uninitialized LoggedInUser object
        LoggedInUser user = new LoggedInUser();
        // When setDisplayName is invoked,
        user.setDisplayName(NAME);
        // Then the name gets updated
        assertEquals(user.getDisplayName(), NAME);
    }

    @Test
    public void setEmail_setsNewValue() {
        // Given an uninitialized LoggedInUser object
        LoggedInUser user = new LoggedInUser();
        // When setEmail is invoked,
        user.setEmail(EMAIL);
        // Then the name gets updated
        assertEquals(user.getEmail(), EMAIL);
    }

    @Test
    public void isOperator_isOperator_returnsTrue() {
        // Given a LoggedInUser object as a `user`
        LoggedInUser user = new LoggedInUser(mockFirebaseUser, Arrays.asList(LoggedInUser.OPERATOR));
        // When isOperator is invoked,
        boolean isUser = user.isOperator();
        // Then isOperator returns true
        assertTrue(isUser);
    }

    @Test
    public void isOperator_null_returnsTrue() {
        // Given a LoggedInUser object as a `user`
        LoggedInUser user = new LoggedInUser(mockFirebaseUser, null);
        // When isOperator is invoked,
        boolean isUser = user.isOperator();
        // Then isOperator returns false
        assertFalse(isUser);
    }
}