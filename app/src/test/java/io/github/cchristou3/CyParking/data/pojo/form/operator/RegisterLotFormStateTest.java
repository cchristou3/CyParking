package io.github.cchristou3.CyParking.data.pojo.form.operator;

import org.junit.Assert;
import org.junit.Test;

import io.github.cchristou3.CyParking.R;

/**
 * Unit tests for the {@link RegisterLotFormState} class.
 */
public class RegisterLotFormStateTest {

    @Test
    public void registerFormState_withoutErrors_validIsTrue() {
        // Given
        Integer error = R.string.any_error;
        // When
        RegisterLotFormState state =
                new RegisterLotFormState(true);
        // Then
        Assert.assertTrue(state.getMobileNumberError() == null
                && state.getLotNameError() == null
                && state.getLotCapacityError() == null
                && state.getSlotOfferError() == null
                && state.getLatLngError() == null
                && state.isDataValid());
    }

    @Test
    public void registerFormState_fiveErrors_ErrorsNotNullValidIsFalse() {
        // Given
        Integer error = R.string.any_error;
        // When
        RegisterLotFormState state =
                new RegisterLotFormState(error,
                        error,
                        error,
                        error,
                        error);
        // Then
        Assert.assertTrue(state.getMobileNumberError().equals(error)
                && state.getLotNameError().equals(error)
                && state.getLotCapacityError().equals(error)
                && state.getSlotOfferError().equals(error)
                && state.getLatLngError().equals(error)
                && !state.isDataValid());
    }

    @Test
    public void registerFormState_mobileError_mobileErrorNotNullValidIsFalse() {
        // Given
        Integer error = R.string.any_error;
        // When
        RegisterLotFormState state =
                new RegisterLotFormState(error,
                        null,
                        null,
                        null,
                        null);
        // Then
        Assert.assertTrue(state.getMobileNumberError().equals(error)
                && state.getLotNameError() == null
                && state.getLotCapacityError() == null
                && state.getSlotOfferError() == null
                && state.getLatLngError() == null
                && !state.isDataValid());
    }

    @Test
    public void registerFormState_lotNameError_lotNameErrorNotNullValidIsFalse() {
        // Given
        Integer error = R.string.any_error;
        // When
        RegisterLotFormState state =
                new RegisterLotFormState(null,
                        error,
                        null,
                        null,
                        null);
        // Then
        Assert.assertTrue(state.getMobileNumberError() == null
                && state.getLotNameError().equals(error)
                && state.getLotCapacityError() == null
                && state.getSlotOfferError() == null
                && state.getLatLngError() == null
                && !state.isDataValid());
    }

    @Test
    public void registerFormState_lotCapacityError_lotCapacityErrorNotNullValidIsFalse() {
        // Given
        Integer error = R.string.any_error;
        // When
        RegisterLotFormState state =
                new RegisterLotFormState(null,
                        null,
                        error,
                        null,
                        null);
        // Then
        Assert.assertTrue(state.getMobileNumberError() == null
                && state.getLotNameError() == null
                && state.getLotCapacityError().equals(error)
                && state.getSlotOfferError() == null
                && state.getLatLngError() == null
                && !state.isDataValid());
    }

    @Test
    public void registerFormState_slotOfferError_slotOfferErrorNotNullInvalid() {
        // Given
        Integer error = R.string.any_error;
        // When
        RegisterLotFormState state =
                new RegisterLotFormState(null,
                        null,
                        null,
                        error,
                        null);
        // Then
        Assert.assertTrue(state.getMobileNumberError() == null
                && state.getLotNameError() == null
                && state.getLotCapacityError() == null
                && state.getSlotOfferError().equals(error)
                && state.getLatLngError() == null
                && !state.isDataValid());
    }

    @Test
    public void registerFormState_lotLatLngError_lotLatLngNotNullInvalid() {
        // Given
        Integer error = R.string.any_error;
        // When
        RegisterLotFormState state =
                new RegisterLotFormState(null,
                        null,
                        null,
                        null,
                        error);
        // Then
        Assert.assertTrue(state.getMobileNumberError() == null
                && state.getLotNameError() == null
                && state.getLotCapacityError() == null
                && state.getSlotOfferError() == null
                && state.getLatLngError().equals(error)
                && !state.isDataValid());
    }

    @Test
    public void registerFormState_mobileLotNameErrors_thoseErrorsNotNullInvalid() {
        // Given
        Integer error = R.string.any_error;
        // When
        RegisterLotFormState state =
                new RegisterLotFormState(error,
                        error,
                        null,
                        null,
                        null);
        // Then
        Assert.assertTrue(state.getMobileNumberError().equals(error)
                && state.getLotNameError().equals(error)
                && state.getLotCapacityError() == null
                && state.getSlotOfferError() == null
                && state.getLatLngError() == null
                && !state.isDataValid());
    }

    @Test
    public void registerFormState_mobileLotCapacityErrors_thoseErrorsNotNullInvalid() {
        // Given
        Integer error = R.string.any_error;
        // When
        RegisterLotFormState state =
                new RegisterLotFormState(error,
                        null,
                        error,
                        null,
                        null);
        // Then
        Assert.assertTrue(state.getMobileNumberError().equals(error)
                && state.getLotNameError() == null
                && state.getLotCapacityError().equals(error)
                && state.getSlotOfferError() == null
                && state.getLatLngError() == null
                && !state.isDataValid());
    }

    @Test
    public void registerFormState_mobileSlotOfferErrors_thoseErrorsNotNullInvalid() {
        // Given
        Integer error = R.string.any_error;
        // When
        RegisterLotFormState state =
                new RegisterLotFormState(error,
                        null,
                        null,
                        error,
                        null);
        // Then
        Assert.assertTrue(state.getMobileNumberError().equals(error)
                && state.getLotNameError() == null
                && state.getLotCapacityError() == null
                && state.getSlotOfferError().equals(error)
                && state.getLatLngError() == null
                && !state.isDataValid());
    }

    @Test
    public void registerFormState_mobileLatLngErrors_thoseErrorsNotNullInvalid() {
        // Given
        Integer error = R.string.any_error;
        // When
        RegisterLotFormState state =
                new RegisterLotFormState(error,
                        null,
                        null,
                        null,
                        error);
        // Then
        Assert.assertTrue(state.getMobileNumberError().equals(error)
                && state.getLotNameError() == null
                && state.getLotCapacityError() == null
                && state.getSlotOfferError() == null
                && state.getLatLngError().equals(error)
                && !state.isDataValid());
    }

    @Test
    public void registerFormState_lotNameLotCapacityErrors_thoseErrorsNotNullInvalid() {
        // Given
        Integer error = R.string.any_error;
        // When
        RegisterLotFormState state =
                new RegisterLotFormState(null,
                        error,
                        error,
                        null,
                        null);
        // Then
        Assert.assertTrue(state.getMobileNumberError() == null
                && state.getLotNameError().equals(error)
                && state.getLotCapacityError().equals(error)
                && state.getSlotOfferError() == null
                && state.getLatLngError() == null
                && !state.isDataValid());
    }

    @Test
    public void registerFormState_lotNameSlotOfferErrors_thoseErrorsNotNullInvalid() {
        // Given
        Integer error = R.string.any_error;
        // When
        RegisterLotFormState state =
                new RegisterLotFormState(null,
                        error,
                        null,
                        error,
                        null);
        // Then
        Assert.assertTrue(state.getMobileNumberError() == null
                && state.getLotNameError().equals(error)
                && state.getLotCapacityError() == null
                && state.getSlotOfferError().equals(error)
                && state.getLatLngError() == null
                && !state.isDataValid());
    }

    @Test
    public void registerFormState_lotNameLatLngErrors_thoseErrorsNotNullInvalid() {
        // Given
        Integer error = R.string.any_error;
        // When
        RegisterLotFormState state =
                new RegisterLotFormState(null,
                        error,
                        null,
                        null,
                        error);
        // Then
        Assert.assertTrue(state.getMobileNumberError() == null
                && state.getLotNameError().equals(error)
                && state.getLotCapacityError() == null
                && state.getSlotOfferError() == null
                && state.getLatLngError().equals(error)
                && !state.isDataValid());
    }

    @Test
    public void registerFormState_lotCapacitySlotOfferErrors_thoseErrorsNotNullInvalid() {
        // Given
        Integer error = R.string.any_error;
        // When
        RegisterLotFormState state =
                new RegisterLotFormState(null,
                        null,
                        error,
                        error,
                        null);
        // Then
        Assert.assertTrue(state.getMobileNumberError() == null
                && state.getLotNameError() == null
                && state.getLotCapacityError().equals(error)
                && state.getSlotOfferError().equals(error)
                && state.getLatLngError() == null
                && !state.isDataValid());
    }

    @Test
    public void registerFormState_slotOfferLatLngErrors_thoseErrorsNotNullInvalid() {
        // Given
        Integer error = R.string.any_error;
        // When
        RegisterLotFormState state =
                new RegisterLotFormState(null,
                        null,
                        null,
                        error,
                        error);
        // Then
        Assert.assertTrue(state.getMobileNumberError() == null
                && state.getLotNameError() == null
                && state.getLotCapacityError() == null
                && state.getSlotOfferError().equals(error)
                && state.getLatLngError().equals(error)
                && !state.isDataValid());
    }

    @Test
    public void registerFormState_mobileNameCapacityErrors_thoseErrorsNotNullInvalid() {
        // Given
        Integer error = R.string.any_error;
        // When
        RegisterLotFormState state =
                new RegisterLotFormState(error,
                        error,
                        error,
                        null,
                        null);
        // Then
        Assert.assertTrue(state.getMobileNumberError().equals(error)
                && state.getLotNameError().equals(error)
                && state.getLotCapacityError().equals(error)
                && state.getSlotOfferError() == null
                && state.getLatLngError() == null
                && !state.isDataValid());
    }

    @Test
    public void registerFormState_mobileNameSlotOfferErrors_thoseErrorsNotNullInvalid() {
        // Given
        Integer error = R.string.any_error;
        // When
        RegisterLotFormState state =
                new RegisterLotFormState(error,
                        error,
                        null,
                        error,
                        null);
        // Then
        Assert.assertTrue(state.getMobileNumberError().equals(error)
                && state.getLotNameError().equals(error)
                && state.getLotCapacityError() == null
                && state.getSlotOfferError().equals(error)
                && state.getLatLngError() == null
                && !state.isDataValid());
    }

    @Test
    public void registerFormState_mobileNameLatLngErrors_thoseErrorsNotNullInvalid() {
        // Given
        Integer error = R.string.any_error;
        // When
        RegisterLotFormState state =
                new RegisterLotFormState(error,
                        error,
                        null,
                        null,
                        error);
        // Then
        Assert.assertTrue(state.getMobileNumberError().equals(error)
                && state.getLotNameError().equals(error)
                && state.getLotCapacityError() == null
                && state.getSlotOfferError() == null
                && state.getLatLngError().equals(error)
                && !state.isDataValid());
    }

    @Test
    public void registerFormState_nameCapacitySlotOfferErrors_thoseErrorsNotNullInvalid() {
        // Given
        Integer error = R.string.any_error;
        // When
        RegisterLotFormState state =
                new RegisterLotFormState(null,
                        error,
                        error,
                        error,
                        null);
        // Then
        Assert.assertTrue(state.getMobileNumberError() == null
                && state.getLotNameError().equals(error)
                && state.getLotCapacityError().equals(error)
                && state.getSlotOfferError().equals(error)
                && state.getLatLngError() == null
                && !state.isDataValid());
    }

    @Test
    public void registerFormState_nameCapacityLatLngErrors_thoseErrorsNotNullInvalid() {
        // Given
        Integer error = R.string.any_error;
        // When
        RegisterLotFormState state =
                new RegisterLotFormState(null,
                        error,
                        error,
                        null,
                        error);
        // Then
        Assert.assertTrue(state.getMobileNumberError() == null
                && state.getLotNameError().equals(error)
                && state.getLotCapacityError().equals(error)
                && state.getSlotOfferError() == null
                && state.getLatLngError().equals(error)
                && !state.isDataValid());
    }

    @Test
    public void registerFormState_capacitySlotOfferLatLngErrors_thoseErrorsNotNullInvalid() {
        // Given
        Integer error = R.string.any_error;
        // When
        RegisterLotFormState state =
                new RegisterLotFormState(null,
                        null,
                        error,
                        error,
                        error);
        // Then
        Assert.assertTrue(state.getMobileNumberError() == null
                && state.getLotNameError() == null
                && state.getLotCapacityError().equals(error)
                && state.getSlotOfferError().equals(error)
                && state.getLatLngError().equals(error)
                && !state.isDataValid());
    }

    @Test
    public void registerFormState_allErrorsExceptMobile_thoseErrorsNotNullInvalid() {
        // Given
        Integer error = R.string.any_error;
        // When
        RegisterLotFormState state =
                new RegisterLotFormState(null,
                        error,
                        error,
                        error,
                        error);
        // Then
        Assert.assertTrue(state.getMobileNumberError() == null
                && state.getLotNameError().equals(error)
                && state.getLotCapacityError().equals(error)
                && state.getSlotOfferError().equals(error)
                && state.getLatLngError().equals(error)
                && !state.isDataValid());
    }

    @Test
    public void registerFormState_allErrorsExceptLotName_thoseErrorsNotNullInvalid() {
        // Given
        Integer error = R.string.any_error;
        // When
        RegisterLotFormState state =
                new RegisterLotFormState(error,
                        null,
                        error,
                        error,
                        error);
        // Then
        Assert.assertTrue(state.getMobileNumberError().equals(error)
                && state.getLotNameError() == null
                && state.getLotCapacityError().equals(error)
                && state.getSlotOfferError().equals(error)
                && state.getLatLngError().equals(error)
                && !state.isDataValid());
    }

    @Test
    public void registerFormState_allErrorsExceptLotCapacity_thoseErrorsNotNullInvalid() {
        // Given
        Integer error = R.string.any_error;
        // When
        RegisterLotFormState state =
                new RegisterLotFormState(error,
                        error,
                        null,
                        error,
                        error);
        // Then
        Assert.assertTrue(state.getMobileNumberError().equals(error)
                && state.getLotNameError().equals(error)
                && state.getLotCapacityError() == null
                && state.getSlotOfferError().equals(error)
                && state.getLatLngError().equals(error)
                && !state.isDataValid());
    }

    @Test
    public void registerFormState_allErrorsExceptLotSlotOffer_thoseErrorsNotNullInvalid() {
        // Given
        Integer error = R.string.any_error;
        // When
        RegisterLotFormState state =
                new RegisterLotFormState(error,
                        error,
                        error,
                        null,
                        error);
        // Then
        Assert.assertTrue(state.getMobileNumberError().equals(error)
                && state.getLotNameError().equals(error)
                && state.getLotCapacityError().equals(error)
                && state.getSlotOfferError() == null
                && state.getLatLngError().equals(error)
                && !state.isDataValid());
    }

    @Test
    public void registerFormState_allErrorsExceptLotLatLng_thoseErrorsNotNullInvalid() {
        // Given
        Integer error = R.string.any_error;
        // When
        RegisterLotFormState state =
                new RegisterLotFormState(error,
                        error,
                        error,
                        error,
                        null);
        // Then
        Assert.assertTrue(state.getMobileNumberError().equals(error)
                && state.getLotNameError().equals(error)
                && state.getLotCapacityError().equals(error)
                && state.getSlotOfferError().equals(error)
                && state.getLatLngError() == null
                && !state.isDataValid());
    }
}