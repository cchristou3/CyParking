package io.github.cchristou3.CyParking.apiClient.model.data.parking.lot;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import io.github.cchristou3.CyParking.apiClient.R;
import io.github.cchristou3.CyParking.apiClient.model.data.parking.Parking;
import io.github.cchristou3.CyParking.apiClient.model.data.parking.slot.booking.Booking;
import io.github.cchristou3.CyParking.apiClient.remote.repository.BookingRepository;
import io.github.cchristou3.CyParking.utils.ShaUtility;

/**
 * Purpose: <p>POJO to be used to transfer and receive data
 * via activities / fragments and HTTP requests. Further,
 * associated with a Google Maps Marker in the ParkingMapFragment.
 * This is a Subclass of Parking.</p>
 * <p>
 * To be used, when operators register/update their
 * parking lot area.
 *
 * <p><strong>Note:</strong></p>
 * A ParkingLot object stored inside the FirebaseFirestore database
 * is uniquely identified by its
 * {@link #coordinates}, its {@link #parkingId} and its {@link #lotName}.
 * The inherited method {@link Parking#generateDocumentId()} is implemented
 * to generate the {@link DocumentReference#getId()} for the parking lot
 * in the database.
 *
 * @author Charalambos Christou
 * @version 9.0 20/01/21
 */
public class ParkingLot extends Parking implements Parcelable {

    public static final Creator<ParkingLot> CREATOR = new Creator<ParkingLot>() {
        @Override
        public ParkingLot createFromParcel(Parcel in) {
            return new ParkingLot(in);
        }

        @Override
        public ParkingLot[] newArray(int size) {
            return new ParkingLot[size];
        }
    };

    @SerializedName("lotName")
    private String lotName;
    @SerializedName("operatorId")
    private String operatorId;
    @SerializedName("operatorMobileNumber")
    private String operatorMobileNumber;
    @SerializedName("availability")
    private Availability availability;
    @SerializedName("lotPhotoUrl")
    private String lotPhotoUrl;
    @SerializedName("slotOfferList")
    private List<SlotOffer> slotOfferList;

    /* no-argument constructor to be used for deserialization */
    public ParkingLot() {
        this.availability = new Availability();
        this.coordinates = new Coordinates();
    }

    /**
     * Public Constructor.
     * Initializes {@link #coordinates}, {@link #operatorMobileNumber},
     * and {@link #operatorMobileNumber} with the specified arguments.
     * The remaining fields are set to their default values.
     *
     * @param coordinates          The position of the lot.
     * @param operatorMobileNumber The lot's operator's phone number.
     * @param email                The lot's operator's email address.
     * @throws NumberFormatException if cannot parse the mobile number to string.
     * @throws NullPointerException  if the coordinates or the mobile number is null.
     */
    public ParkingLot(
            @NonNull Coordinates coordinates, @NonNull String operatorMobileNumber,
            @NonNull String email, @NonNull String lotName
    ) throws NumberFormatException, NullPointerException {
        this.setCoordinates(coordinates);
        this.setParkingId(generateParkingId(coordinates, operatorMobileNumber));
        this.operatorId = email;
        this.lotName = lotName;
        this.operatorMobileNumber = operatorMobileNumber;
        this.availability = new Availability();
        this.lotPhotoUrl = "";
        this.slotOfferList = new ArrayList<>();
        // Generate a random list of slot offers
        final Random generator = new Random();
        for (int i = 0; i < generator.nextInt(5 + 1) + 1; i++) {
            this.slotOfferList.add(SlotOffer.getRandomInstance(generator));
        }
    }

    /**
     * Public Constructor.
     * Initialize all the attributes of the class with the given arguments.
     * Besides, the {@link #parkingId} is generated and set.
     *
     * @param coordinates          The position of the lot.
     * @param lotName              The lot's name.
     * @param operatorId           The lot's operator's email address.
     * @param operatorMobileNumber The lot's operator's phone number.
     * @param capacity             The lot's capacity.
     * @param lotPhotoUrl          The url that contains the photo of the lot in the Firebase storage.
     * @param slotOfferList        The lot's offers.
     * @throws IllegalArgumentException If the capacity is invalid, or the available spaces are not
     *                                  in the bounds of the capacity's value.
     * @throws NumberFormatException    if cannot parse the mobile number to string.
     * @throws NullPointerException     if the coordinates or the mobile number is null.
     * @see Availability#checkIfValid()
     */
    public ParkingLot(
            Coordinates coordinates, String lotName, String operatorId,
            String operatorMobileNumber, int capacity, String lotPhotoUrl, List<SlotOffer> slotOfferList
    ) throws IllegalArgumentException {
        super(coordinates, 0);
        this.setParkingId(generateParkingId(coordinates, operatorMobileNumber));
        this.lotName = lotName;
        this.operatorId = operatorId;
        this.operatorMobileNumber = operatorMobileNumber;
        this.availability = new Availability(capacity, capacity).checkIfValid();
        this.lotPhotoUrl = lotPhotoUrl;
        this.slotOfferList = slotOfferList;
    }

    /**
     * Constructor to be used by the Parcelable interface
     * to initialize the ParkingLot instance with the specified
     * {@link Parcel}.
     *
     * @param in Contains the contents of the ParkingLot instance.
     */
    protected ParkingLot(@NotNull Parcel in) {
        parkingId = in.readInt();
        coordinates = Coordinates.CREATOR.createFromParcel(in);
        lotName = in.readString();
        operatorId = in.readString();
        operatorMobileNumber = in.readString();
        availability = Availability.CREATOR.createFromParcel(in);
        lotPhotoUrl = in.readString();

        slotOfferList = new ArrayList<>();
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            slotOfferList.add(SlotOffer.CREATOR.createFromParcel(in));
        }
    }

    /**
     * Check whether the given mobile number is valid.
     *
     * @param mobileNumber The mobile number to be validated.
     * @return True, if it consists of 8 digits. Otherwise, false.
     */
    public static boolean isValidPhoneNumber(final String mobileNumber) {
        return mobileNumber != null && Pattern.compile("^\\d{8}$").matcher(mobileNumber).matches();
        // TODO: 12/01/2021 Add appropriate pattern
    }

    /**
     * Check whether the given name is valid.
     *
     * @param name The name to be validated.
     * @return True, if it non-null and non-empty. Otherwise, false.
     */
    public static boolean isNameValid(final String name) {
        return !(name == null || name.trim().isEmpty());
    }

    /**
     * Check whether the given latitude and longitude are valid.
     *
     * @param lotLatLng The latitude and longitude to be validated.
     * @return True, if it non-null. Otherwise, false.
     */
    public static boolean isLotLatLngValid(final LatLng lotLatLng) {
        return lotLatLng != null;
    }

    /**
     * Check whether the given slot offer list are valid.
     *
     * @param slotOfferList The slot offer list to be validated.
     * @return True, if it non-null and non-empty. Otherwise, false.
     */
    public static boolean areSlotOffersValid(final List<SlotOffer> slotOfferList) {
        return slotOfferList != null && !slotOfferList.isEmpty();
    }

    /**
     * Converts the {@link QueryDocumentSnapshot} of the given
     * {@link DocumentChange} to a {@link ParkingLot} object.
     *
     * @param dc The {@link DocumentChange} to get the object from.
     * @return The {@link QueryDocumentSnapshot}'s corresponding {@link ParkingLot} object.
     */
    @NotNull
    public static ParkingLot toParkingLot(@NotNull DocumentChange dc) {
        return dc.getDocument().toObject(ParkingLot.class);
    }

    public String getLotPhotoUrl() {
        return lotPhotoUrl;
    }

    public void setLotPhotoUrl(String lotPhotoUrl) {
        this.lotPhotoUrl = lotPhotoUrl;
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
        dest.writeInt(parkingId);
        dest.writeParcelable(coordinates, flags);

        dest.writeString(lotName);
        dest.writeString(operatorId);
        dest.writeString(operatorMobileNumber);
        availability.writeToParcel(dest, flags);

        dest.writeString(lotPhotoUrl);

        int size = slotOfferList.size();
        dest.writeInt(size);
        for (int i = 0; i < size; i++) {
            dest.writeParcelable(slotOfferList.get(i), flags);
        }
    }

    /**
     * Non-implemented Parcelable method.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    @NonNull
    @Override
    public String toString() {
        return super.toString() +
                "lotName: " + lotName + ", "
                + "operatorId: " + operatorId + ", "
                + "operatorMobileNumber: " + operatorMobileNumber + ", "
                + "availability: " + availability + ", "
                + "lotPhotoUri: " + lotPhotoUrl + ", "
                + "slotOfferList: " + slotOfferList;

    }

    /**
     * Create a new string which consists of the following attributes:
     * {@link #coordinates}, {@link #parkingId} and {@link #lotName}
     * Then, hash the generated string and return it.
     * Used as the DocumentID for the Firestore database's PRIVATE_PARKING node.
     *
     * @return A digest unique to each object
     * @see BookingRepository
     * @see Booking#generateDocumentId()
     */
    @Override
    public String generateDocumentId() {
        // Create a long and unique id
        String id = super.toString() + lotName;
        // Hash (SHA256) it to has a fixed length of 32 characters and return its value
        return ShaUtility.digest(id);
    }

    /**
     * Generate a unique identifier based on the lot's
     * coordinates and the operator's mobile phone number.
     *
     * @param lotCoordinates The lot's coordinates.
     * @param mobileNumber   The operator's mobile number.
     * @return The id of the parking lot.
     * @throws NumberFormatException if cannot parse the mobile number to string.
     * @throws NullPointerException  if any of the arguments is null.
     */
    private int generateParkingId(@NotNull final Coordinates lotCoordinates, @NotNull String mobileNumber)
            throws NumberFormatException, NullPointerException {
        double lat = lotCoordinates.getLatitude() * 1000000; // Get rid most of the decimal part
        double lng = lotCoordinates.getLongitude() * 1000000; // and cast it to an integer
        // E.g. 33.62356 * 1000000 -> (int)336235.6 -> 336235
        return (int) (Integer.parseInt(mobileNumber) + lat + lng);
    }


    /**
     * Access the latitude of {@link Parking.Coordinates}.
     * Note: @Exclude annotation is used to inform Firebase to not
     * map this field during (De/)Serialization, it already exists in
     * {@link Coordinates}. Omitting it would result into duplication of those
     * fields.
     *
     * @return The latitude of the lot's coordinate.
     */
    @Exclude
    public double getLatitude() {
        return coordinates.getLatitude();
    }

    /**
     * Access the longitude of {@link Parking.Coordinates}.
     * Note: @Exclude annotation is used to inform Firebase to
     * not map this field during (De/)Serialization, it already exists in
     * {@link Coordinates}. Omitting it would result into duplication of those
     * fields.
     *
     * @return The longitude of the lot's coordinate.
     */
    @Exclude
    public double getLongitude() {
        return coordinates.getLongitude();
    }

    /**
     * Invokes {@link #availability#getAvailability()} method.
     *
     * @return The string representation of the lot's availability.
     * @see #availability#getAvailability()
     */
    @Exclude
    public String getLotAvailability(@NotNull Context context) {
        return availability.getAvailability(context);
    }

    /**
     * Get the capacity of the parking lot.
     *
     * @return The {@link #availability#capacity}.
     */
    @Exclude
    public int getAvailableSpaces() {
        return availability.availableSpaces;
    }

    /**
     * Assign the value of {@link #availability#availableSpaces}
     * to the given argument.
     *
     * @param availableSpaces The new availableSpaces of the lot.
     */
    @Exclude
    public void setAvailableSpaces(int availableSpaces) {
        availability.availableSpaces = availableSpaces;
    }

    /**
     * Get the availableSpaces of this parking lot.
     *
     * @return The {@link #availability#availableSpaces}.
     */
    @Exclude
    public int getCapacity() {
        return availability.capacity;
    }

    /**
     * Assign the valie of {@link #availability#capacity}
     * to the given argument.
     *
     * @param capacity The new capacity of the lot.
     */
    @Exclude
    public void setCapacity(int capacity) {
        availability.capacity = capacity;
    }

    /**
     * Looks for the most beneficial {@link SlotOffer} of the
     * {@link #slotOfferList}.
     *
     * @return The {@link SlotOffer} instance with the smallest ratio
     * of {@link #slotOfferList}.
     */
    @Exclude
    public SlotOffer getBestOffer() {
        if (!areSlotOffersValid(slotOfferList)) {
            throw new EmptyStackException();
        }
        if (slotOfferList.size() == 1) {
            return slotOfferList.get(0);
        }

        SlotOffer bestOffer = slotOfferList.get(0);
        for (int i = 1; i < slotOfferList.size(); i++) {
            if (slotOfferList.get(i).smallerOf(bestOffer)) {
                bestOffer = slotOfferList.get(i);
            }
        }
        return bestOffer;
    }

    /**
     * Get the availability of this parking lot.
     *
     * @return The {@link ParkingLot#availability}.
     */
    public Availability getAvailability() {
        return availability;
    }

    /**
     * Assign the {@link ParkingLot#availability} with the specified
     * argument.
     *
     * @param availability The number of hours
     */
    public void setAvailability(Availability availability) {
        this.availability = availability;
    }

    /**
     * Get the lotName of this parking lot.
     *
     * @return The {@link ParkingLot#lotName}.
     */
    public String getLotName() {
        return lotName;
    }

    /**
     * Assign the {@link ParkingLot#lotName} with the specified
     * argument.
     *
     * @param lotName The number of hours
     */
    public void setLotName(String lotName) {
        this.lotName = lotName;
    }

    /**
     * Get the operatorId of this parking lot.
     *
     * @return The {@link ParkingLot#operatorId}.
     */
    public String getOperatorId() {
        return operatorId;
    }

    /**
     * Assign the {@link ParkingLot#operatorId} with the specified
     * argument.
     *
     * @param operatorId The number of hours
     */
    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    /**
     * Get the operatorMobileNumber of this parking lot.
     *
     * @return The {@link ParkingLot#operatorMobileNumber}.
     */
    public String getOperatorMobileNumber() {
        return operatorMobileNumber;
    }

    /**
     * Assign the {@link ParkingLot#operatorMobileNumber} with the specified
     * argument.
     *
     * @param operatorMobileNumber The number of hours
     */
    public void setOperatorMobileNumber(String operatorMobileNumber) {
        this.operatorMobileNumber = operatorMobileNumber;
    }

    /**
     * Get the slotOfferList of this parking lot.
     *
     * @return The {@link ParkingLot#slotOfferList}.
     */
    public List<SlotOffer> getSlotOfferList() {
        return slotOfferList;
    }

    /**
     * Assign the {@link ParkingLot#slotOfferList} with the specified
     * argument.
     *
     * @param slotOfferList The number of hours
     */
    public void setSlotOfferList(List<SlotOffer> slotOfferList) {
        this.slotOfferList = slotOfferList;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * If they have the same {@link #parkingId} and {@link #coordinates},
     * then they are the same.
     *
     * @param obj the reference object with which to compare.
     * @return {@code true} if this object is the same as the obj
     * argument; {@code false} otherwise.
     * @see java.util.HashMap
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        return (obj instanceof ParkingLot) // Same instance class
                && ((ParkingLot) obj).parkingId == this.parkingId // Same Parking Id
                && ((ParkingLot) obj).coordinates.equals(this.coordinates); // Same Coordinates
    }

    public static final class Availability implements Parcelable {
        public static final Creator<Availability> CREATOR = new Creator<Availability>() {
            @Override
            public Availability createFromParcel(Parcel in) {
                return new Availability(in);
            }

            @Override
            public Availability[] newArray(int size) {
                return new Availability[size];
            }
        };
        @SerializedName("capacity")
        private int capacity;
        @SerializedName("availableSpaces")
        private int availableSpaces;

        /**
         * Public Constructor.
         * Initialize all the attributes of the class with the given arguments.
         *
         * @param capacity        The lot's to be capacity
         * @param availableSpaces The lot's available spaces.
         */
        public Availability(int capacity, int availableSpaces) {
            this.capacity = capacity;
            this.availableSpaces = availableSpaces;
        }

        /**
         * Default constructor.
         * Initializes the object's capacity
         * and available spaces
         * with the same random integer.
         */
        public Availability() {
            this.capacity = new Random().nextInt(60 + 10) + 10;
            this.availableSpaces = capacity;
        }

        /**
         * Constructor to be used by the Parcelable interface
         * to initialize the Availability instance with the specified
         * {@link Parcel}.
         *
         * @param in Contains the contents of the ParkingLot instance.
         */
        protected Availability(@NotNull Parcel in) {
            capacity = in.readInt();
            availableSpaces = in.readInt();
        }

        /**
         * Checks whether the given capacity is valid.
         *
         * @param capacity The capacity to be validated.
         * @return True, if greater than zero. Otherwise, false.
         */
        public static boolean isCapacityValid(int capacity) {
            return capacity > 0;
        }


        /**
         * Checks whether the calling object has valid
         * {@link #capacity} and {@link #availability}.
         *
         * @return The calling object.
         * @throws IllegalArgumentException if at least one is not valid.
         */
        public Availability checkIfValid()
                throws IllegalArgumentException {
            if (!(isCapacityValid(capacity) && (availableSpaces >= 0 && availableSpaces <= capacity))) {
                throw new IllegalArgumentException("The available spaces must be in range of 0..capacity (inclusive).");
            }
            return this;
        }

        /**
         * Get the capacity of the parking lot.
         *
         * @return The {@link Availability#capacity}.
         */
        public int getCapacity() {
            return capacity;
        }

        /**
         * Assign the {@link Availability#capacity} with the specified
         * argument.
         *
         * @param capacity The capacity of the availability object.
         */
        public void setCapacity(int capacity) {
            this.capacity = capacity;
        }

        /**
         * Get the availableSpaces of this parking lot.
         *
         * @return The {@link Availability#availableSpaces}.
         */
        public int getAvailableSpaces() {
            return availableSpaces;
        }

        /**
         * Assign the {@link Availability#availableSpaces} with the specified
         * argument.
         *
         * @param availableSpaces The number of available hours
         */
        public void setAvailableSpaces(int availableSpaces) {
            this.availableSpaces = availableSpaces;
        }

        /**
         * Non-implemented Parcelable method.
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
            dest.writeInt(capacity);
            dest.writeInt(availableSpaces);
        }

        /**
         * Calculates the current availability (the amount of slots are currently taken)
         * of the parking lot.
         * E.g. availableSpaces = 20, capacity = 30
         * It will generate the string "Availability: 10/30".
         *
         * @param context The context to use.  Usually your {@link android.app.Application}
         *                or {@link android.app.Activity} object.
         * @return A string representation of the lot's availability status.
         * @see #toString()
         */
        @NotNull
        @Exclude
        public String getAvailability(@NotNull Context context) {
            return context.getString(R.string.availability) + " " + toString();
        }

        /**
         * Returns a string representation of the object.
         *
         * @return a string representation of the object.
         */
        @NonNull
        @Override
        public String toString() {
            return (capacity - availableSpaces) + "/" + capacity;
        }
    }
}
