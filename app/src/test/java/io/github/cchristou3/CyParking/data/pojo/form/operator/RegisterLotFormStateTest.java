package io.github.cchristou3.CyParking.data.pojo.form.operator;

import org.junit.Assert;
import org.junit.Test;

import io.github.cchristou3.CyParking.R;

/**
 * Unit tests for the {@link RegisterLotFormState} class.
 */
public class RegisterLotFormStateTest {

    @Test
    public void registerFormState_with_five_errors() {
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
    public void registerFormState_with_mobile_error() {
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
    public void registerFormState_with_lot_name_error() {
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
    public void registerFormState_with_lot_capacity_error() {
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
    public void registerFormState_with_slot_offer_error() {
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
    public void registerFormState_with_lot_latLng_error() {
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
    public void registerFormState_with_mobile_lot_name_error() {
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
    public void registerFormState_with_mobile_lot_capacity_error() {
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
    public void registerFormState_with_mobile_slot_offer_error() {
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
    public void registerFormState_with_mobile_latLng_error() {
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
    public void registerFormState_with_lot_name_lot_capacity_error() {
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
    public void registerFormState_with_lot_name_slot_offer_error() {
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
    public void registerFormState_with_lot_name_latLng_error() {
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
    public void registerFormState_with_lot_capacity_slot_offer_error() {
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
    public void registerFormState_with_slot_offer_latLng_error() {
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
    public void registerFormState_with_mobile_name_capacity_error() {
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
    public void registerFormState_with_mobile_name_slot_offer_error() {
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
    public void registerFormState_with_mobile_name_latLng_error() {
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
    public void registerFormState_with_name_capacity_slot_offer_error() {
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
    public void registerFormState_with_name_capacity_latLng_error() {
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
    public void registerFormState_with_capacity_slot_offer_latLng_error() {
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
    public void registerFormState_with_all_errors_except_mobile() {
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
    public void registerFormState_with_all_errors_except_lot_name() {
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
    public void registerFormState_with_all_errors_except_lot_capacity() {
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
    public void registerFormState_with_all_errors_except_lot_slot_offer() {
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
    public void registerFormState_with_all_errors_except_lot_latLng() {
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