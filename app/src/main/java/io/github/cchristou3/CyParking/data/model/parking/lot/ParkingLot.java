package io.github.cchristou3.CyParking.data.model.parking.lot;

import android.content.Context;
import android.os.Parcel;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.Exclude;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.data.model.parking.slot.Parking;
import io.github.cchristou3.CyParking.data.model.parking.slot.booking.Booking;
import io.github.cchristou3.CyParking.utilities.ShaUtility;

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
 * {@link #coordinates}, its {@link #parkingID} and its {@link #lotName}.
 *
 * @author Charalambos Christou
 * @version 7.0 02/01/21
 */
public class ParkingLot extends Parking {

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
    @SerializedName("capacity")
    private int capacity;
    @SerializedName("availableSpaces")
    private int availableSpaces;
    @SerializedName("capacityForDisabled")
    private int capacityForDisabled;
    @SerializedName("availableSpacesForDisabled")
    private int availableSpacesForDisabled;
    @SerializedName("slotOfferList")
    private List<SlotOffer> slotOfferList;

    public ParkingLot() { /* no-argument constructor to be used for deserialization */ }

    /**
     * Public Constructor.
     * Initializes {@link #coordinates}, {@link #operatorMobileNumber},
     * and {@link #operatorMobileNumber} with the specified arguments.
     * The remaining fields are set to their default values.
     *
     * @param coordinates          The position of the lot.
     * @param operatorMobileNumber The lot's operator's phone number.
     * @param email                The lot's operator's email address.
     */
    public ParkingLot(
            @NonNull Coordinates coordinates, @NonNull String operatorMobileNumber,
            @NonNull String email, @NonNull String lotName
    ) {
        super(coordinates, 0);
        this.setParkingID(generateParkingId(coordinates, operatorMobileNumber));
        this.operatorId = email;
        this.lotName = lotName;
        this.operatorMobileNumber = operatorMobileNumber;
        this.availableSpacesForDisabled = 0;
        this.capacityForDisabled = 0;
        this.slotOfferList = new ArrayList<>();
        final Random generator = new Random();
        for (int i = 0; i < generator.nextInt(5 + 1) + 1; i++) {
            this.slotOfferList.add(SlotOffer.getRandomInstance(generator));
        }

        this.capacity = generator.nextInt(60 + 10) + 10;
        this.availableSpaces = this.capacity;
    }

    /**
     * Public Constructor.
     * Initialize all the attributes of the class with the given arguments.
     * Besides, the {@link #parkingID} is generated and set.
     *
     * @param coordinates                The position of the lot.
     * @param lotName                    The lot's name.
     * @param operatorId                 The lot's operator's email address.
     * @param operatorMobileNumber       The lot's operator's phone number.
     * @param capacity                   The lot's capacity.
     * @param capacityForDisabled        The lot's capacity for disabled people.
     * @param availableSpacesForDisabled The lot's available spaces for disabled people.
     * @param slotOfferList              The lot's offers.
     * @throws IllegalArgumentException If the capacity is invalid, or the available spaces are not
     *                                  in the bounds of the capacity's value.
     */
    public ParkingLot(Coordinates coordinates, String lotName, String operatorId, String operatorMobileNumber,
                      int capacity, int capacityForDisabled, int availableSpacesForDisabled,
                      List<SlotOffer> slotOfferList) throws IllegalArgumentException, IllegalArgumentException {
        super(coordinates, 0);
        if (!isValidCapacity(capacity)) {
            throw new IllegalArgumentException("Capacity cannot be equal or less than 0.");
        }
        if (!areAvailableSpacesValid(availableSpaces)) {
            throw new IllegalArgumentException("The available spaces must be in range of 0..capacity (inclusive).");
        }
        this.setParkingID(generateParkingId(coordinates, operatorMobileNumber));
        this.lotName = lotName;
        this.operatorId = operatorId;
        this.operatorMobileNumber = operatorMobileNumber;
        this.capacity = capacity;
        this.availableSpaces = capacity;
        this.capacityForDisabled = capacityForDisabled;
        this.availableSpacesForDisabled = availableSpacesForDisabled;
        this.slotOfferList = slotOfferList;
    }

    /**
     * Constructor to be used by the Parcelable interface
     * to initialize the ParkingLot instance with the specified
     * {@link Parcel}.
     *
     * @param in Contains the contents of the ParkingLot instance.
     */
    protected ParkingLot(Parcel in) {
        super(in);
        lotName = in.readString();
        operatorId = in.readString();
        operatorMobileNumber = in.readString();
        capacity = in.readInt();
        availableSpaces = in.readInt();
        capacityForDisabled = in.readInt();
        availableSpacesForDisabled = in.readInt();

        slotOfferList = new ArrayList<>();
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            slotOfferList.add(in.readParcelable(SlotOffer.class.getClassLoader()));
        }
    }

    // Validation methods
    public static boolean isValidPhoneNumber(final String mobileNumber) {
        return Pattern.compile("^\\d{8}$").matcher(mobileNumber).matches();
    }

    public static boolean isValidCapacity(final int lotCapacity) {
        return lotCapacity > 0;
    }

    public static boolean isValidLotName(final String lotName) {
        return lotName != null && !lotName.trim().isEmpty();
    }

    public static boolean isValidLotLatLng(final LatLng lotLatLng) {
        return lotLatLng != null;
    }

    public static boolean areSlotOffersValid(@NotNull final List<SlotOffer> slotOfferList) {
        return slotOfferList != null && slotOfferList.size() > 0;
    }

    public boolean areAvailableSpacesValid(final int availableSpaces) {
        return availableSpaces > 0 && availableSpaces <= this.capacity;
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
        super.writeToParcel(dest, flags);
        dest.writeString(lotName);
        dest.writeString(operatorId);
        dest.writeString(operatorMobileNumber);
        dest.writeInt(capacity);
        dest.writeInt(availableSpaces);
        dest.writeInt(capacityForDisabled);
        dest.writeInt(availableSpacesForDisabled);

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
                + "capacity: " + capacity + ", "
                + "availableSpaces: " + availableSpaces + ", "
                + "capacityForDisabled: " + capacityForDisabled + ", "
                + "availableSpacesForDisabled: " + availableSpacesForDisabled + ", "
                + "slotOfferList: " + slotOfferList;

    }

    /**
     * Access the latitude of {@link Parking.Coordinates}.
     * Note: @Exclude annotation is used to inform Firebase not
     * not map this field during (De/)Serialization, it already exists in
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
     * Note: @Exclude annotation is used to inform Firebase not
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
     * Create a new string which consists of the following attributes:
     * {@link #coordinates}, {@link #parkingID} and {@link #lotName}
     * Then, hash the generated string and return it.
     * Used as the DocumentID for the Firestore database's PRIVATE_PARKING node.
     *
     * @return A digest unique to each object
     * @see io.github.cchristou3.CyParking.data.repository.ParkingRepository
     * @see Booking#generateUniqueId()
     */
    @Override
    public String generateUniqueId() {
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
     */
    private int generateParkingId(@NotNull final Coordinates lotCoordinates, @NotNull String mobileNumber)
            throws ClassCastException, NumberFormatException, NullPointerException {
        double lat = lotCoordinates.getLatitude() * 1000000; // Get rid most of the decimal part
        double lng = lotCoordinates.getLongitude() * 1000000; // and cast it to an integer
        // E.g. 33.62356 * 1000000 -> (int)336235.6 -> 336235
        return (int) (Integer.parseInt(mobileNumber) + lat + lng);
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
     * Get the capacity of this parking lot.
     *
     * @return The {@link ParkingLot#capacity}.
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Assign the {@link ParkingLot#capacity} with the specified
     * argument.
     *
     * @param capacity The number of hours
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Get the availableSpaces of this parking lot.
     *
     * @return The {@link ParkingLot#availableSpaces}.
     */
    public int getAvailableSpaces() {
        return availableSpaces;
    }

    /**
     * Assign the {@link ParkingLot#availableSpaces} with the specified
     * argument.
     *
     * @param availableSpaces The number of hours
     */
    public void setAvailableSpaces(int availableSpaces) {
        this.availableSpaces = availableSpaces;
    }

    /**
     * Get the capacityForDisabled of this parking lot.
     *
     * @return The {@link ParkingLot#capacityForDisabled}.
     */
    public int getCapacityForDisabled() {
        return capacityForDisabled;
    }

    /**
     * Assign the {@link ParkingLot#capacityForDisabled} with the specified
     * argument.
     *
     * @param capacityForDisabled The number of hours
     */
    public void setCapacityForDisabled(int capacityForDisabled) {
        this.capacityForDisabled = capacityForDisabled;
    }

    /**
     * Get the availableSpacesForDisabled of this parking lot.
     *
     * @return The {@link ParkingLot#availableSpacesForDisabled}.
     */
    public int getAvailableSpacesForDisabled() {
        return availableSpacesForDisabled;
    }

    /**
     * Assign the {@link ParkingLot#availableSpacesForDisabled} with the specified
     * argument.
     *
     * @param availableSpacesForDisabled The number of hours
     */
    public void setAvailableSpacesForDisabled(int availableSpacesForDisabled) {
        this.availableSpacesForDisabled = availableSpacesForDisabled;
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
     * Calculates the current availability (the amount of slots are currently taken)
     * of the parking lot.
     * E.g. availableSpaces = 20, capacity = 30
     * It will generate the string "Availability: 10/30".
     *
     * @param context The context to use.  Usually your {@link android.app.Application}
     *                or {@link android.app.Activity} object.
     * @return A string representation of the lot's availability status.
     */
    public String getAvailability(@NotNull Context context) {
        return context.getString(R.string.availability) + " "
                + (capacity - availableSpaces)
                + "/" + capacity;
    }

    /**
     * Looks for the most beneficial {@link SlotOffer} of the
     * {@link #slotOfferList}.
     *
     * @return The {@link SlotOffer} instance with the smallest ratio
     * of {@link #slotOfferList}.
     */
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
}
