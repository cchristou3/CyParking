package io.github.cchristou3.CyParking.utilities;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Assert;
import org.junit.Test;

import static io.github.cchristou3.CyParking.utilities.Utility.getDistanceApart;
import static io.github.cchristou3.CyParking.utilities.Utility.getVolume;

/**
 * Unit tests for the {@link Utility} class.
 */
public class UtilityTest {

    ///////////////////////////////////////////////////////////////////////////
    // fromStringToDate - END
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // isNearbyUser - START
    ///////////////////////////////////////////////////////////////////////////

    @Test
    public void getDistanceApart_inSamePosition_returnsZero() {
        // Given
        LatLng latLng1 = new LatLng(1.00002D, 1.00002D);
        LatLng latLng2 = new LatLng(1.00002D, 1.00002D);

        // When
        double output = getDistanceApart(latLng1, latLng2);
        // Then
        Assert.assertEquals(0.0D, output, 0.0D);
    }

    @Test
    public void getDistanceApart_outOfRange_returnsNoZero() {
        // Given
        LatLng latLng1 = new LatLng(2.00002D, 2.00002D);
        LatLng latLng2 = new LatLng(1.00002D, 1.00002D);
        // When
        double output = getDistanceApart(latLng1, latLng2);
        // Then
        Assert.assertNotEquals(0.0D, output, 0.0);
    }

    ///////////////////////////////////////////////////////////////////////////
    // isNearbyUser - END
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // getVolume - START
    ///////////////////////////////////////////////////////////////////////////

    @Test
    public void getVolume_validParameters_returnsExpectedArray() {
        // Given
        float multiplicand = 1;
        int startFrom = 0, endTo = 3;
        // When
        String[] output = getVolume(multiplicand, startFrom, endTo);
        // Then
        Assert.assertArrayEquals(new String[]{"0.0", "1.0", "2.0"}, output);
    }

    @Test(expected = IllegalArgumentException.class) // Then
    public void getVolume_invalidEndTo_throwsException() {
        // Given
        float multiplicand = 1;
        int startFrom = 0, endTo = -3;
        // When
        String[] output = getVolume(multiplicand, startFrom, endTo);
    }

    ///////////////////////////////////////////////////////////////////////////
    // getVolume - END
    ///////////////////////////////////////////////////////////////////////////
}