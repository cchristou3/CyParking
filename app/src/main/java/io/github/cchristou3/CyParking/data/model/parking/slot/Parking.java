package io.github.cchristou3.CyParking.data.model.parking.slot;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

/**
 * Purpose: <p>POJO to be used to transfer and receive data
 * via activities / fragments and HTTP requests.
 * This is a Base class.</p>
 *
 * @author Charalambos Christou
 * @version 3.0 07/11/20
 */
public class Parking implements Parcelable {

    public static final Creator<Parking> CREATOR = new Creator<Parking>() {
        @Override
        public Parking createFromParcel(Parcel in) {
            return new Parking(in);
        }

        @Override
        public Parking[] newArray(int size) {
            return new Parking[size];
        }
    };
    @SerializedName("coordinates")
    protected Coordinates coordinates; // TODO: Replace with inner class Coordinates
    @SerializedName("parkingID")
    protected int parkingID; // TODO: Migrate to String data type

    public Parking() { /* no-argument constructor to be used by GSON */ }

    /**
     * Public Constructor.
     * Initialize all the attributes of the class with the given arguments.
     *
     * @param coordinates The lot's to be booked coordinates
     * @param parkingID   The lot's id.
     */
    public Parking(Coordinates coordinates, int parkingID) {
        this.coordinates = coordinates;
        this.parkingID = parkingID;
    }

    /**
     * Constructor to be used by the Parcelable interface
     * to initialize the Parking instance with the specified
     * {@link Parcel}.
     *
     * @param in Contains the contents of the Parking instance.
     */
    protected Parking(Parcel in) {
        coordinates = new Coordinates(in);
        parkingID = in.readInt();
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
        coordinates.writeToParcel(dest, flags);
        dest.writeInt(parkingID);
    }

    /**
     * Non-implemented Parcelable method.
     */
    @Override
    public int describeContents() {
        return 0;
    }


    /**
     * Access the {@link #coordinates} of the coordinate.
     *
     * @return The coordinates of the lot.
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }

    /**
     * Sets the value of {@link #coordinates} with the given argument.
     *
     * @param coordinates The new coordinates of the parking object.
     */
    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * Access the {@link #parkingID} of the coordinate.
     *
     * @return The parkingID of the lot.
     */
    public int getParkingID() {
        return parkingID;
    }

    /**
     * Sets the value of {@link #parkingID} with the given argument.
     *
     * @param parkingID The new parkingID of the parking object.
     */
    public void setParkingID(int parkingID) {
        this.parkingID = parkingID;
    }

    /**
     * To be overridden by its subclasses
     * Combines the class' attribute values to create a unique id for the object
     */
    public String generateUniqueId() {
        return "";
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    @NonNull
    @Override
    public String toString() {
        return "Id: " + parkingID + ", " + coordinates.toString();
    }

    /**
     * Purpose: store the coordinates of a lot.
     */
    public static class Coordinates implements Parcelable {
        public static final Creator<Coordinates> CREATOR = new Creator<Coordinates>() {
            @Override
            public Coordinates createFromParcel(Parcel in) {
                return new Coordinates(in);
            }

            @Override
            public Coordinates[] newArray(int size) {
                return new Coordinates[size];
            }
        };

        @SerializedName("latitude")
        protected double latitude;
        @SerializedName("longitude")
        protected double longitude;

        public Coordinates() { /* no-argument constructor to be used for deserialization */ }

        /**
         * Public Constructor.
         * Initialize all the attributes of the class with the given arguments.
         *
         * @param latitude  The latitude of the lot.
         * @param longitude The longitude of the lot.
         */
        public Coordinates(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        /**
         * @see Parking#Parking(Parcel)
         */
        protected Coordinates(@NotNull Parcel in) {
            latitude = in.readDouble();
            longitude = in.readDouble();
        }

        /**
         * Returns a string representation of the object.
         *
         * @return a string representation of the object.
         */
        @NotNull
        @Override
        public String toString() {
            return "coordinates: { "
                    + "latitude:" + latitude
                    + ", longitude:" + longitude
                    + " }";
        }

        /**
         * @see Parking#writeToParcel
         */
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeDouble(latitude);
            dest.writeDouble(longitude);
        }

        /**
         * @see Parking#describeContents
         */
        @Override
        public int describeContents() {
            return 0;
        }

        /**
         * Access the {@link #latitude} of the coordinate.
         *
         * @return The latitude of the lot's coordinate.
         */
        public double getLatitude() {
            return latitude;
        }

        /**
         * Access the {@link #longitude} of the coordinate.
         *
         * @return The longitude of the lot's coordinate.
         */
        public double getLongitude() {
            return longitude;
        }
    }
}
