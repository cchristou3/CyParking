package io.github.cchristou3.CyParking.data.pojo.form.operator;

import org.jetbrains.annotations.Nullable;

import io.github.cchristou3.CyParking.data.pojo.form.FormState;

/**
 * Purpose: <p>Data validation state of the lot registration form.</p>
 *
 * @author Charalambos Christou
 * @version 1.0 21/12/20
 */
public class RegisterLotFormState extends FormState {

    @Nullable
    private final Integer mMobileNumberError;

    @Nullable
    private final Integer mLotNameError;

    @Nullable
    private final Integer mLotCapacityError;

    @Nullable
    private final Integer mSlotOfferError;

    @Nullable
    private final Integer mLatLngError;

    @Nullable
    private final Integer mPhotoError;

    /**
     * Constructor used when there is an error in the RegisterLotFormState
     * (E.g. Negative capacity, invalid mobile number, etc.).
     *
     * @param mobileNumberError The id of the error related to the mobile number.
     * @param lotNameError      The id of the error related to the lot's name.
     * @param lotCapacityError  The id of the error related to the lot's capacity.
     * @param slotOfferError    The id of the error related to the lot's slot offers.
     * @param latLngError       The id of the error related to the lot's location.
     */
    public RegisterLotFormState(@Nullable Integer mobileNumberError,
                                @Nullable Integer lotNameError, @Nullable Integer lotCapacityError,
                                @Nullable Integer slotOfferError, @Nullable Integer latLngError,
                                @Nullable Integer photoError) {
        super(false);
        this.mMobileNumberError = mobileNumberError;
        this.mLotNameError = lotNameError;
        this.mLotCapacityError = lotCapacityError;
        this.mSlotOfferError = slotOfferError;
        this.mLatLngError = latLngError;
        this.mPhotoError = photoError;
    }

    /**
     * Constructor used when the RegisterLotFormState is valid.
     *
     * @param isDataValid true of the data in the form is valid
     */
    public RegisterLotFormState(boolean isDataValid) {
        super(isDataValid);
        this.mMobileNumberError = null;
        this.mLotNameError = null;
        this.mLotCapacityError = null;
        this.mSlotOfferError = null;
        this.mLatLngError = null;
        this.mPhotoError = null;
    }

    /**
     * Getters for all error ids.
     */
    @Nullable
    public Integer getMobileNumberError() {
        return mMobileNumberError;
    }

    @Nullable
    public Integer getLotNameError() {
        return mLotNameError;
    }

    @Nullable
    public Integer getLotCapacityError() {
        return mLotCapacityError;
    }

    @Nullable
    public Integer getSlotOfferError() {
        return mSlotOfferError;
    }

    @Nullable
    public Integer getLatLngError() {
        return mLatLngError;
    }

    @Nullable
    public Integer getPhotoError() {
        return mPhotoError;
    }
}
