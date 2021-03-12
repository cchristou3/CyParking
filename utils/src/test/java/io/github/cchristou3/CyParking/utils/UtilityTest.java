package io.github.cchristou3.CyParking.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static io.github.cchristou3.CyParking.utils.Utility.cloneList;
import static io.github.cchristou3.CyParking.utils.Utility.getVolume;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


/**
 * Unit tests for the {@link Utility} class.
 */
public class UtilityTest {

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

    ///////////////////////////////////////////////////////////////////////////
    // cloneList - START
    ///////////////////////////////////////////////////////////////////////////

    @Test
    public void cloneList_returnsDifferentReference() {
        List<Object> list = new ArrayList<>();

        // Check if the references
        assertThat(list == cloneList(list), is(false));
    }

    @Test
    public void cloneList_returnsDifferentReference_SameSize() {
        List<Object> list = new ArrayList<>();
        // Check if the references
        assertThat(list == cloneList(list), is(false));
        assertThat(list.size(), is(cloneList(list).size()));
    }

    @Test(expected = NullPointerException.class)
    public void cloneList_nullList_throwsException() {
        cloneList(null);
    }

}