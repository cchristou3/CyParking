package io.github.cchristou3.CyParking.apiClient.remote.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.apiClient.model.data.user.LoggedInUser;

/**
 * Purpose: <p>Class that offers to the user the following services:
 * <p>- Update their display name
 * <p>- Update their email
 * <p>- Update their password
 *
 * @author Charalambos Christou
 * @version 6.0 22/04/21
 */
public class AccountRepository implements DataSourceRepository.UserHandler, DataSourceRepository.CloudFunctionCaller {

    private final FirebaseUser mFirebaseUser;
    private static final String TAG = AccountRepository.class.getCanonicalName();

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
     * Updates the user's display name with the specified parameter.
     *
     * @param newDisplayName The user's new name.
     * @param user           The current instance of {@link LoggedInUser}.
     * @return A Task<Void> object
     */
    @NonNull
    public Task<Void> updateDisplayName(String newDisplayName, LoggedInUser user) {
        return mFirebaseUser.updateProfile(new UserProfileChangeRequest.Builder()
                .setDisplayName(newDisplayName)
                .build())
                .continueWithTask(task -> {
                    Log.d(TAG, "New display name then: " + task.getResult());
                    if (task.isSuccessful()) {
                        return updateUserDisplayName(user.getUserId(), newDisplayName);
                    }
                    return null;
                });
    }

    /**
     * Updates the user's display name in the database.
     *
     * @param newDisplayName The new display name of the user.
     * @return A task to be handle by the view.
     */
    @NotNull
    public Task<Void> updateUserDisplayName(String userId, String newDisplayName) {
        return getUserRef()
                .document(userId)
                .update(USER_DISPLAY_NAME, newDisplayName);
    }

    /**
     * Updates the user's email with the specified parameter.
     *
     * @param newEmail The user's new email.
     * @param user     The current instance of {@link LoggedInUser}.
     * @return The Task<Void> object to be handled by the view.
     */
    @NonNull
    public Task<Void> updateEmail(String newEmail, LoggedInUser user) {
        return mFirebaseUser.updateEmail(newEmail)
                .continueWithTask(task -> {
                    Log.d(TAG, "then: " + task.getResult());
                    if (task.isSuccessful()) {
                        updateUserEmail(user.getUserId(), user.getEmail(), newEmail);
                        return task;
                    }
                    return null;
                });
    }

    /**
     * Updates the user's email in the database.
     *
     * @param newEmail The new email of the user
     * @param oldEmail The old email of the user
     * @param userId   The user id
     */
    public void updateUserEmail(String userId, String oldEmail, String newEmail) {
        callUpdateEmailFunction(userId, oldEmail, newEmail);
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
