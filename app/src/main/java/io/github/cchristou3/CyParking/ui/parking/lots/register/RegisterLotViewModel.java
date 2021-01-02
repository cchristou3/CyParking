package io.github.cchristou3.CyParking.ui.parking.lots.register;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Pattern;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot;
import io.github.cchristou3.CyParking.data.model.parking.lot.SlotOffer;
import io.github.cchristou3.CyParking.data.pojo.form.operator.RegisterLotFormState;
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
    private final MutableLiveData<String> mOperatorMobileNumber = new MutableLiveData<>();
    private final MutableLiveData<Integer> mLotCapacity = new MutableLiveData<>();
    private final MutableLiveData<String> mLotName = new MutableLiveData<>();
    private final MutableLiveData<LatLng> mLotLatLng = new MutableLiveData<>();
    private final MutableLiveData<List<SlotOffer>> mSlotOfferList = new MutableLiveData<>();
    private final MutableLiveData<RegisterLotFormState> mRegisterLotFormState = new MutableLiveData<>();

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
        } else if (!isValidLotName(lotName)) {
            mRegisterLotFormState.setValue(new RegisterLotFormState(null,
                    R.string.lot_name_error, null, null, null));
        } else if (!isValidCapacity(lotCapacity)) {
            mRegisterLotFormState.setValue(new RegisterLotFormState(null,
                    null, R.string.lot_capacity_error, null, null));
        } else if (!isValidLotLatLng(lotLatLng)) {
            mRegisterLotFormState.setValue(new RegisterLotFormState(null,
                    null, null, null, R.string.lot_lat_lng_error));
        } else if (!AreSlotOffersValid(slotOfferList)) {
            mRegisterLotFormState.setValue(new RegisterLotFormState(null,
                    null, null, R.string.lot_slot_offer_error, null));
        } else {
            mRegisterLotFormState.setValue(new RegisterLotFormState(true));
        }
    }

    public Task<Void> registerParkingLot(ParkingLot parkingLot) {
        return ParkingRepository.registerParkingLot(parkingLot);
    }

    // Return the LiveData instance of it, to limit any direct changes to it outside of the ViewModel.
    public LiveData<String> getOperatorMobileNumber() {
        return mOperatorMobileNumber;
    }

    public LiveData<Integer> getLotCapacity() {
        return mLotCapacity;
    }

    public LiveData<String> getLotName() {
        return mLotName;
    }

    public LiveData<LatLng> getLotLatLng() {
        return mLotLatLng;
    }

    public LiveData<List<SlotOffer>> getSlotOfferList() {
        return mSlotOfferList;
    }

    public LiveData<RegisterLotFormState> getRegisterLotFormState() {
        return mRegisterLotFormState;
    }

    // Validation methods

    public boolean isValidPhoneNumber(final String mobileNumber) {
        return Pattern.compile("^\\d{8}$").matcher(mobileNumber).matches();
    }

    public boolean isValidCapacity(final int lotCapacity) {
        return lotCapacity > 0;
    }

    public boolean isValidLotName(final String lotName) {
        return lotName != null && !lotName.trim().isEmpty();
    }

    public boolean isValidLotLatLng(final LatLng lotLatLng) {
        return lotLatLng != null;
    }

    public boolean AreSlotOffersValid(@NotNull final List<SlotOffer> slotOfferList) {
        return slotOfferList.size() > 0;
    }
}