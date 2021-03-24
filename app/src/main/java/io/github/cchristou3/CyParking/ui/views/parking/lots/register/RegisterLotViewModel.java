package io.github.cchristou3.CyParking.ui.views.parking.lots.register;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.apiClient.model.data.parking.lot.ParkingLot;
import io.github.cchristou3.CyParking.apiClient.model.data.parking.lot.SlotOffer;
import io.github.cchristou3.CyParking.apiClient.remote.repository.OperatorRepository;
import io.github.cchristou3.CyParking.data.pojo.form.FormState;
import io.github.cchristou3.CyParking.data.pojo.form.operator.RegisterLotFormBuilder;
import io.github.cchristou3.CyParking.data.pojo.form.operator.RegisterLotFormState;
import io.github.cchristou3.CyParking.ui.components.SingleLiveEvent;
import io.github.cchristou3.CyParking.ui.components.ToastViewModel;
import io.github.cchristou3.CyParking.utils.Utility;

import static io.github.cchristou3.CyParking.apiClient.model.data.parking.lot.ParkingLot.Availability.isCapacityValid;
import static io.github.cchristou3.CyParking.apiClient.model.data.parking.lot.ParkingLot.areSlotOffersValid;
import static io.github.cchristou3.CyParking.apiClient.model.data.parking.lot.ParkingLot.isLotLatLngValid;
import static io.github.cchristou3.CyParking.apiClient.model.data.parking.lot.ParkingLot.isNameValid;
import static io.github.cchristou3.CyParking.apiClient.model.data.parking.lot.ParkingLot.isValidPhoneNumber;

/**
 * Purpose: <p>Data persistence when configuration changes.
 * Used when operator-typed users try to register
 * their parking lot onto the system.</p>
 *
 * @author Charalambos Christou
 * @version 3.0 13/03/21
 */
public class RegisterLotViewModel extends ToastViewModel {

    // Data members
    private final MutableLiveData<String> mOperatorMobileNumber = new MutableLiveData<>();
    private final MutableLiveData<Integer> mLotCapacity = new MutableLiveData<>();
    private final MutableLiveData<String> mLotName = new MutableLiveData<>();
    private final MutableLiveData<LatLng> mLotLatLng = new MutableLiveData<>();
    private final MutableLiveData<List<SlotOffer>> mSlotOfferList = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<RegisterLotFormState> mRegisterLotFormState = new MutableLiveData<>();
    private final MutableLiveData<FormState> mAreSlotOfferArgumentsSelected = new MutableLiveData<>(new FormState(false));
    private final MutableLiveData<Uri> mImageUri = new MutableLiveData<>();
    private final MutableLiveData<Float> mSelectedPrice = new MutableLiveData<>(null); // Initially null
    private final MutableLiveData<Float> mSelectedDuration = new MutableLiveData<>(null); // Initially null
    private final MutableLiveData<Object> mNavigateBack = new SingleLiveEvent<>();

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
            return new RegisterLotFormBuilder().setMobileNumberError(R.string.mobile_phone_error).build();
        } else if (!isNameValid(lotName)) {
            return new RegisterLotFormBuilder().setLotNameError(R.string.lot_name_error).build();
        } else if (!isCapacityValid(lotCapacity)) {
            return new RegisterLotFormBuilder().setLotCapacityError(R.string.lot_capacity_error).build();
        } else if (!isLotLatLngValid(lotLatLng)) {
            return new RegisterLotFormBuilder().setLatLngError(R.string.lot_lat_lng_error).build();
        } else if (!isImageUriValid()) {
            return new RegisterLotFormBuilder().setPhotoError(0).build();
        } else if (!areSlotOffersValid(slotOfferList)) {
            return new RegisterLotFormBuilder().setSlotOfferError(R.string.lot_slot_offer_error).build();
        }
        return new RegisterLotFormState(true);
    }

    /**
     * Stores the specified {@link ParkingLot} instance to the database.
     *
     * @param parkingLot     The lot to be added to the database.
     * @param hideLoadingBar a runnable responsible for hiding the loading bar.
     */
    public void registerParkingLot(ParkingLot parkingLot, Runnable hideLoadingBar) {
        mOperatorRepository.registerParkingLot(getImageUri(), parkingLot)
                .addOnCompleteListener((Task<Boolean> task) -> {
                    if (task.isSuccessful()) {
                        boolean wasRegistrationSuccessful = task.getResult();
                        if (wasRegistrationSuccessful) {
                            // Display message to user.
                            updateToastMessage(R.string.success_lot_registration);
                            // Navigate back to home screen
                            navigateBack();
                        } else {
                            // Display error message to user that the parking lot already exists
                            updateToastMessage(R.string.error_lot_already_exists);
                        }
                    }
                    hideLoadingBar.run();
                });
        ;
    }

    /**
     * Set the value of {@link #mNavigateBack} to null to trigger the observer callback.
     */
    void navigateBack() {
        mNavigateBack.setValue(null);
    }

    /**
     * Getter of the {@link RegisterLotViewModel#mNavigateBack}.
     *
     * @return the LiveData instance of it, to limit any direct changes to it outside of the ViewModel.
     */
    public LiveData<Object> getNavigateBackState() {
        return mNavigateBack;
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
        return mAreSlotOfferArgumentsSelected;
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
     * Updates the value of {@link #mSelectedDuration}
     * with the given argument. Also, updates the value
     * of {@link #mAreSlotOfferArgumentsSelected} that indicates
     * whether both the price and the duration were selected.
     *
     * @param selectedDuration the new value of {@link #mSelectedDuration}.
     */
    public void updateSelectedDuration(Float selectedDuration) {
        mSelectedDuration.setValue(selectedDuration);
        updateSelectedSlotOfferArguments(selectedDuration, mSelectedPrice.getValue());
    }

    /**
     * Updates the value of {@link #mSelectedPrice}
     * with the given argument. Also, updates the value
     * of {@link #mAreSlotOfferArgumentsSelected} that indicates
     * whether both the price and the duration were selected.
     *
     * @param selectedPrice the new value of {@link #mSelectedPrice}.
     */
    public void updateSelectedPrice(Float selectedPrice) {
        mSelectedPrice.setValue(selectedPrice);
        updateSelectedSlotOfferArguments(mSelectedDuration.getValue(), selectedPrice);
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
     * Updates the value of {@link #mAreSlotOfferArgumentsSelected}
     * with the given argument.
     *
     * @param duration the duration of a potential new slot offer.
     * @param price    the price of a potential new slot offer.
     */
    public void updateSelectedSlotOfferArguments(Float duration, Float price) {
        if (mAreSlotOfferArgumentsSelected.getValue() != null && mAreSlotOfferArgumentsSelected.getValue().isDataValid()) {
            // If valid then both attributes have been set.
            mSelectedPrice.setValue(price);
            mSelectedDuration.setValue(duration);
            return;
        }
        // Otherwise, check which if both were selected.
        if (duration == null || price == null) {
            mAreSlotOfferArgumentsSelected.setValue(new FormState(false));
        } else {
            mAreSlotOfferArgumentsSelected.setValue(new FormState(true));
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

    /**
     * Create a {@link SlotOffer} object based on the selected price and duration,
     * validate it, and if valid add it to the list.
     */
    public void addToList() {
        // Add to the adapter's list
        List<SlotOffer> newSlotOfferList = getSlotOfferList();
        if (newSlotOfferList == null) {
            newSlotOfferList = new ArrayList<>();
        } else {
            newSlotOfferList = Utility.cloneList(newSlotOfferList);
        }

        // Terminate method if either is null
        if (mSelectedDuration.getValue() == null || mSelectedPrice.getValue() == null) return;

        final SlotOffer newSlotOffer = new SlotOffer(mSelectedDuration.getValue(), mSelectedPrice.getValue());

        if (Utility.contains(newSlotOfferList, newSlotOffer)) {
            updateToastMessage(R.string.slot_offer_already_exist);
            // TODO: 24/01/2021 Animate color to that item
            return;
        }
        newSlotOfferList.add(newSlotOffer);
        updateSlotOfferList(newSlotOfferList);
    }
}