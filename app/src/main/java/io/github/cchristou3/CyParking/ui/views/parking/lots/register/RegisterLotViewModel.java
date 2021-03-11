package io.github.cchristou3.CyParking.ui.views.parking.lots.register;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.data.interfaces.OperatorRepository;
import io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot;
import io.github.cchristou3.CyParking.data.model.parking.lot.SlotOffer;
import io.github.cchristou3.CyParking.data.pojo.form.FormState;
import io.github.cchristou3.CyParking.data.pojo.form.operator.RegisterLotFormState;

import static io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot.Availability.isCapacityValid;
import static io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot.areSlotOffersValid;
import static io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot.isLotLatLngValid;
import static io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot.isNameValid;
import static io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot.isValidPhoneNumber;

/**
 * Purpose: <p>Data persistence when configuration changes.
 * Used when operator-typed users try to register
 * their parking lot onto the system.</p>
 *
 * @author Charalambos Christou
 * @version 2.0 24/01/21
 */
public class RegisterLotViewModel extends ViewModel {

    // Data members
    private final MutableLiveData<String> mOperatorMobileNumber = new MutableLiveData<>();
    private final MutableLiveData<Integer> mLotCapacity = new MutableLiveData<>();
    private final MutableLiveData<String> mLotName = new MutableLiveData<>();
    private final MutableLiveData<LatLng> mLotLatLng = new MutableLiveData<>();
    private final MutableLiveData<List<SlotOffer>> mSlotOfferList = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<RegisterLotFormState> mRegisterLotFormState = new MutableLiveData<>();
    private final MutableLiveData<FormState> mSelectedSlotOfferArguments = new MutableLiveData<>(new FormState(false));
    private final MutableLiveData<Uri> mImageUri = new MutableLiveData<>();

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

        mRegisterLotFormState.setValue(validateForm(operatorMobileNumber, lotCapacity, lotName, lotLatLng, slotOfferList));
    }

    /**
     * Validates the input data of the form and returns an appropriate
     * {@link RegisterLotFormState} object.
     *
     * @param operatorMobileNumber The operatorMobileNumber of the lot.
     * @param lotCapacity          The Capacity of the lot.
     * @param lotName              The Name of the lot.
     * @param lotLatLng            The LatLng of the lot.
     * @param slotOfferList        The slotOfferList of the lot.
     * @return A {@link RegisterLotFormState} object based on the user's input data.
     */
    @NotNull
    private RegisterLotFormState validateForm(final String operatorMobileNumber, final Integer lotCapacity,
                                              final String lotName, final LatLng lotLatLng,
                                              final List<SlotOffer> slotOfferList) {
        // Validate the input and set the RegisterLotFormState accordingly
        if (!isValidPhoneNumber(operatorMobileNumber)) {
            return new RegisterLotFormState(R.string.mobile_phone_error,
                    null, null, null, null, null);
        } else if (!isNameValid(lotName)) {
            return new RegisterLotFormState(null,
                    R.string.lot_name_error, null, null, null, null);
        } else if (!isCapacityValid(lotCapacity)) {
            return new RegisterLotFormState(null,
                    null, R.string.lot_capacity_error, null, null, null);
        } else if (!isLotLatLngValid(lotLatLng)) {
            return new RegisterLotFormState(null,
                    null, null, null, R.string.lot_lat_lng_error, null);
        } else if (!isImageUriValid()) {
            return new RegisterLotFormState(null,
                    null, null, null, null, 0);
        } else if (!areSlotOffersValid(slotOfferList)) {
            return new RegisterLotFormState(null,
                    null, null, R.string.lot_slot_offer_error, null, null);
        }
        return new RegisterLotFormState(true);
    }

    /**
     * Stores the specified {@link ParkingLot} instance to the database.
     *
     * @param parkingLot The lot to be added to the database.
     * @return A {@link Task<Void>} object to be handled by the view.
     */
    public Task<Boolean> registerParkingLot(ParkingLot parkingLot) {
        return mOperatorRepository.registerParkingLot(getImageUri(), parkingLot);
    }

    /**
     * Getter of the {@link RegisterLotViewModel#mRegisterLotFormState}.
     *
     * @return the LiveData instance of it, to limit any direct changes to it outside of the ViewModel.
     */
    public LiveData<RegisterLotFormState> getRegisterLotFormState() {
        return mRegisterLotFormState;
    }

    /**
     * Getter of the {@link RegisterLotViewModel#mSlotOfferList}.
     *
     * @return the LiveData instance of it, to limit any direct changes to it outside of the ViewModel.
     */
    public LiveData<List<SlotOffer>> getSlotOfferListState() {
        return mSlotOfferList;
    }

    /**
     * Getter of the {@link RegisterLotViewModel#mSlotOfferList}.
     *
     * @return the LiveData instance of it, to limit any direct changes to it outside of the ViewModel.
     */
    public LiveData<FormState> getSelectedSlotOfferArgumentsState() {
        return mSelectedSlotOfferArguments;
    }

    /**
     * Returns the value of {@link #mImageUri}.
     *
     * @return a reference to the value of {@link #mImageUri}.
     */
    public Uri getImageUri() {
        return mImageUri.getValue();
    }

    /**
     * Returns the LiveData reference of {@link #mImageUri}.
     *
     * @return the LiveData reference of {@link #mImageUri}.
     */
    public LiveData<Uri> getImageUriState() {
        return mImageUri;
    }

    /**
     * Updates the value of {@link #mImageUri}
     * with the given argument.
     *
     * @param imageUri the new value of {@link #mImageUri}.
     */
    public void updateImageUri(Uri imageUri) {
        mImageUri.setValue(imageUri);
        mRegisterLotFormState.setValue(validateForm(
                mOperatorMobileNumber.getValue(),
                mLotCapacity.getValue(),
                mLotName.getValue(),
                mLotLatLng.getValue(),
                mSlotOfferList.getValue())
        );
    }

    /**
     * Returns the value of {@link #mSlotOfferList}.
     *
     * @return a reference to the value of {@link #mSlotOfferList}.
     */
    public List<SlotOffer> getSlotOfferList() {
        return mSlotOfferList.getValue();
    }

    /**
     * Updates the value of {@link #mSlotOfferList}
     * with the given argument.
     *
     * @param slotOffers the new value of {@link #mSlotOfferList}.
     */
    public void updateSlotOfferList(List<SlotOffer> slotOffers) {
        mSlotOfferList.setValue(slotOffers);
    }

    /**
     * Updates the value of {@link #mSelectedSlotOfferArguments}
     * with the given argument.
     *
     * @param duration the duration of a potential new slot offer.
     * @param price    the price of a potential new slot offer.
     */
    public void updateSelectedSlotOfferArguments(Float duration, Float price) {
        if (mSelectedSlotOfferArguments.getValue().isDataValid()) return;

        if (duration == null || price == null) {
            mSelectedSlotOfferArguments.setValue(new FormState(false));
        } else {
            mSelectedSlotOfferArguments.setValue(new FormState(true));
        }
    }

    /**
     * Check whether the current value of {@link #mImageUri}
     * is valid.
     *
     * @return True, if non-null. Otherwise, false.
     */
    private boolean isImageUriValid() {
        return getImageUri() != null;
    }
}