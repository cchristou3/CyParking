package io.github.cchristou3.CyParking.data.pojo.form.operator

/**
 * Purpose: isolate complex construction code of [RegisterLotFormState].
 *
 * @author Charalambos Christou
 * @since 2.0 27/03/21
 */
class RegisterLotFormBuilder : RegisterLotFormStateBuilder {

    private var mMobileNumberError: Int? = null
    private var mLotNameError: Int? = null
    private var mLotCapacityError: Int? = null
    private var mSlotOfferError: Int? = null
    private var mLatLngError: Int? = null
    private var mPhotoError: Int? = null

    override fun setMobileNumberError(error: Int): RegisterLotFormBuilder {
        mMobileNumberError = error
        return this
    }

    override fun setLotNameError(error: Int): RegisterLotFormBuilder {
        mLotNameError = error
        return this
    }

    override fun setLotCapacityError(error: Int): RegisterLotFormBuilder {
        mLotCapacityError = error
        return this
    }

    override fun setSlotOfferError(error: Int): RegisterLotFormBuilder {
        mSlotOfferError = error
        return this
    }

    override fun setLatLngError(error: Int): RegisterLotFormBuilder {
        mLatLngError = error
        return this
    }

    override fun setPhotoError(error: Int): RegisterLotFormBuilder {
        mPhotoError = error
        return this
    }

    /**
     * If none of the errors is set then return a valid [RegisterLotFormState] instance.
     * Otherwise, return an appropriate [RegisterLotFormState] instance with the set errors.
     */
    override fun build(): RegisterLotFormState = if (mMobileNumberError == null
            && mLotNameError == null
            && mLotCapacityError == null
            && mSlotOfferError == null
            && mLatLngError == null
            && mPhotoError == null
    ) RegisterLotFormState(true)
    else RegisterLotFormState(
            mMobileNumberError, mLotNameError,
            mLotCapacityError, mSlotOfferError,
            mLatLngError, mPhotoError
    )
}