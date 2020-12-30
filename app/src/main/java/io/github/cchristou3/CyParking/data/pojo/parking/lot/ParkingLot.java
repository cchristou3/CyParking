package io.github.cchristou3.CyParking.data.pojo.parking.lot;

import android.content.Context;
import android.os.Parcel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.data.pojo.parking.slot.Parking;
import io.github.cchristou3.CyParking.data.pojo.parking.slot.booking.PrivateParkingBooking;
import io.github.cchristou3.CyParking.utilities.ShaUtility;

/**
 * Purpose: <p>POJO to be used to transfer and receive data
 * via activities / fragments and HTTPS requests. Further,
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
 * @version 6.0 29/12/20
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
    @SerializedName("operatorEmail")
    private String operatorEmail;
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
    @Nullable
    @SerializedName("openingHours")
    private String openingHours;
    @SerializedName("slotOfferList")
    private List<SlotOffer> slotOfferList;

    public ParkingLot() { /*  no-argument constructor to be used by GSON */ }

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
    public ParkingLot(HashMap<String, Double> coordinates, String operatorMobileNumber, String email) {
        super(coordinates, 0);
        this.setParkingID(generateParkingId(coordinates.values().toArray(), operatorMobileNumber));
        this.operatorEmail = email;
        this.operatorMobileNumber = operatorMobileNumber;
        this.availableSpaces = 0;
        this.availableSpacesForDisabled = 0;
        this.capacity = 0;
        this.capacityForDisabled = 0;
        this.openingHours = null;
        this.slotOfferList = null;
    }

    /**
     * Public Constructor.
     * Initialize all the attributes of the class with the given arguments.
     * Besides, the {@link #parkingID} is generated and set.
     *
     * @param coordinates                The position of the lot.
     * @param lotName                    The lot's name.
     * @param operatorEmail              The lot's operator's email address.
     * @param operatorMobileNumber       The lot's operator's phone number.
     * @param capacity                   The lot's capacity.
     * @param capacityForDisabled        The lot's capacity for disabled people.
     * @param availableSpacesForDisabled The lot's available spaces for disabled people.
     * @param openingHours               The lot's opening hours.
     * @param slotOfferList              The lot's offers.
     */
    public ParkingLot(HashMap<String, Double> coordinates, String lotName, String operatorEmail, String operatorMobileNumber,
                      int capacity, int capacityForDisabled, int availableSpacesForDisabled,
                      @Nullable String openingHours, List<SlotOffer> slotOfferList) {
        super(coordinates, 0);
        this.setParkingID(generateParkingId(coordinates.values().toArray(), operatorMobileNumber));
        this.lotName = lotName;
        this.operatorEmail = operatorEmail;
        this.operatorMobileNumber = operatorMobileNumber;
        this.capacity = capacity;
        this.availableSpaces = capacity;
        this.capacityForDisabled = capacityForDisabled;
        this.availableSpacesForDisabled = availableSpacesForDisabled;
        this.openingHours = openingHours;
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
        operatorEmail = in.readString();
        operatorMobileNumber = in.readString();
        capacity = in.readInt();
        availableSpaces = in.readInt();
        capacityForDisabled = in.readInt();
        availableSpacesForDisabled = in.readInt();
        openingHours = in.readString();

        slotOfferList = new ArrayList<>();
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            slotOfferList.add(in.readParcelable(SlotOffer.class.getClassLoader()));
        }
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
        dest.writeString(operatorEmail);
        dest.writeString(operatorMobileNumber);
        dest.writeInt(capacity);
        dest.writeInt(availableSpaces);
        dest.writeInt(capacityForDisabled);
        dest.writeInt(availableSpacesForDisabled);
        dest.writeString(openingHours);

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
        return
                "LotName: " + lotName + ", "
                        + "OperatorEmail: " + operatorEmail + ", "
                        + "OperatorMobileNumber: " + operatorMobileNumber + ", "
                        + "Capacity: " + capacity + ", "
                        + "AvailableSpaces: " + availableSpaces + ", "
                        + "CapacityForDisabled: " + capacityForDisabled + ", "
                        + "AvailableSpacesForDisabled: " + availableSpacesForDisabled + ", "
                        + "OpeningHours: " + openingHours + ", "
                        + "SlotOfferList: " + slotOfferList;

    }

    /**
     * Access the the parking lot's coordinates
     * based on a key.
     * The key indicates whether to access its
     *
     * @param key The key used for look up.
     * @return The Latitude or its Longitude of the lot, depending of the given key.
     */
    public double getParkingLotAttribute(String key) {
        if (getCoordinates() == null || getCoordinates().get(key) == null)
            return 0.00000D;
        else
            return getCoordinates().get(key);
    }

    /**
     * Create a new string which consists of the following attributes:
     * {@link #coordinates}, {@link #parkingID} and {@link #lotName}
     * Then, hash the generated string and return it.
     * Used as the DocumentID for the Firestore database's PRIVATE_PARKING node.
     *
     * @return A digest unique to each object
     * @see io.github.cchristou3.CyParking.data.repository.ParkingRepository
     * @see PrivateParkingBooking#generateUniqueId()
     */
    @Override
    public String generateUniqueId() {
        // Create a long and unique id
        String id = getCoordinates().values().toString() + parkingID + lotName;
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
    private int generateParkingId(@NotNull final Object[] lotCoordinates, @NotNull String mobileNumber) {
        try {
            int lat = (int) ((double) lotCoordinates[0] * 1000000); // Get rid most of the decimal part
            int lng = (int) ((double) lotCoordinates[1] * 1000000); // and cast it to an integer
            // 33.62356 -> (int)336235.6 -> 336235

            return Integer.parseInt(mobileNumber) + lat + lng;
        } catch (ClassCastException | NumberFormatException e) {
            byte[] bytesOfObject = (mobileNumber + lotCoordinates[0] + lotCoordinates[1] + capacity).getBytes();
            return UUID.nameUUIDFromBytes(bytesOfObject).hashCode();
        }
    }

    /**
     * Getters & Setters
     */
    public String getOperatorEmail() {
        return operatorEmail;
    }

    public void setOperatorEmail(String operatorEmail) {
        this.operatorEmail = operatorEmail;
    }

    public String getLotName() {
        return lotName;
    }

    public void setLotName(String lotName) {
        this.lotName = lotName;
    }

    public String getOperatorMobileNumber() {
        return operatorMobileNumber;
    }

    public void setOperatorMobileNumber(String operatorMobileNumber) {
        this.operatorMobileNumber = operatorMobileNumber;
    }

    public List<SlotOffer> getSlotOfferList() {
        return slotOfferList;
    }

    public void setSlotOfferList(List<SlotOffer> slotOfferList) {
        this.slotOfferList = slotOfferList;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getAvailableSpaces() {
        return availableSpaces;
    }

    public void setAvailableSpaces(int availableSpaces) {
        this.availableSpaces = availableSpaces;
    }

    public int getCapacityForDisabled() {
        return capacityForDisabled;
    }

    public void setCapacityForDisabled(int capacityForDisabled) {
        this.capacityForDisabled = capacityForDisabled;
    }

    public int getAvailableSpacesForDisabled() {
        return availableSpacesForDisabled;
    }

    public void setAvailableSpacesForDisabled(int availableSpacesForDisabled) {
        this.availableSpacesForDisabled = availableSpacesForDisabled;
    }

    @Nullable
    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(@Nullable String openingHours) {
        this.openingHours = openingHours;
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
}
