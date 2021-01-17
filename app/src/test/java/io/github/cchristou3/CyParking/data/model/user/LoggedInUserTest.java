package io.github.cchristou3.CyParking.data.model.user;

import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link LoggedInUser} class.
 */
public class LoggedInUserTest {

    private static final String NAME = "Name";
    private static final String EMAIL = "email@gmail.com";
    private static final String UID = "qwertyuiolkjhgfdsazxcvbnxcvb";
    private static final List<String> ROLES = Arrays.asList(LoggedInUser.USER, LoggedInUser.OPERATOR);

    @Mock
    FirebaseUser mockFirebaseUser;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(mockFirebaseUser.getDisplayName()).thenReturn(NAME);
        when(mockFirebaseUser.getEmail()).thenReturn(EMAIL);
        when(mockFirebaseUser.getUid()).thenReturn(UID);
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
    public void isUser_isUser_returnsTrue() {
        // Given a LoggedInUser object as a `user`
        LoggedInUser user = new LoggedInUser(mockFirebaseUser, Arrays.asList(LoggedInUser.USER));
        // When isUser is invoked,
        boolean isUser = user.isUser();
        // Then isUser returns true
        assertTrue(isUser);
    }

    @Test
    public void isUser_isOperator_returnsTrue() {
        // Given a LoggedInUser object as a `user`
        LoggedInUser user = new LoggedInUser(mockFirebaseUser, Arrays.asList(LoggedInUser.OPERATOR));
        // When isUser is invoked,
        boolean isUser = user.isUser();
        // Then isUser returns false
        assertFalse(isUser);
    }

    @Test
    public void isUser_null_returnsTrue() {
        // Given a LoggedInUser object as a `user`
        LoggedInUser user = new LoggedInUser(mockFirebaseUser, null);
        // When isUser is invoked,
        boolean isUser = user.isUser();
        // Then isUser returns false
        assertFalse(isUser);
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
    public void isOperator_isUser_returnsTrue() {
        // Given a LoggedInUser object as a `user`
        LoggedInUser user = new LoggedInUser(mockFirebaseUser, Arrays.asList(LoggedInUser.USER));
        // When isOperator is invoked,
        boolean isUser = user.isOperator();
        // Then isOperator returns false
        assertFalse(isUser);
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