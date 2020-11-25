package io.github.cchristou3.CyParking.view.data.repository;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import org.jetbrains.annotations.Nullable;

public class AccountRepository {

    @NonNull
    final private FirebaseUser firebaseUser;

    public AccountRepository(@NonNull FirebaseUser firebaseUser) {
        this.firebaseUser = firebaseUser;
    }

    @NonNull
    public FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }

    @Nullable
    public Task<Void> updateDisplayName(String newDisplayName) {
        return firebaseUser.updateProfile(new UserProfileChangeRequest.Builder()
                .setDisplayName(newDisplayName)
                .build());
    }

    @Nullable
    public Task<Void> updateEmail(String newEmail) {
        return firebaseUser.updateEmail(newEmail);
    }

    @Nullable
    public Task<Void> updatePassword(String newPassword) {
        return firebaseUser.updatePassword(newPassword);
    }
}
