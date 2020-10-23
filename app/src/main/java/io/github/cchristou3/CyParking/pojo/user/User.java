package io.github.cchristou3.CyParking.pojo.user;

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

    public String[] getmRoles() {
        return mRoles;
    }

    public void setmRoles(String[] mRoles) {
        this.mRoles = mRoles;
    }

    public String getmCarNumberPlate() {
        return mCarNumberPlate;
    }

    public void setmCarNumberPlate(String mCarNumberPlate) {
        this.mCarNumberPlate = mCarNumberPlate;
    }

    public int getmUserID() {
        return mUserID;
    }

    public void setmUserID(int mUserID) {
        this.mUserID = mUserID;
    }
}
