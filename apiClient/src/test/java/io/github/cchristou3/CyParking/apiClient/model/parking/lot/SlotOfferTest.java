package io.github.cchristou3.CyParking.apiClient.model.parking.lot;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

import io.github.cchristou3.CyParking.apiClient.model.data.parking.lot.SlotOffer;
import io.github.cchristou3.CyParking.utils.Utility;

/**
 * Unit tests for the {@link SlotOffer} class.
 */
public class SlotOfferTest {

    ///////////////////////////////////////////////////////////////////////////
    // SlotOffer(float,float) - START
    ///////////////////////////////////////////////////////////////////////////
    @Test(expected = IllegalArgumentException.class)
    public void slotOffer_zeroDuration_throwsException() {
        new SlotOffer(0, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void slotOffer_negativeDuration_throwsException() {
        new SlotOffer(-1, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void slotOffer_zeroPrice_throwsException() {
        new SlotOffer(2, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void slotOffer_negativePrice_throwsException() {
        new SlotOffer(2, -1);
    }
    ///////////////////////////////////////////////////////////////////////////
    // SlotOffer(float,float) - END
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // getRandomInstance - START
    ///////////////////////////////////////////////////////////////////////////
    @Test
    public void getRandomInstance_neverReturnsNulls_true() {
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            Assert.assertNotNull(SlotOffer.getRandomInstance(random));
        }
    }
    ///////////////////////////////////////////////////////////////////////////
    // getRandomInstance - END
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // toString - START
    ///////////////////////////////////////////////////////////////////////////

    @Test
    public void toString_validObject_returnExpectedString() {
        // Given
        SlotOffer slotOffer = new SlotOffer(1, 1);
        // When
        String offer = slotOffer.toString();
        // Then
        Assert.assertEquals(Utility.getCurrency().getSymbol() + slotOffer.getPrice() + " for " + slotOffer.getDuration() + " hours", offer);
    }
    ///////////////////////////////////////////////////////////////////////////
    // toString - END
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // getRatio - START
    ///////////////////////////////////////////////////////////////////////////
    @Test
    public void getRatio_durationPriceAreSame_returnOne() {
        // Given
        SlotOffer slotOffer = new SlotOffer(1, 1);
        // When
        float ratio = slotOffer.getRatio();
        // Then
        Assert.assertEquals(1, ratio, 1);
    }

    @Test
    public void getRatio_durationGreaterThanPrice_returnsSmallerThanOne() {
        // Given
        SlotOffer slotOffer = new SlotOffer(2, 1);
        // When
        float ratio = slotOffer.getRatio();
        // Then
        Assert.assertEquals(0.5f, ratio, 0.5f);
    }

    @Test
    public void getRatio_priceGreaterThanDuration_returnsGreaterThanOne() {
        // Given
        SlotOffer slotOffer = new SlotOffer(1, 2);
        // When
        float ratio = slotOffer.getRatio();
        // Then
        Assert.assertEquals(2, ratio, 2);
    }
    ///////////////////////////////////////////////////////////////////////////
    // getRatio - END
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // smallerOf - START
    ///////////////////////////////////////////////////////////////////////////
    @Test
    public void smallerOf_null_returnsNonNullObject() {
        // Given
        SlotOffer a = new SlotOffer(1, 2);
        SlotOffer b = null;
        // When
        boolean AsmallerOfB = a.smallerOf(b);
        // Then
        Assert.assertTrue(AsmallerOfB);
    }

    @Test
    public void smallerOf_aSmallerB_returnsTrueForA() {
        // Given
        SlotOffer a = new SlotOffer(1, 2);
        SlotOffer b = new SlotOffer(2, 2);
        // When
        boolean AsmallerOfB = a.smallerOf(b);
        // Then
        Assert.assertFalse(AsmallerOfB);
    }

    @Test
    public void smallerOf_bSmallerA_returnsTrueForB() {
        // Given
        SlotOffer a = new SlotOffer(2, 2);
        SlotOffer b = new SlotOffer(1, 2);
        // When
        boolean BsmallerOfA = a.smallerOf(b);
        // Then
        Assert.assertTrue(BsmallerOfA);
    }
    ///////////////////////////////////////////////////////////////////////////
    // smallerOf - END
    ///////////////////////////////////////////////////////////////////////////
}