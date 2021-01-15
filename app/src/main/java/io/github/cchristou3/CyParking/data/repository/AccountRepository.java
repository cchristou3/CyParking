package io.github.cchristou3.CyParking.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.data.model.user.LoggedInUser;

import static io.github.cchristou3.CyParking.data.repository.RepositoryData.FEEDBACK;
import static io.github.cchristou3.CyParking.data.repository.RepositoryData.USERS;
import static io.github.cchristou3.CyParking.data.repository.RepositoryData.USER_DISPLAY_NAME;
import static io.github.cchristou3.CyParking.data.repository.RepositoryData.USER_EMAIL;
import static io.github.cchristou3.CyParking.ui.host.MainHostActivity.TAG;

/**
 * Purpose: <p>Class that offers to the user the following services:
 * <p>- Update their display name
 * <p>- Update their email
 * <p>- Update their password
 *
 * @author Charalambos Christou
 * @version 4.0 12/01/21
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
        return FirebaseFirestore.getInstance().collection(USERS)
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
     * TODO: Migrate into a cloud function
     * src: https://stackoverflow.com/questions/53836195/firebase-functions-update-all-documents-inside-a-collection
     *
     * @param newEmail The new email of the user.
     */
    public void updateUserEmail(String userId, String oldEmail, String newEmail) {
        updateEmailFromFeedbackNode(oldEmail, newEmail);
        updateEmailFromUsersNode(userId, newEmail);
    }

    /**
     * Updates all the feedback documents with the specified old email with
     * the new email address.
     *
     * @param oldEmail The current email address of the user.
     * @param newEmail The new email address of the user.
     */
    private void updateEmailFromFeedbackNode(String oldEmail, String newEmail) {
        // Update the email from the FEEDBACK node
        FirebaseFirestore.getInstance().collection(FEEDBACK)
                .whereEqualTo(USER_EMAIL, oldEmail).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Iterate all documents (feedback messages) that contain the old email
                for (DocumentSnapshot document :
                        task.getResult().getDocuments()) {
                    // Update with the new one.
                    document.getReference().update(USER_EMAIL, newEmail);
                }
            }
        });
    }

    /**
     * Updates the user's document with
     * the new email address.
     *
     * @param userId   The id of the user's document (also his id).
     * @param newEmail The new email address of the user.
     */
    private void updateEmailFromUsersNode(String userId, String newEmail) {
        // Update the email from the USERS node
        FirebaseFirestore.getInstance().collection(USERS)
                .document(userId)
                .update(USER_EMAIL, newEmail);
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
