package io.github.cchristou3.CyParking.view.data.pojo.parking;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Purpose: <p>POJO to be used to transfer and receive data
 * via activities / fragments and HTTPS requests (through GSON).</p>
 *
 * @author Charalambos Christou
 * @version 1.0 30/10/20
 */
public class PrivateParkingResultSet implements Parcelable {

    public static final Creator<PrivateParkingResultSet> CREATOR = new Creator<PrivateParkingResultSet>() {
        @Override
        public PrivateParkingResultSet createFromParcel(Parcel in) {
            return new PrivateParkingResultSet(in);
        }

        @Override
        public PrivateParkingResultSet[] newArray(int size) {
            return new PrivateParkingResultSet[size];
        }
    };
    @SerializedName("id")
    private String documentID;
    @SerializedName("parking")
    private PrivateParking parking;

    public PrivateParkingResultSet() {
    } //  no-argument constructor to be used by GSON

    protected PrivateParkingResultSet(Parcel in) {
        documentID = in.readString();
        parking = in.readParcelable(PrivateParking.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(documentID);
        dest.writeParcelable(parking, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getDocumentID() {
        return documentID;
    }

    public PrivateParking getParking() {
        return parking;
    }
}
