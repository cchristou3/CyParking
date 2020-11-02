package io.github.cchristou3.CyParking.view.ui.login;

/**
 * purpose: Class exposing authenticated user details to the UI.
 *
 * @author Charalambos Christou
 * @version 1.0 1/11/20
 */
public class LoggedInUserView {
    private final String displayName;
    private final boolean isUser;
    private final boolean isOperator;
    //... other data fields that may be accessible to the UI

    /**
     * LoggedInUserView's Constructor.
     *
     * @param displayName The name of which the user will be addressed in the UI.
     * @param isUser      Whether the user is logged in as a user.
     * @param isOperator  Whether the user is logged in as an operator.
     */
    public LoggedInUserView(String displayName, boolean isUser, boolean isOperator) {
        this.displayName = displayName;
        this.isUser = isUser;
        this.isOperator = isOperator;
    }

    /**
     * Getters & Setters
     */
    public String getDisplayName() {
        return displayName;
    }

    public boolean isUser() {
        return isUser;
    }

    public boolean isOperator() {
        return isOperator;
    }
}