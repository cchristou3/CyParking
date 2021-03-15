package io.github.cchristou3.CyParking.apiClient.model.data.parking;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import io.github.cchristou3.CyParking.apiClient.model.data.parking.lot.ParkingLot;

/**
 * Purpose: <p>POJO to be used to transfer and receive data
 * via activities / fragments and HTTP requests.</p>
 * <p>This is a subclass of {@link ParkingId} - A parking has a ParkingId.</p>
 * <p>This is the base class for all major Parking objects</p>
 * <p>Its subclass is: {@link ParkingLot}</p>
 *
 * @author Charalambos Christou
 * @version 4.0 20/01/21
 */
public abstract class Parking extends ParkingId {

    @SerializedName("coordinates")
    protected Coordinates coordinates;

    public Parking() {/* no-argument constructor to be used by GSON */ }

    /**
     * Public Constructor.
     * Initialize all the attributes of the class with the given arguments.
     *
     * @param coordinates The lot's to be booked coordinates
     * @param parkingId   The lot's id.
     */
    public Parking(Coordinates coordinates, int parkingId) {
        this.coordinates = coordinates;
        this.parkingId = parkingId;
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
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    @NonNull
    @Override
    public String toString() {
        return super.toString() + ", " + coordinates.toString();
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
         * Constructor to be used by the Parcelable interface
         * to initialize the ParkingLot instance with the specified
         * {@link Parcel}.
         *
         * @param in Contains the contents of the ParkingLot instance.
         */
        protected Coordinates(@NotNull Parcel in) {
            latitude = in.readDouble();
            longitude = in.readDouble();
        }

        /**
         * Indicates whether some other object is "equal to" this one.
         *
         * @param obj the reference object with which to compare.
         * @return {@code true} if this object is the same as the obj
         * argument; {@code false} otherwise.
         * @see HashMap
         */
        @Override
        public boolean equals(@Nullable Object obj) {
            return (obj instanceof Coordinates) // Same instance class
                    && latitude == ((Coordinates) obj).latitude // Same latitude
                    && longitude == ((Coordinates) obj).longitude; // Same longitude
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
         * @see Parcelable#writeToParcel
         */
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeDouble(latitude);
            dest.writeDouble(longitude);
        }

        /**
         * @see Parcelable#describeContents
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
