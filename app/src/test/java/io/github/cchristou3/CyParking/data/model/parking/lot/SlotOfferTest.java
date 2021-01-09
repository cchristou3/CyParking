package io.github.cchristou3.CyParking.data.model.parking.lot;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

/**
 * Unit tests for the {@link SlotOffer} class.
 */
public class SlotOfferTest {

    ///////////////////////////////////////////////////////////////////////////
    // SlotOffer(float,float) - START
    ///////////////////////////////////////////////////////////////////////////
    @Test(expected = IllegalArgumentException.class)
    public void slotOffer_zero_duration() {
        new SlotOffer(0, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void slotOffer_negative_duration() {
        new SlotOffer(-1, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void slotOffer_zero_price() {
        new SlotOffer(2, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void slotOffer_negative_price() {
        new SlotOffer(2, -1);
    }
    ///////////////////////////////////////////////////////////////////////////
    // SlotOffer(float,float) - END
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // getRandomInstance - START
    ///////////////////////////////////////////////////////////////////////////
    @Test
    public void getRandomInstance_never_nulls() {
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
    public void toString_valid_return() {
        // Given
        SlotOffer slotOffer = new SlotOffer(1, 1);
        // When
        String offer = slotOffer.toString();
        // Then
        Assert.assertEquals("€" + slotOffer.getPrice() + " for " + slotOffer.getDuration() + " hours", offer);
    }
    ///////////////////////////////////////////////////////////////////////////
    // toString - END
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // getRatio - START
    ///////////////////////////////////////////////////////////////////////////
    @Test
    public void getRatio_duration_price_are_same() {
        // Given
        SlotOffer slotOffer = new SlotOffer(1, 1);
        // When
        float ratio = slotOffer.getRatio();
        // Then
        Assert.assertEquals(1, ratio, 1);
    }

    @Test
    public void getRatio_duration_greater_than_price() {
        // Given
        SlotOffer slotOffer = new SlotOffer(2, 1);
        // When
        float ratio = slotOffer.getRatio();
        // Then
        Assert.assertEquals(0.f, ratio, 0.5f);
    }

    @Test
    public void getRatio_price_greater_than_duration() {
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
    public void smallerOf_with_null() {
        // Given
        SlotOffer a = new SlotOffer(1, 2);
        SlotOffer b = null;
        // When
        boolean AsmallerOfB = a.smallerOf(b);
        // Then
        Assert.assertTrue(AsmallerOfB);
    }

    @Test
    public void smallerOf_with_a_smaller_b() {
        // Given
        SlotOffer a = new SlotOffer(1, 2);
        SlotOffer b = new SlotOffer(2, 2);
        // When
        boolean AsmallerOfB = a.smallerOf(b);
        // Then
        Assert.assertFalse(AsmallerOfB);
    }

    @Test
    public void smallerOf_with_b_smaller_a() {
        // Given
        SlotOffer a = new SlotOffer(2, 2);
        SlotOffer b = new SlotOffer(1, 2);
        // When
        boolean AsmallerOfB = a.smallerOf(b);
        // Then
        Assert.assertTrue(AsmallerOfB);
    }
    ///////////////////////////////////////////////////////////////////////////
    // smallerOf - END
    ///////////////////////////////////////////////////////////////////////////
}