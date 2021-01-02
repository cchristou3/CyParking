package io.github.cchristou3.CyParking.data.repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.github.cchristou3.CyParking.data.manager.SharedPreferencesManager;
import io.github.cchristou3.CyParking.data.model.user.LoggedInUser;
import io.github.cchristou3.CyParking.data.pojo.form.login.LoginResult;
import io.github.cchristou3.CyParking.ui.host.MainHostActivity;

/**
 * Purpose: <p>Class that handles authentication w/ login credentials and retrieves user information.
 * Further, it requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.</p>
 *
 * @author Charalambos Christou
 * @version 2.0 15/12/20
 */
public class AuthenticatorRepository {

    // Firebase Firestore paths (nodes)
    private static final String USERS = "users";
    // Constant data members
    private static final String USER_DISPLAY_NAME = "displayName";
    private static final String USER_EMAIL = "email";

    // Non-constant data members
    private static volatile AuthenticatorRepository INSTANCE;
    private final FirebaseAuth mDataSource;

    /**
     * Private Constructor : singleton access.
     * Initializes the Repository's {@link FirebaseAuth}
     * member with the given argument.
     *
     * @param dataSource The data source to be used as an Auth API.
     */
    private AuthenticatorRepository(@NotNull FirebaseAuth dataSource) {
        this.mDataSource = dataSource;
    }

    /**
     * Returns a new instance of the data source, if there isn't already one.
     * Otherwise returns the existing instance.
     *
     * @param dataSource A FirebaseAuth object
     * @return A LoginRepository instance
     */
    public static AuthenticatorRepository getInstance(FirebaseAuth dataSource) {
        if (INSTANCE == null) {
            INSTANCE = new AuthenticatorRepository(dataSource);
        }
        return INSTANCE;
    }

    /**
     * Re-authenticates the user with the given credentials.
     *
     * @param credentials The user's credentials
     * @return A task to be handled by the view.
     */
    @NotNull
    public Task<AuthResult> reauthenticateUser(String credentials) {
        return mDataSource.getCurrentUser().reauthenticateAndRetrieveData(EmailAuthProvider
                .getCredential(mDataSource.getCurrentUser().getEmail(), credentials));
    }

    /**
     * Sign the user in with the given username (email) and password.
     * Once successfully logged in, the user's roles are accessed locally
     * or if not found, on the server, and then the loginResult is updated
     * which itself triggers a Ui update.
     *
     * @param username    A string which corresponds to the email of the user.
     * @param password    A string which corresponds to the password of the user.
     * @param loginResult A MutableLiveData which handles the authentication result.
     */
    public void login(Context context, String username, String password, MutableLiveData<LoginResult> loginResult) {
        // handle login
        // Handle loggedInUser authentication
        mDataSource.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(loginTask -> {
                    // Check whether an exception occurred
                    if (loginTask.getException() != null) {
                        loginResult.setValue(new LoginResult(loginTask.getException().getMessage()));
                        return;
                    }

                    // If the user successfully signed in
                    if (loginTask.isSuccessful() && loginTask.getResult().getUser() != null) {
                        // Access the data related to user locally via the SharedPreferencesManager
                        // using his/her Uid as the key
                        List<String> listOfRoles = new SharedPreferencesManager(context.getApplicationContext())
                                .getValue(loginTask.getResult().getUser().getUid());

                        // If the user has data stored locally
                        if (!(listOfRoles == null || listOfRoles.isEmpty())) {
                            // then
                            updateLoginResultWithUser(new LoggedInUser(loginTask.getResult().getUser(), listOfRoles), loginResult);
                        } else {
                            // Otherwise, the user does not have data stored locally (cache was cleared or changed device).
                            // Fetch the user's data from the server
                            updateLoginResultWithFetchedData(loginTask.getResult().getUser(), loginResult);
                        }
                    }
                });
    }

    /**
     * Fetch the user's data from the Firestore database and
     * update the loginResult accordingly.
     *
     * @param currentFirebaseUser The current FirebaseUser instance.
     * @param loginResult         A MutableLiveData which handles the authentication result.
     */
    private void updateLoginResultWithFetchedData(@NotNull FirebaseUser currentFirebaseUser, MutableLiveData<LoginResult> loginResult) {
        AuthenticatorRepository.this.getUser(currentFirebaseUser)
                .addOnCompleteListener(retrieveUserDataTask -> {
                    // The task was unsuccessful, the user has no data stored on the server
                    // and an exception occurred
                    if (retrieveUserDataTask.getException() != null &&
                            !(retrieveUserDataTask.isSuccessful()
                                    && retrieveUserDataTask.getResult().getData() != null)) {
                        updateLoginResultWithUser(new LoggedInUser(currentFirebaseUser, null), loginResult);
                        return;
                        // TODO: Ask the user to re-enter his credentials or to create a new account
                    }
                    // The task was successful and the user has data stored on the server
                    LoggedInUser user = null;
                    try {
                        user = retrieveUserDataTask.getResult().toObject(LoggedInUser.class);
                    } catch (NullPointerException ignored) {
                    }
                    // A user with roles will be treated as a un-logged-in user.
                    List<String> roles = (user != null) ? user.getRoles() : null;
                    updateLoginResultWithUser(new LoggedInUser(currentFirebaseUser, roles), loginResult);
                });
    }

    /**
     * Updates the value of loginResult
     * based on the given LoggedInUser argument.
     *
     * @param user        A {@link LoggedInUser} object.
     * @param loginResult A MutableLiveData which handles the authentication result.
     */
    private void updateLoginResultWithUser(@NotNull LoggedInUser user, @NotNull MutableLiveData<LoginResult> loginResult) {
        // Trigger loginResult observer update
        loginResult.setValue(new LoginResult(user)); // Roles
    }

    /**
     * Sign the user up with the given username (email) and password
     *
     * @param username    A string which corresponds to the email of the user.
     * @param password    A string which corresponds to the password of the user.
     * @param loginResult A MutableLiveData which handles the authentication result.
     * @param isUser      true if the user selected the checkbox which corresponds to the user. Otherwise, false.
     * @param isOperator  true if the user selected the checkbox which corresponds to the operator. Otherwise, false.
     * @param context     The context of the current tab.
     */
    public void register(String username, String password, MutableLiveData<LoginResult> loginResult,
                         boolean isUser, boolean isOperator, Context context) throws IllegalArgumentException {
        if (!isUser && !isOperator)
            throw new IllegalArgumentException("At least one of the given roles must be selected!");
        // handle registration. A registered User is also a loggedInUser
        // Handle loggedInUser authentication
        mDataSource.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(task -> {
                    // Check whether an exception occurred
                    if (task.getException() != null) {
                        loginResult.setValue(new LoginResult(task.getException().getMessage()));
                        return;
                    }

                    // If the user successfully registered
                    if (task.isSuccessful() && task.getResult().getUser() != null) {
                        // Create a list to store the user's selected role(s)
                        List<String> listOfRoles = new ArrayList<>();
                        if (isUser) listOfRoles.add(MainHostActivity.USER);
                        if (isOperator) listOfRoles.add(MainHostActivity.OPERATOR);

                        // Save the user's roles locally via SharedPreferences
                        // Each user in the database has a unique Uid. Thus, to be used as the key.
                        new SharedPreferencesManager(context.getApplicationContext())
                                .setValue(task.getResult().getUser().getUid(), listOfRoles);

                        final LoggedInUser loggedInUser = new LoggedInUser(task.getResult().getUser(), listOfRoles);
                        // Initialize the Repository's LoggedInUser instance
                        // and trigger loginResult observer update
                        updateLoginResultWithUser(loggedInUser, loginResult);

                        // Save the user's data to the server
                        addUser(loggedInUser);
                    }
                });
    }

    /**
     * Store information about the user to the Firestore database.
     *
     * @param user The current LoggedInUser instance.
     */
    private void addUser(@NotNull LoggedInUser user) {
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
     * Updates the user's display name in the database.
     *
     * @param newDisplayName The new display name of the user.
     * @return A task to be handle by the view.
     */
    public Task<Void> updateUserDisplayName(String userId, String newDisplayName) {
        return FirebaseFirestore.getInstance().collection(USERS)
                .document(userId)
                .update(USER_DISPLAY_NAME, newDisplayName);
    }

    /**
     * Updates the user's email in the database.
     * TODO: Migrate into a cloud function
     * src: https://stackoverflow.com/questions/53836195/firebase-functions-update-all-documents-inside-a-collection
     *
     * @param newEmail The new email of the user.
     * @return A task to be handle by the view.
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
     * @return A {@link Task<QuerySnapshot>} instance to be handled by the view.
     */
    @NotNull
    private Task<QuerySnapshot> updateEmailFromFeedbackNode(String oldEmail, String newEmail) {
        // Update the email from the FEEDBACK node
        return FirebaseFirestore.getInstance().collection(FeedbackRepository.FEEDBACK)
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

    @NotNull
    private Task<Void> updateEmailFromUsersNode(String userId, String newEmail) {
        // Update the email from the USERS node
        return FirebaseFirestore.getInstance().collection(USERS)
                .document(userId)
                .update(USER_EMAIL, newEmail);
    }


    /**
     * Signs the user out.
     */
    public void signOut() {
        mDataSource.signOut();
    }
}