package io.github.cchristou3.CyParking.ui.parking.lots.register;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;

import java.util.List;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.data.interfaces.OperatorRepository;
import io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot;
import io.github.cchristou3.CyParking.data.model.parking.lot.SlotOffer;
import io.github.cchristou3.CyParking.data.pojo.form.operator.RegisterLotFormState;

import static io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot.Availability.isCapacityValid;
import static io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot.areSlotOffersValid;
import static io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot.isLotLatLngValid;
import static io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot.isNameValid;
import static io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot.isValidPhoneNumber;

/**
 * Purpose: <p>Data persistence when orientation changes.
 * Used when operator-typed users try to register
 * their parking lot onto the system.</p>
 *
 * @author Charalambos Christou
 * @version 1.0 15/12/20
 */
public class RegisterLotViewModel extends ViewModel {

    // Data members
    private final MutableLiveData<String> mOperatorMobileNumber = new MutableLiveData<>();
    private final MutableLiveData<Integer> mLotCapacity = new MutableLiveData<>();
    private final MutableLiveData<String> mLotName = new MutableLiveData<>();
    private final MutableLiveData<LatLng> mLotLatLng = new MutableLiveData<>();
    private final MutableLiveData<List<SlotOffer>> mSlotOfferList = new MutableLiveData<>();
    private final MutableLiveData<RegisterLotFormState> mRegisterLotFormState = new MutableLiveData<>();

    private final OperatorRepository mOperatorRepository;

    /**
     * Initialize the ViewModel's OperatorRepository instance
     * with the given argument.
     *
     * @param operatorRepository An OperatorRepository instance.
     */
    public RegisterLotViewModel(OperatorRepository operatorRepository) {
        this.mOperatorRepository = operatorRepository;
    }

    /**
     * Updates the values of
     * <p>{@link #mOperatorMobileNumber}
     * <p>{@link #mLotCapacity}
     * <p>{@link #mLotName}
     * <p>{@link #mLotLatLng}
     * <p>{@link #mSlotOfferList}
     * with the specified attributes
     * and updates the {@link #mRegisterLotFormState}
     * based on them.
     *
     * @param operatorMobileNumber The operatorMobileNumber of the lot.
     * @param lotCapacity          The Capacity of the lot.
     * @param lotName              The Name of the lot.
     * @param lotLatLng            The LatLng of the lot.
     * @param slotOfferList        The slotOfferList of the lot.
     */
    public void lotRegistrationDataChanged(final String operatorMobileNumber, final Integer lotCapacity,
                                           final String lotName, final LatLng lotLatLng,
                                           final List<SlotOffer> slotOfferList) {
        // Update the values of the LiveData data members
        this.mOperatorMobileNumber.setValue(operatorMobileNumber);
        this.mLotCapacity.setValue(lotCapacity);
        this.mLotName.setValue(lotName);
        this.mLotLatLng.setValue(lotLatLng);
        this.mSlotOfferList.setValue(slotOfferList);

        // Validate the input and set the RegisterLotFormState accordingly
        if (!isValidPhoneNumber(operatorMobileNumber)) {
            mRegisterLotFormState.setValue(new RegisterLotFormState(R.string.mobile_phone_error,
                    null, null, null, null));
        } else if (!isNameValid(lotName)) {
            mRegisterLotFormState.setValue(new RegisterLotFormState(null,
                    R.string.lot_name_error, null, null, null));
        } else if (!isCapacityValid(lotCapacity)) {
            mRegisterLotFormState.setValue(new RegisterLotFormState(null,
                    null, R.string.lot_capacity_error, null, null));
        } else if (!isLotLatLngValid(lotLatLng)) {
            mRegisterLotFormState.setValue(new RegisterLotFormState(null,
                    null, null, null, R.string.lot_lat_lng_error));
        } else if (!areSlotOffersValid(slotOfferList)) {
            mRegisterLotFormState.setValue(new RegisterLotFormState(null,
                    null, null, R.string.lot_slot_offer_error, null));
        } else {
            mRegisterLotFormState.setValue(new RegisterLotFormState(true));
        }
    }

    /**
     * Stores the specified {@link ParkingLot} instance to the database.
     *
     * @param parkingLot The lot to be added to the database.
     * @return A {@link Task<Void>} object to be handled by the view.
     */
    public Task<Void> registerParkingLot(ParkingLot parkingLot) {
        return mOperatorRepository.registerParkingLot(parkingLot);
    }

    /**
     * Getter of the {@link RegisterLotViewModel#mRegisterLotFormState}.
     *
     * @return the LiveData instance of it, to limit any direct changes to it outside of the ViewModel.
     */
    public LiveData<RegisterLotFormState> getRegisterLotFormState() {
        return mRegisterLotFormState;
    }
}