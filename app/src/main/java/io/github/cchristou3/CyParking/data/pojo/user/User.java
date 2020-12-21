package io.github.cchristou3.CyParking.data.pojo.user;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Purpose: <p>POJO to be used to transfer and receive data
 * via activities and HTTPS requests. Used to keep track of the user's
 * data.</p>
 *
 * @author Charalambos Christou
 * @version 3.0 13/12/20
 */
public class User implements Parcelable {

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
    private List<String> roles;
    @Nullable
    private String carNumberPlate;

    public User() {
    }

    private String userID;

    public User(String userID, List<String> roles, @Nullable String carNumberPlate) {
        this.roles = roles;
        this.carNumberPlate = carNumberPlate;
        this.userID = userID;
    }


    protected User(@NotNull Parcel in) {
        // Read the size of the passed hash set
        final int sizeOfSet = in.readInt();
        // Create a hash set
        List<String> setToBeRead = new ArrayList<>();
        // Traverse through the parcel
        for (int i = 0; i < sizeOfSet; i++) {
            // Access the passed hash sets's value
            final String value = in.readString();
            // Add the value to the set
            setToBeRead.add(value);
        }
        // Set the roles to the values of the hash set
        setRoles(setToBeRead);
        setToBeRead.clear();

        carNumberPlate = in.readString();
        userID = in.readString();
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
        // set up roles of the hash set
        List<String> rolesList = getRoles();
        dest.writeInt(rolesList.size()); // store the size of the set

        for (String value : rolesList) {
            dest.writeString(value); // store the value
        }

        dest.writeString(carNumberPlate);
        dest.writeString(userID);
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> mRoles) {
        this.roles = mRoles;
    }

    @Nullable
    public String getCarNumberPlate() {
        return carNumberPlate;
    }

    public void setCarNumberPlate(@Nullable String mCarNumberPlate) {
        this.carNumberPlate = mCarNumberPlate;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String mUserID) {
        this.userID = mUserID;
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
}
