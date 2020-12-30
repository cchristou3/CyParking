package io.github.cchristou3.CyParking.data.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import org.jetbrains.annotations.Nullable;

import io.github.cchristou3.CyParking.data.pojo.user.LoggedInUser;

/**
 * Purpose: <p>Class that offers to the user's the following services:
 * <p>- Update their display name
 * <p>- Update their email
 * <p>- Update their password
 *
 * @author Charalambos Christou
 * @version 3.0 28/12/20
 */
public class AccountRepository {

    @Nullable
    private FirebaseUser mFirebaseUser = null;

    /**
     * Initializes the AccountRepository's firebase user
     * based on the specified argument.
     *
     * @param loggedInUser The current instance of LoggedInUser
     */
    public AccountRepository(@Nullable LoggedInUser loggedInUser) {
        if (loggedInUser != null)
            this.mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * @return A reference to the object's FirebaseUser instance
     */
    @Nullable
    public FirebaseUser getFirebaseUser() {
        return mFirebaseUser;
    }

    /**
     * Updates the user's display name with the specified parameter.
     *
     * @param newDisplayName The user's new name.
     * @return A Task<Void> object
     */
    @Nullable
    public Task<Void> updateDisplayName(String newDisplayName) {
        return mFirebaseUser.updateProfile(new UserProfileChangeRequest.Builder()
                .setDisplayName(newDisplayName)
                .build());
    }

    /**
     * Updates the user's email with the specified parameter.
     *
     * @param newEmail The user's new email.
     * @return A Task<Void> object
     */
    @Nullable
    public Task<Void> updateEmail(String newEmail) {
        return mFirebaseUser.updateEmail(newEmail);
    }

    /**
     * Updates the user's password with the specified parameter.
     *
     * @param newPassword The user's new password.
     * @return A Task<Void> object
     */
    @Nullable
    public Task<Void> updatePassword(String newPassword) {
        return mFirebaseUser.updatePassword(newPassword);
    }

    public Task<Void> reauthenticateUser(String credentials) {
        return mFirebaseUser.reauthenticate(EmailAuthProvider
                .getCredential(mFirebaseUser.getEmail(), credentials));
    }
}
