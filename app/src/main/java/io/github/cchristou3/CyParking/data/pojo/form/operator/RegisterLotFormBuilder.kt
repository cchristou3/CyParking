package io.github.cchristou3.CyParking.data.pojo.form.operator

/**
 * Purpose: isolate complex construction code of [RegisterLotFormState].
 *
 * @author Charalambos Christou
 * @since 1.0 12/03/21
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

    override fun build(): RegisterLotFormState = RegisterLotFormState(
            mMobileNumberError, mLotNameError,
            mLotCapacityError, mSlotOfferError,
            mLatLngError, mPhotoError
    )
}