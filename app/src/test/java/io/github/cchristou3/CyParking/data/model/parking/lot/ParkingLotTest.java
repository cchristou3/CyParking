package io.github.cchristou3.CyParking.data.model.parking.lot;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.Random;

import io.github.cchristou3.CyParking.data.model.parking.slot.Parking;

import static io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot.Availability.isCapacityValid;
import static io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot.areSlotOffersValid;
import static io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot.isLotLatLngValid;
import static io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot.isNameValid;
import static io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot.isValidPhoneNumber;

/*
 * Unit tests for the {@link ParkingLot} class.
 */
@RunWith(AndroidJUnit4.class)
public class ParkingLotTest {
    ParkingLot parkingLot;

    ///////////////////////////////////////////////////////////////////////////
    // generateParkingId - START
    ///////////////////////////////////////////////////////////////////////////
    @Test
    public void generateParkingId_validParameters_returnsExpectedParkingID() {
        // Given
        parkingLot = new ParkingLot(new Parking.Coordinates(1, 2),
                "11111111", "1@gmail.com", "name");
        // When
        int output = parkingLot.getParkingID();
        // Then
        Assert.assertEquals(14111111, output);
    }

    @Test(expected = NullPointerException.class)
    public void generateParkingId_nullCoordinates_throwsException() {
        parkingLot = new ParkingLot(null,
                "11111111", "1@gmail.com", "name");
    }

    @Test(expected = NumberFormatException.class)
    public void generateParkingId_nullMobile_throwsException() {
        parkingLot = new ParkingLot(new Parking.Coordinates(1, 2),
                null, "1@gmail.com", "name");
    }

    @Test(expected = NumberFormatException.class)
    public void generateParkingId_invalidFormatMobile_throwsException() {
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
    public void getAvailability_validValues_returnsExpectedString() {
        // Given
        parkingLot = new ParkingLot();
        parkingLot.setCapacity(20);
        parkingLot.setAvailableSpaces(20);
        // When
        String output = parkingLot.getLotAvailability(ApplicationProvider.getApplicationContext());
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
    public void getBestOffer_nullList_throwsException() {
        // Given
        parkingLot = new ParkingLot();
        // When
        SlotOffer output = parkingLot.getBestOffer();
    }

    @Test(expected = EmptyStackException.class) // Then
    public void getBestOffer_noOffers_throwsException() {
        // Given
        parkingLot = new ParkingLot();
        parkingLot.setSlotOfferList(Collections.emptyList());
        // When
        SlotOffer output = parkingLot.getBestOffer();
    }

    @Test
    public void getBestOffer_oneOffer_returnsThatOffer() {
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
    public void getBestOffer_manyOffers_returnsExpectedOffer() {
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
    public void isValidPhoneNumber_empty_returnsFalse() {
        Assert.assertFalse(isValidPhoneNumber(""));
    }

    @Test
    public void isValidPhoneNumber_null_returnsFalse() {
        Assert.assertFalse(isValidPhoneNumber(null));
    }

    @Test
    public void isValidPhoneNumber_withChars_returnsFalse() {
        Assert.assertFalse(isValidPhoneNumber("asdd"));
    }

    @Test
    public void isValidPhoneNumber_tooSmall_returnsFalse() {
        Assert.assertFalse(isValidPhoneNumber("12"));
    }

    @Test
    public void isValidPhoneNumber_tooBig_returnsFalse() {
        Assert.assertFalse(isValidPhoneNumber("999999999999999999"));
    }

    @Test
    public void isValidPhoneNumber_valid8DigitNumber_returnsTrue() {
        Assert.assertTrue(isValidPhoneNumber("99999999"));
    }

    ///////////////////////////////////////////////////////////////////////////
    // isValidPhoneNumber - END
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // isValidCapacity - START
    ///////////////////////////////////////////////////////////////////////////
    @Test
    public void isValidCapacity_zeroInput_returnsFalse() {
        Assert.assertFalse(isCapacityValid(0));
    }

    @Test
    public void isValidCapacity_negativeInput_returnsFalse() {
        Assert.assertFalse(isCapacityValid(-2));
    }

    @Test
    public void isValidCapacity_validInput_returnsTrue() {
        Assert.assertTrue(isCapacityValid(20));
    }

    ///////////////////////////////////////////////////////////////////////////
    // isValidCapacity - END
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // isNameValid - START
    ///////////////////////////////////////////////////////////////////////////
    @Test
    public void isNameValid_withNull_returnsFalse() {
        Assert.assertFalse(isNameValid(null));
    }

    @Test
    public void isNameValid_withEmpty_returnsFalse() {
        Assert.assertFalse(isNameValid(""));
    }

    @Test
    public void isNameValid_withSpaces_returnsFalse() {
        Assert.assertFalse(isNameValid("   "));
    }

    @Test
    public void isNameValid_smallName_returnsFalse() {
        Assert.assertTrue(isNameValid("1"));
    }

    @Test
    public void isNameValid_largeName_returnsTrue() {
        Assert.assertTrue(isNameValid("1234567890-sdfghj"));
    }

    ///////////////////////////////////////////////////////////////////////////
    // isNameValid - END
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // isLotLatLngValid - START
    ///////////////////////////////////////////////////////////////////////////
    @Test
    public void isLotLatLngValid_withNull_returnsFalse() {
        Assert.assertFalse(isLotLatLngValid(null));
    }

    @Test
    public void isLotLatLngValid_withNonNull_returnsTrue() {
        Assert.assertTrue(isLotLatLngValid(new LatLng(1, 2)));
    }
    ///////////////////////////////////////////////////////////////////////////
    // isLotLatLngValid - END
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // AreSlotOffersValid - START
    ///////////////////////////////////////////////////////////////////////////
    @Test
    public void areSlotOffersValid_withEmptyList_returnsFalse() {
        Assert.assertFalse(areSlotOffersValid(new ArrayList<>()));
    }

    @Test
    public void areSlotOffersValid_withNullList_returnsFalse() {
        Assert.assertFalse(areSlotOffersValid(null));
    }

    @Test
    public void areSlotOffersValid_withValidList_returnsTrue() {
        Assert.assertTrue(areSlotOffersValid(new ArrayList<>(Collections
                .singletonList(SlotOffer.getRandomInstance(new Random())))));
    }
    ///////////////////////////////////////////////////////////////////////////
    // AreSlotOffersValid - END
    ///////////////////////////////////////////////////////////////////////////
}