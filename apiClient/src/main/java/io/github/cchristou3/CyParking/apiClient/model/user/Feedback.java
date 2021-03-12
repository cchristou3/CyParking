package io.github.cchristou3.CyParking.apiClient.model.user;

import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;

/**
 * Purpose: Encapsulate attributes related to feedback.
 * <p>POJO to be used to transfer and receive data
 * * via activities / fragments and HTTP requests.
 * Referenced in FeedbackFragment.
 *
 * @author Charalambos Christou
 * @version 1.0 30/12/20
 */
public class Feedback implements Parcelable {

    public static final Creator<Feedback> CREATOR = new Creator<Feedback>() {
        @Override
        public Feedback createFromParcel(Parcel in) {
            return new Feedback(in);
        }

        @Override
        public Feedback[] newArray(int size) {
            return new Feedback[size];
        }
    };
    private final String email;
    private final String body;

    /**
     * Initializes the instance's member
     * with the given arguments.
     *
     * @param email The recipient's email.
     * @param body  The inputted feedback.
     */
    public Feedback(String email, String body) {
        this.email = email;
        this.body = body;
    }

    protected Feedback(@NotNull Parcel in) {
        email = in.readString();
        body = in.readString();
    }

    /**
     * Access the recipient's email.
     *
     * @return The email of the sender.
     */
    public String getEmail() {
        return email;
    }

    /**
     * The actual feedback message.
     *
     * @return The body of the feedback.
     */
    public String getBody() {
        return body;
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
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(body);
    }
}
