package io.github.cchristou3.CyParking.apiClient.model.data.user;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Exclude;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Purpose: <p>Data class that captures user information for logged in users retrieved from AuthenticatorRepository</p>
 * Also, exposes authenticated user details to the UI.
 *
 * @author Charalambos Christou
 * @version 3.0 10/02/21
 */
public class LoggedInUser implements Parcelable {

    public static final Creator<LoggedInUser> CREATOR = new Creator<LoggedInUser>() {
        @Override
        public LoggedInUser createFromParcel(Parcel in) {
            return new LoggedInUser(in);
        }

        @Override
        public LoggedInUser[] newArray(int size) {
            return new LoggedInUser[size];
        }
    };

    public static final String OPERATOR = "Operator";
    private String userId;
    private List<String> roles; // Note: HashSet is more convenient but Firebase does not support it.
    private String displayName;
    private String email;

    /**
     * No-argument constructor required for deserialization
     */
    public LoggedInUser() {
    }


    /**
     * Public Constructor.
     * Initialize all the attributes of the class with the given arguments.
     *
     * @param firebaseUser The FirebaseUser instance to have its info extracted.
     * @param roles        A list of the user's roles.
     */
    public LoggedInUser(@NotNull FirebaseUser firebaseUser, List<String> roles) {
        this.userId = firebaseUser.getUid();
        this.displayName = firebaseUser.getDisplayName();
        this.email = firebaseUser.getEmail();
        this.roles = roles;
    }

    /**
     * Constructor to be used by the Parcelable interface
     * to initialize the LoggedInUser instance with the specified
     * {@link Parcel}.
     *
     * @param in Contains the contents of the LoggedInUser instance.
     */
    protected LoggedInUser(@NotNull Parcel in) {
        userId = in.readString();
        displayName = in.readString();
        email = in.readString();
        roles = in.createStringArrayList();
    }

    /**
     * Getters and Setters
     */
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public LoggedInUser setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public LoggedInUser setEmail(String email) {
        this.email = email;
        return this;
    }


    public List<String> getRoles() {
        return roles;
    }

    /**
     * Checks whether the instance is a {@link #OPERATOR} of role.
     *
     * @return True if the user is. Otherwise, false.
     */
    @Exclude
    public boolean isOperator() {
        return isRole(OPERATOR);
    }

    /**
     * Checks whether the instance is the given role.
     *
     * @return True if the user is. Otherwise, false.
     */
    @Exclude
    private boolean isRole(String role) {
        return (roles != null) && roles.contains(role);
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(@NotNull Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(displayName);
        dest.writeString(email);
        dest.writeStringList(roles);
    }
}