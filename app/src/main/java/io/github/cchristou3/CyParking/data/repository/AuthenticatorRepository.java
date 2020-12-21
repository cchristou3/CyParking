package io.github.cchristou3.CyParking.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.github.cchristou3.CyParking.MainHostActivity;
import io.github.cchristou3.CyParking.data.manager.SharedPreferencesManager;
import io.github.cchristou3.CyParking.data.pojo.user.LoggedInUser;
import io.github.cchristou3.CyParking.data.pojo.user.User;
import io.github.cchristou3.CyParking.data.pojo.user.login.LoggedInUserView;
import io.github.cchristou3.CyParking.data.pojo.user.login.LoginResult;

/**
 * Purpose: <p>Class that handles authentication w/ login credentials and retrieves user information.
 * Further, it requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.</p>
 *
 * @author Charalambos Christou
 * @version 2.0 15/12/20
 */
public class AuthenticatorRepository {

    // Constant data members
    private static final String USERS = "users";

    // Non-constant data members
    private static volatile AuthenticatorRepository instance;
    private final FirebaseAuth dataSource;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser user = null;

    /**
     * Private Constructor : singleton access.
     * Initializes the Repository's {@link FirebaseAuth}
     * member with the given argument.
     *
     * @param dataSource The data source to be used as an Auth API.
     */
    private AuthenticatorRepository(FirebaseAuth dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Returns a new instance of the data source, if there isn't already one.
     * Otherwise returns the existing instance.
     *
     * @param dataSource A FirebaseAuth object
     * @return A LoginRepository instance
     */
    public static AuthenticatorRepository getInstance(FirebaseAuth dataSource) {
        if (instance == null) {
            instance = new AuthenticatorRepository(dataSource);
        }
        return instance;
    }

    /**
     * Creates a new instance of OnCompleteListener<AuthResult>. When triggered checks for the result.
     * If successful, it accesses the contents of the FirebaseUser and creates a new instance of LoggedInUser.
     * The value of the loginResult is then updated with the new LoginResult and its corresponding LoggedInUserView value
     * which is based on the previously created LoggedInUser instance.
     * Further, if the Context is NOT  null, then the user is in the process of registration. Thus, if successful, the user's
     * roles are stored locally.
     * If the result is failure, then the LoginResult is updated with an error flag.
     *
     * @param loginResult MutableLiveData which handles the result of the authentication.
     * @param isUser      true if the user selected the checkbox which corresponds to the user. Otherwise, false.
     * @param isOperator  true if the user selected the checkbox which corresponds to the operator. Otherwise, false.
     * @param context     The context of the current tab.
     * @return An OnCompleteListener<AuthResult> instance.
     */
    @NotNull
    @Contract(pure = true)
    private OnCompleteListener<AuthResult> getAuthResultOnCompleteListener(MutableLiveData<LoginResult> loginResult,
                                                                           boolean isUser, boolean isOperator,
                                                                           @Nullable Context context) {
        return task -> {
            Log.d(MainHostActivity.TAG, "getAuthResultOnCompleteListener: Invoked!");
            // Check whether an exception occurred
            if (task.getException() != null) {
                loginResult.setValue(new LoginResult(task.getException().getMessage()));
                return;
            }

            if (task.isSuccessful() && dataSource.getCurrentUser() != null) {
                LoggedInUser loggedInUser = new LoggedInUser(dataSource.getCurrentUser().getEmail(),
                        dataSource.getCurrentUser().getUid());
                setLoggedInUser(loggedInUser);


                // If user did not pick any of the roles and he passed the data validation
                // it means that he must be logging in. Otherwise, he must be registering.
                if ((isUser || isOperator) && context != null) { // User is registering
                    SharedPreferencesManager preferencesManager = new SharedPreferencesManager(context);

                    List<String> setOfRoles = new ArrayList<>();
                    if (isUser) setOfRoles.add(MainHostActivity.USER);
                    if (isOperator) setOfRoles.add(MainHostActivity.OPERATOR);

                    // Save the user's roles both locally via SharedPreferences
                    // Each user in the database has a unique Uid. Perfect attribute to be used as the key.
                    preferencesManager.setValue(dataSource.getCurrentUser().getUid(), setOfRoles);
                    // Save the user's data to the server
                    addUser(dataSource.getCurrentUser(), setOfRoles);
                }
                loginResult.setValue(new LoginResult(new LoggedInUserView(
                        dataSource.getCurrentUser().getEmail(), isUser, isOperator)));
            }
        };
    }

    /**
     * Sign the user in with the given username (email) and password
     *
     * @param username    A string which corresponds to the email of the user.
     * @param password    A string which corresponds to the password of the user.
     * @param loginResult A MutableLiveData which handles the authentication result.
     */
    public void login(String username, String password, MutableLiveData<LoginResult> loginResult) {
        // handle login
        // Handle loggedInUser authentication
        dataSource.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(getAuthResultOnCompleteListener(loginResult, false, false, null));
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
                         boolean isUser, boolean isOperator, Context context) {
        if (!isUser && !isOperator)
            throw new IllegalArgumentException("At least one of the given roles must be selected!");
        // handle registration. A registered User is also a loggedInUser
        // Handle loggedInUser authentication
        dataSource.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(getAuthResultOnCompleteListener(loginResult, isUser, isOperator, context));
    }

    /**
     * Stores information about the user to the Firestore database.
     *
     * @param currentUser The current FirebaseUser instance.
     * @param setOfRoles  The roles associated with the user.
     */
    private void addUser(@NotNull FirebaseUser currentUser, List<String> setOfRoles) {
        // Map users to a document via their autogenerated uid
        FirebaseFirestore.getInstance().collection(USERS)
                .document(currentUser.getUid())
                .set(new User(currentUser.getUid(), setOfRoles, null));
        // For now carNumberPlate is passed as null. TODO: Add additional info
    }

    public Task<DocumentSnapshot> getUser(@NotNull FirebaseUser currentUser) {
        return FirebaseFirestore.getInstance().collection(USERS)
                .document(currentUser.getUid())
                .get();
    }

    /**
     * @return true if the is set (logged in). Otherwise, false.
     */
    public boolean isLoggedIn() {
        return user != null;
    }

    /**
     * Unset the user and signs him/her out
     */
    public void logout() {
        user = null;
        dataSource.signOut();
    }

    /**
     * Sets the user
     *
     * @param user An instance of LoggedInUser
     */
    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

}