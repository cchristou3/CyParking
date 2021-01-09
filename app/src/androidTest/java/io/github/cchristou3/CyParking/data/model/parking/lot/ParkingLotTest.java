package io.github.cchristou3.CyParking.data.model.parking.lot;

import androidx.test.core.app.ApplicationProvider;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.Random;

import io.github.cchristou3.CyParking.data.model.parking.slot.Parking;

import static io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot.areSlotOffersValid;
import static io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot.isValidCapacity;
import static io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot.isValidLotLatLng;
import static io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot.isValidLotName;
import static io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot.isValidPhoneNumber;

/**
 * Unit tests for the {@link ParkingLot} class.
 */
public class ParkingLotTest {
    ParkingLot parkingLot;

    ///////////////////////////////////////////////////////////////////////////
    // generateParkingId - START
    ///////////////////////////////////////////////////////////////////////////
    @Test
    public void generateParkingId_valid_parameters() {
        // Given
        parkingLot = new ParkingLot(new Parking.Coordinates(1, 2),
                "11111111", "1@gmail.com", "name");
        // When
        int output = parkingLot.getParkingID();
        // Then
        Assert.assertEquals(14111111, output);
    }

    @Test(expected = NullPointerException.class)
    public void generateParkingId_null_coordinates() {
        parkingLot = new ParkingLot(null,
                "11111111", "1@gmail.com", "name");
    }

    @Test(expected = NumberFormatException.class)
    public void generateParkingId_null_mobile() {
        parkingLot = new ParkingLot(new Parking.Coordinates(1, 2),
                null, "1@gmail.com", "name");
    }

    @Test(expected = NumberFormatException.class)
    public void generateParkingId_invalid_format_mobile() {
        parkingLot = new ParkingLot(new Parking.Coordinates(1, 2),
                "sdsasdsa123", "1@gmail.com", "name");
    }

    ///////////////////////////////////////////////////////////////////////////
    // generateParkingId - END
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // getAvailability - START
    ///////////////////////////////////////////////////////////////////////////

    @Test
    public void getAvailability_valid_values() {
        // Given
        parkingLot = new ParkingLot();
        parkingLot.setCapacity(20);
        parkingLot.setAvailableSpaces(20);
        // When
        String output = parkingLot.getAvailability(ApplicationProvider.getApplicationContext());
        // Then
        Assert.assertEquals("Availability: 0/20", output);
    }

    ///////////////////////////////////////////////////////////////////////////
    // getAvailability - END
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // getBestOffer - START
    ///////////////////////////////////////////////////////////////////////////

    @Test(expected = EmptyStackException.class) // Then
    public void getBestOffer_null_list() {
        // Given
        parkingLot = new ParkingLot();
        // When
        SlotOffer output = parkingLot.getBestOffer();
    }

    @Test(expected = EmptyStackException.class) // Then
    public void getBestOffer_no_offers() {
        // Given
        parkingLot = new ParkingLot();
        parkingLot.setSlotOfferList(Collections.emptyList());
        // When
        SlotOffer output = parkingLot.getBestOffer();
    }

    @Test
    public void getBestOffer_one_offer() {
        // Given
        parkingLot = new ParkingLot();
        parkingLot.setSlotOfferList(new ArrayList<>(Arrays.asList(
                new SlotOffer(1, 1)
        )));
        // When
        SlotOffer output = parkingLot.getBestOffer();
        // Then
        Assert.assertEquals(new SlotOffer(1, 1).toString(), output.toString());
    }

    @Test
    public void getBestOffer_many_offers() {
        // Given
        parkingLot = new ParkingLot();
        parkingLot.setSlotOfferList(new ArrayList<>(Arrays.asList(
                new SlotOffer(1, 1), new SlotOffer(1, 2), new SlotOffer(2.5f, 2)
        )));
        // When
        SlotOffer output = parkingLot.getBestOffer();
        // Then
        Assert.assertEquals(new SlotOffer(2.5f, 2).toString(), output.toString());
    }
    ///////////////////////////////////////////////////////////////////////////
    // getBestOffer - END
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // isValidPhoneNumber - START
    ///////////////////////////////////////////////////////////////////////////
    @Test
    public void isValidPhoneNumber_empty() {
        Assert.assertFalse(isValidPhoneNumber(""));
    }

    @Test(expected = NullPointerException.class)
    public void isValidPhoneNumber_null() {
        isValidPhoneNumber(null);
    }

    @Test
    public void isValidPhoneNumber_with_chars() {
        Assert.assertFalse(isValidPhoneNumber("asdd"));
    }

    @Test
    public void isValidPhoneNumber_too_small() {
        Assert.assertFalse(isValidPhoneNumber("12"));
    }

    @Test
    public void isValidPhoneNumber_too_big() {
        Assert.assertFalse(isValidPhoneNumber("999999999999999999"));
    }

    @Test
    public void isValidPhoneNumber_valid_number() {
        Assert.assertTrue(isValidPhoneNumber("99999999"));
    }

    ///////////////////////////////////////////////////////////////////////////
    // isValidPhoneNumber - END
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // isValidCapacity - START
    ///////////////////////////////////////////////////////////////////////////
    @Test
    public void isValidCapacity_zero_input() {
        Assert.assertFalse(isValidCapacity(0));
    }

    @Test
    public void isValidCapacity_negative_input() {
        Assert.assertFalse(isValidCapacity(-2));
    }

    @Test
    public void isValidCapacity_valid_input() {
        Assert.assertTrue(isValidCapacity(20));
    }

    ///////////////////////////////////////////////////////////////////////////
    // isValidCapacity - END
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // isValidLotName - START
    ///////////////////////////////////////////////////////////////////////////
    @Test
    public void isValidLotName_with_null() {
        Assert.assertFalse(isValidLotName(null));
    }

    @Test
    public void isValidLotName_with_empty() {
        Assert.assertFalse(isValidLotName(""));
    }

    @Test
    public void isValidLotName_with_spaces() {
        Assert.assertFalse(isValidLotName("   "));
    }

    @Test
    public void isValidLotName_small_name() {
        Assert.assertTrue(isValidLotName("1"));
    }

    @Test
    public void isValidLotName_large_name() {
        Assert.assertTrue(isValidLotName("1234567890-sdfghj"));
    }

    ///////////////////////////////////////////////////////////////////////////
    // isValidLotName - END
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // isValidLotLatLng - START
    ///////////////////////////////////////////////////////////////////////////
    @Test
    public void isValidLotLatLng_with_null() {
        Assert.assertFalse(isValidLotLatLng(null));
    }

    @Test
    public void isValidLotLatLng_with_non_null() {
        Assert.assertTrue(isValidLotLatLng(new LatLng(1, 2)));
    }
    ///////////////////////////////////////////////////////////////////////////
    // isValidLotLatLng - END
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // AreSlotOffersValid - START
    ///////////////////////////////////////////////////////////////////////////
    @Test
    public void areSlotOffersValid_with_empty_list() {
        Assert.assertFalse(areSlotOffersValid(new ArrayList<>()));
    }

    @Test
    public void areSlotOffersValid_with_null_list() {
        Assert.assertFalse(areSlotOffersValid(null));
    }

    @Test
    public void areSlotOffersValid_with_valid_list() {
        Assert.assertTrue(areSlotOffersValid(new ArrayList<>(Collections
                .singletonList(SlotOffer.getRandomInstance(new Random())))));
    }
    ///////////////////////////////////////////////////////////////////////////
    // AreSlotOffersValid - END
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // AreAvailableSpacesValid - START
    ///////////////////////////////////////////////////////////////////////////
    @Test
    public void areAvailableSpacesValid_negative_spaces() {
        // Given
        parkingLot = new ParkingLot();
        parkingLot.setCapacity(20);
        // When
        boolean output = parkingLot.areAvailableSpacesValid(-1);
        // Then
        Assert.assertFalse(output);
    }

    @Test
    public void areAvailableSpacesValid_spaces_greater_than_capacity() {
        // Given
        parkingLot = new ParkingLot();
        parkingLot.setCapacity(20);
        // When
        boolean output = parkingLot.areAvailableSpacesValid(21);
        // Then
        Assert.assertFalse(output);
    }

    @Test
    public void areAvailableSpacesValid_spaces_in_valid_range() {
        // Given
        parkingLot = new ParkingLot();
        parkingLot.setCapacity(20);
        // When
        boolean output = parkingLot.areAvailableSpacesValid(13);
        // Then
        Assert.assertTrue(output);
    }
    ///////////////////////////////////////////////////////////////////////////
    // AreAvailableSpacesValid - END
    ///////////////////////////////////////////////////////////////////////////
}