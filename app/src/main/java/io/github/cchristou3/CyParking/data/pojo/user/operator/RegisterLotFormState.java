package io.github.cchristou3.CyParking.data.pojo.user.operator;

import org.jetbrains.annotations.Nullable;

import io.github.cchristou3.CyParking.data.pojo.user.FormState;

/**
 * Purpose: <p>Data validation state of the lot registration form.</p>
 *
 * @author Charalambos Christou
 * @version 1.0 21/12/20
 */
public class RegisterLotFormState extends FormState {

    @Nullable
    private final Integer mobileNumberError;

    @Nullable
    private final Integer lotNameError;

    @Nullable
    private final Integer lotCapacityError;

    @Nullable
    private final Integer slotOfferError;

    @Nullable
    private final Integer latLngError;

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
                                @Nullable Integer slotOfferError, @Nullable Integer latLngError) {
        super(false);
        this.mobileNumberError = mobileNumberError;
        this.lotNameError = lotNameError;
        this.lotCapacityError = lotCapacityError;
        this.slotOfferError = slotOfferError;
        this.latLngError = latLngError;
    }

    /**
     * Constructor used when the RegisterLotFormState is valid.
     *
     * @param isDataValid true of the data in the form is valid
     */
    public RegisterLotFormState(boolean isDataValid) {
        super(isDataValid);
        this.mobileNumberError = null;
        this.lotNameError = null;
        this.lotCapacityError = null;
        this.slotOfferError = null;
        this.latLngError = null;
    }

    /**
     * Getters for all error ids.
     */
    @Nullable
    public Integer getMobileNumberError() {
        return mobileNumberError;
    }

    @Nullable
    public Integer getLotNameError() {
        return lotNameError;
    }

    @Nullable
    public Integer getLotCapacityError() {
        return lotCapacityError;
    }

    @Nullable
    public Integer getSlotOfferError() {
        return slotOfferError;
    }

    @Nullable
    public Integer getLatLngError() {
        return latLngError;
    }
}
