package io.github.cchristou3.CyParking.data.pojo.parking.lot;

import android.os.Parcel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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
 * @version 5.0 23/12/20
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

    public ParkingLot() {
    }  //  no-argument constructor to be used by GSON

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

    public ParkingLot(HashMap<String, Double> coordinates, String lotName, String operatorEmail, String operatorMobileNumber,
                      int capacity, int availableSpaces, int capacityForDisabled, int availableSpacesForDisabled,
                      @Nullable String openingHours, List<SlotOffer> slotOfferList) {
        super(coordinates, 0);
        this.setParkingID(generateParkingId(coordinates.values().toArray(), operatorMobileNumber));
        this.lotName = lotName;
        this.operatorEmail = operatorEmail;
        this.operatorMobileNumber = operatorMobileNumber;
        this.capacity = capacity;
        this.availableSpaces = availableSpaces;
        this.capacityForDisabled = capacityForDisabled;
        this.availableSpacesForDisabled = availableSpacesForDisabled;
        this.openingHours = openingHours;
        this.slotOfferList = slotOfferList;
    }

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
            dest.writeParcelable(slotOfferList.get(i), 0);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

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
        if (getCoordinates().get(key) != null)
            return getCoordinates().get(key);
        else
            return 0.00000D;
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
     * @return
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

    // Getters & Setters
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
}
