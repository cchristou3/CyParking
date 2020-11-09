package io.github.cchristou3.CyParking.view.data.pojo.user;

/**
 * Purpose: <p>POJO to be used to transfer and receive data
 * via activities and HTTPS requests. Used to keep track of the user's
 * data.</p>
 *
 * @author Charalambos Christou
 * @version 2.0 29/10/20
 */
public class User {
    private String[] mRoles;
    private String mCarNumberPlate;
    private int mUserID;

    public User() {
    }

    public User(String[] mRoles, String mCarNumberPlate, int mUserID) {
        this.mRoles = mRoles;
        this.mCarNumberPlate = mCarNumberPlate;
        this.mUserID = mUserID;
    }

    public String[] getRoles() {
        return mRoles;
    }

    public void setRoles(String[] mRoles) {
        this.mRoles = mRoles;
    }

    public String getCarNumberPlate() {
        return mCarNumberPlate;
    }

    public void setCarNumberPlate(String mCarNumberPlate) {
        this.mCarNumberPlate = mCarNumberPlate;
    }

    public int getUserID() {
        return mUserID;
    }

    public void setUserID(int mUserID) {
        this.mUserID = mUserID;
    }
}
