package io.github.cchristou3.CyParking.view.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

import io.github.cchristou3.CyParking.view.data.model.LoggedInUser;
import io.github.cchristou3.CyParking.view.ui.login.LoggedInUserView;
import io.github.cchristou3.CyParking.view.ui.login.LoginResult;

/**
 * purpose: Class that handles authentication w/ login credentials and retrieves user information.
 * Further, it requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 *
 * @author Charalambos Christou
 * @version 1.0 1/11/20
 */
public class LoginRepository {

    private static final String APPLICATION_PREFERENCES_KEY = "CyParkingPreferences";
    private static volatile LoginRepository instance;
    private final FirebaseAuth dataSource;
    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser user = null;

    // private constructor : singleton access
    private LoginRepository(FirebaseAuth dataSource) {
        this.dataSource = dataSource;

    }

    /**
     * Returns a new instance of the data source, if there isn't already one.
     * Otherwise returns the existing instance.
     *
     * @param dataSource A FirebaseAuth object
     * @return A LoginRepository instance
     */
    public static LoginRepository getInstance(FirebaseAuth dataSource) {
        if (instance == null) {
            instance = new LoginRepository(dataSource);
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
            if (task.isSuccessful()) {
                FirebaseUser user = dataSource.getCurrentUser();
                LoggedInUser loggedInUser = new LoggedInUser(user.getEmail(), user.getUid());
                setLoggedInUser(loggedInUser);

                // Save the user's roles locally using SharedPreferences
                // TODO: also add them to the database
                // If user did not pick any of the roles and he passed the data validation
                // it means that he must be logging in. Otherwise, he must be registering.
                if (isUser || isOperator) { // User is registering
                    SharedPreferences.Editor editor = context.getApplicationContext()
                            .getSharedPreferences(APPLICATION_PREFERENCES_KEY, Context.MODE_PRIVATE)
                            .edit();
                    Set<String> setOfRoles = new HashSet<>();
                    if (isUser) setOfRoles.add("User");
                    if (isOperator) setOfRoles.add("Operator");

                    // Each user in the database has a unique Uid
                    editor.putStringSet(user.getUid(), setOfRoles);
                    editor.apply();
                }
                loginResult.setValue(new LoginResult(new LoggedInUserView(
                        user.getEmail(), isUser, isOperator)));
            } else {
                loginResult.setValue(new LoginResult(task.getException().getMessage()));
            }
        };
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
        // handle registration. A registered User is also a loggedInUser
        // Handle loggedInUser authentication
        dataSource.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(getAuthResultOnCompleteListener(loginResult, isUser, isOperator, context));
    }
}