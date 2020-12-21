package io.github.cchristou3.CyParking.ui.parking.lots;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Pattern;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.data.pojo.parking.lot.ParkingLot;
import io.github.cchristou3.CyParking.data.pojo.parking.lot.SlotOffer;
import io.github.cchristou3.CyParking.data.pojo.user.operator.RegisterLotFormState;
import io.github.cchristou3.CyParking.data.repository.ParkingRepository;

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
    private final MutableLiveData<String> operatorMobileNumber = new MutableLiveData<>();
    private final MutableLiveData<Integer> lotCapacity = new MutableLiveData<>();
    private final MutableLiveData<String> lotName = new MutableLiveData<>();
    private final MutableLiveData<LatLng> lotLatLng = new MutableLiveData<>();
    private final MutableLiveData<List<SlotOffer>> slotOfferList = new MutableLiveData<>();
    private final MutableLiveData<RegisterLotFormState> registerLotFormState = new MutableLiveData<>();

    public void lotRegistrationDataChanged(final String operatorMobileNumber, final Integer lotCapacity,
                                           final String lotName, final LatLng lotLatLng,
                                           final List<SlotOffer> slotOfferList) {
        // Update the values of the LiveData data members
        this.operatorMobileNumber.setValue(operatorMobileNumber);
        this.lotCapacity.setValue(lotCapacity);
        this.lotName.setValue(lotName);
        this.lotLatLng.setValue(lotLatLng);
        this.slotOfferList.setValue(slotOfferList);

        // Validate the input and set the RegisterLotFormState accordingly
        if (!isValidPhoneNumber(operatorMobileNumber)) {
            registerLotFormState.setValue(new RegisterLotFormState(R.string.mobile_phone_error,
                    null, null, null, null));
        } else if (!isValidLotName(lotName)) {
            registerLotFormState.setValue(new RegisterLotFormState(null,
                    R.string.lot_name_error, null, null, null));
        } else if (!isValidCapacity(lotCapacity)) {
            registerLotFormState.setValue(new RegisterLotFormState(null,
                    null, R.string.lot_capacity_error, null, null));
        } else if (!isValidLotLatLng(lotLatLng)) {
            registerLotFormState.setValue(new RegisterLotFormState(null,
                    null, null, null, R.string.lot_lat_lng_error));
        } else if (!AreSlotOffersValid(slotOfferList)) {
            registerLotFormState.setValue(new RegisterLotFormState(null,
                    null, null, R.string.lot_slot_offer_error, null));
        } else {
            registerLotFormState.setValue(new RegisterLotFormState(true));
        }
    }

    public Task<Void> registerParkingLot(ParkingLot parkingLot) {
        return ParkingRepository.registerParkingLot(parkingLot);
    }

    // Return the LiveData instance of it, to limit any direct changes to it outside of the ViewModel.
    public LiveData<String> getOperatorMobileNumber() {
        return operatorMobileNumber;
    }

    public LiveData<Integer> getLotCapacity() {
        return lotCapacity;
    }

    public LiveData<String> getLotName() {
        return lotName;
    }

    public LiveData<LatLng> getLotLatLng() {
        return lotLatLng;
    }

    public LiveData<List<SlotOffer>> getSlotOfferList() {
        return slotOfferList;
    }

    public LiveData<RegisterLotFormState> getRegisterLotFormState() {
        return registerLotFormState;
    }

    // Validation methods

    public boolean isValidPhoneNumber(final String mobileNumber) {
        return Pattern.compile("^\\d{8}$").matcher(mobileNumber).matches();
    }

    public boolean isValidCapacity(final int lotCapacity) {
        return lotCapacity > 0;
    }

    public boolean isValidLotName(final String lotName) {
        return lotName != null && !lotName.isEmpty();
    }

    public boolean isValidLotLatLng(final LatLng lotLatLng) {
        return lotLatLng != null;
    }

    public boolean AreSlotOffersValid(@NotNull final List<SlotOffer> slotOfferList) {
        return slotOfferList.size() > 0;
    }
}