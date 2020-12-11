package io.github.cchristou3.CyParking.view.data.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import org.jetbrains.annotations.Nullable;

/**
 * Purpose: <p>Class that offers to the user's the following services:
 * <p>- Update their display name
 * <p>- Update their email
 * <p>- Update their password
 *
 * @author Charalambos Christou
 * @version 2.0 11/12/20
 */
public class AccountRepository {

    @Nullable
    final private FirebaseUser mFirebaseUser;

    /**
     * Initializes the AccountRepository's firebase user
     * with the specified argument.
     *
     * @param firebaseUser The current instance of FirebaseUser
     */
    public AccountRepository(@Nullable FirebaseUser firebaseUser) {
        this.mFirebaseUser = firebaseUser;
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
}
