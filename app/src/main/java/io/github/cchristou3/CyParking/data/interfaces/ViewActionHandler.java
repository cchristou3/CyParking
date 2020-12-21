package io.github.cchristou3.CyParking.data.interfaces;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseUser;

import io.github.cchristou3.CyParking.data.manager.AuthObserver;

/**
 * Purpose:
 * Provide the {@link AuthObserver} class the appropriate callback
 * method.
 * <p>
 * The {@link ViewActionHandler#updateUi(FirebaseUser)} method
 * executes every time the authentication state of the application
 * changes, passing in the latest instance of {@link FirebaseUser}
 * if there is one.
 *
 * @author Charalambos Christou
 * @version 1.0 14/12/20
 */
public interface ViewActionHandler {
    void updateUi(@Nullable FirebaseUser currentUser);
}
