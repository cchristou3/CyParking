package io.github.cchristou3.CyParking.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.github.cchristou3.CyParking.data.manager.SharedPreferencesManager;
import io.github.cchristou3.CyParking.data.model.user.LoggedInUser;
import io.github.cchristou3.CyParking.ui.host.GlobalStateViewModel;

import static io.github.cchristou3.CyParking.data.repository.RepositoryData.USERS;
import static io.github.cchristou3.CyParking.ui.host.MainHostActivity.TAG;

/**
 * Purpose: <p>Class that handles authentication w/ login credentials and retrieves user information.
 * Further, it requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status (TODO:) and user credentials information.</p>
 *
 * @author Charalambos Christou
 * @version 4.0 14/01/21
 */
public class AuthenticatorRepository {

    private final FirebaseAuth mDataSource;

    /**
     * Private Constructor : singleton access.
     * Initializes the Repository's {@link FirebaseAuth}
     * member with its current available instance.
     */
    public AuthenticatorRepository() {
        this.mDataSource = FirebaseAuth.getInstance();
    }

    /**
     * Re-authenticates the user with the given credentials.
     *
     * @param credentials The user's credentials
     * @return A task to be handled by the view.
     * @throws IllegalStateException if method gets invoked when there is no user
     *                               currently signed in.
     */
    @NotNull
    public Task<AuthResult> reauthenticateUser(String credentials) {
        try {
            return mDataSource.getCurrentUser().reauthenticateAndRetrieveData(EmailAuthProvider
                    .getCredential(mDataSource.getCurrentUser().getEmail(), credentials));
        } catch (NullPointerException exception) {
            throw new IllegalStateException("The user must be signed in to perform re-authentication.");
        }
    }

    /**
     * Sign the user in with the given email (email) and password.
     * Once successfully logged in, the user's roles are accessed locally
     * or if not found, on the server, and then the loginResult is updated
     * which itself triggers a Ui update.
     *
     * @param email    A string that corresponds to the email of the user.
     * @param password A string that corresponds to the password of the user.
     * @return A {@link Task<AuthResult>} to be handled by the caller.
     */
    public Task<AuthResult> login(String email, String password) {
        // handle login
        // Handle loggedInUser authentication
        return mDataSource.signInWithEmailAndPassword(email, password);
    }

    /**
     * Sign the user up with the given email (email) and password
     *
     * @param email      A string which corresponds to the email of the user.
     * @param password   A string which corresponds to the password of the user.
     * @param isUser     true if the user selected the checkbox which corresponds to the user. Otherwise, false.
     * @param isOperator true if the user selected the checkbox which corresponds to the operator. Otherwise, false.
     * @return A {@link Task<AuthResult>} to be handled by the caller.
     */
    public Task<AuthResult> register(String email, String password,
                                     boolean isUser, boolean isOperator) throws IllegalArgumentException {
        if (!isUser && !isOperator)
            throw new IllegalArgumentException("At least one of the given roles must be selected!");
        // handle registration. A registered User is also a loggedInUser
        // Handle loggedInUser authentication
        return mDataSource.createUserWithEmailAndPassword(email, password);
    }

    /**
     * Store information about the user to the Firestore database.
     *
     * @param user The current LoggedInUser instance.
     */
    public void addUser(@NotNull LoggedInUser user) {
        // Map users to a document via their autogenerated uid
        FirebaseFirestore.getInstance().collection(USERS)
                .document(user.getUserId())
                .set(user);
    }

    /**
     * Retrieve the user's data from the database.
     *
     * @param currentUser The current FirebaseUser instance.
     * @return A Task<DocumentSnapshot> instance to be handled by the view.
     */
    public Task<DocumentSnapshot> getUser(@NotNull FirebaseUser currentUser) {
        return FirebaseFirestore.getInstance().collection(USERS)
                .document(currentUser.getUid())
                .get();
    }

    /**
     * Initially retrieves the user's data locally. If not found,
     * fetches them from the backend.
     * The events are handled via a {@link UserDataHandler} instance
     * that is provided by the caller.
     *
     * @param context The context to use.
     * @param user    The current FirebaseUser instance provided either by
     *                {@link FirebaseAuth#getCurrentUser()} or {@link AuthResult#getUser()}.
     * @param handler The handler of the user's data both.
     * @see GlobalStateViewModel#getUserInfo(Context, FirebaseUser)
     * @see io.github.cchristou3.CyParking.ui.user.login.AuthenticatorViewModel#login(Context, String, String)
     */
    public void getUserInfo(@NonNull Context context, @Nullable FirebaseUser user, @NonNull UserDataHandler handler) {
        if (user == null) return; // If user not set (logged in), terminate the method.
        // Otherwise,
        // Access the user's role locally via the SharedPreferences using the
        // user's id as the key
        List<String> roles = new SharedPreferencesManager(context.getApplicationContext()).getValue(user.getUid());
        // Local data is found
        if (!(roles == null || roles.isEmpty())) {
            handler.onLocalData(roles);
        } else {
            // fetch user's data from the database
            this.getUser(user)
                    .addOnCompleteListener(task -> {
                        Log.d(TAG, "getUserInfo: data found on server");
                        if (task.getException() != null) {
                            handler.onRemoteDataFailure(task.getException());
                            return;
                        }
                        if (task.isSuccessful()) {
                            handler.onRemoteDataSuccess(task);
                        }
                    });
        }
    }

    /**
     * Signs the user out.
     */
    public void signOut() {
        mDataSource.signOut();
    }

    /**
     * Purpose: Provide callbacks to be used when retrieving the user's data.
     * Initially the data is looked up locally. If no data was found locally,
     * then the user's data are looked up from the server.
     *
     * @see AuthenticatorRepository#getUserInfo(Context, FirebaseUser, UserDataHandler)
     */
    public interface UserDataHandler {
        /**
         * Invoked when the user's data was found locally.
         *
         * @param roles The roles of the user that are stored locally.
         */
        void onLocalData(List<String> roles);

        /**
         * Invoked when the user's data was found on the server's
         * database.
         *
         * @param task The {@link Task} instance that contains the user's data.
         */
        void onRemoteDataSuccess(Task<DocumentSnapshot> task);

        /**
         * Invoked when an error occurred when retrieving the user's
         * data from the server.
         *
         * @param exception The error that caused the requests's failure.
         */
        void onRemoteDataFailure(Exception exception);
    }
}