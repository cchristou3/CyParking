package io.github.cchristou3.CyParking.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import io.github.cchristou3.CyParking.data.model.user.LoggedInUser;

import static io.github.cchristou3.CyParking.data.repository.RepositoryData.USERS;
import static io.github.cchristou3.CyParking.data.repository.RepositoryData.USER_DISPLAY_NAME;
import static io.github.cchristou3.CyParking.ui.views.host.MainHostActivity.TAG;

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
     *
     * @param newEmail The new email of the user.
     */
    public void updateUserEmail(String userId, String oldEmail, String newEmail) {
        FirebaseFunctions.getInstance()
                .getHttpsCallable("updateEmail")
                .call(new HashMap<String, String>() {{
                    put("newEmail", newEmail);
                    put("oldEmail", oldEmail);
                    put("userId", userId);
                }});
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
