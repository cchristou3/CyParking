package io.github.cchristou3.CyParking.data.pojo.form.operator

/**
 * Purpose: interface for building a [RegisterLotFormState] object.
 *
 * @author Charalambos Christou
 * @since 1.0 12/03/21
 */
internal interface RegisterLotFormStateBuilder {

    fun setMobileNumberError(error: Int): RegisterLotFormBuilder
    fun setLotNameError(error: Int): RegisterLotFormBuilder
    fun setLotCapacityError(error: Int): RegisterLotFormBuilder
    fun setSlotOfferError(error: Int): RegisterLotFormBuilder
    fun setLatLngError(error: Int): RegisterLotFormBuilder
    fun setPhotoError(error: Int): RegisterLotFormBuilder
    fun build(): RegisterLotFormState
}