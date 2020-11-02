package io.github.cchristou3.CyParking.view.data.model;

/**
 * purpose: Data class that captures user information for logged in users retrieved from LoginRepository
 *
 * @author Charalambos Christou
 * @version 1.0 1/11/20
 */
public class LoggedInUser {

    private String userId;
    private String displayName;

    public LoggedInUser(String userId, String displayName) {
        this.userId = userId;
        this.displayName = displayName;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }
}