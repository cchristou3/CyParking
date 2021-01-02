package io.github.cchristou3.CyParking.data.repository;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
 * @version 3.0 28/12/20
 */
public class AccountRepository {


    private final FirebaseUser mFirebaseUser;

    /**
     * Initializes the AccountRepository's firebase user
     * based on the specified argument.
     *
     * @param firebaseAuth The current instance of FirebaseAuth
     */
    public AccountRepository(@NonNull FirebaseAuth firebaseAuth) {
        this.mFirebaseUser = firebaseAuth.getCurrentUser();
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
    @NonNull
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
    @NonNull
    public Task<Void> updateEmail(String newEmail) {
        return mFirebaseUser.updateEmail(newEmail);
    }

    /**
     * Updates the user's password with the specified parameter.
     *
     * @param newPassword The user's new password.
     * @return A Task<Void> object
     */
    @NonNull
    public Task<Void> updatePassword(String newPassword) {
        return mFirebaseUser.updatePassword(newPassword);
    }
}
