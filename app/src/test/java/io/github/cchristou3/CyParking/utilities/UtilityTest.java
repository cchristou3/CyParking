package io.github.cchristou3.CyParking.utilities;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

import static io.github.cchristou3.CyParking.utilities.Utility.checkIfFieldsValid;
import static io.github.cchristou3.CyParking.utilities.Utility.dateToString;
import static io.github.cchristou3.CyParking.utilities.Utility.fromStringToDate;
import static io.github.cchristou3.CyParking.utilities.Utility.getCurrentDate;
import static io.github.cchristou3.CyParking.utilities.Utility.getDateOf;
import static io.github.cchristou3.CyParking.utilities.Utility.getTimeOf;
import static io.github.cchristou3.CyParking.utilities.Utility.getVolume;
import static io.github.cchristou3.CyParking.utilities.Utility.isNearbyUser;

/**
 * Unit tests for the {@link Utility} class.
 */
public class UtilityTest {

    ///////////////////////////////////////////////////////////////////////////
    // dateToString(int, int, int) - START
    ///////////////////////////////////////////////////////////////////////////
    @Test
    public void dateToString_with_valid_ymd_values() {
        // Given
        int year = 2020, month = 11, day = 20;
        // When
        String output = dateToString(year, month, day);
        // Then
        Assert.assertEquals("20-11-2020", output);
    }


    @Test(expected = IllegalArgumentException.class) // Then
    public void dateToString_with_month_over_12() {
        // Given
        int year = 2020, month = 13, day = 20;
        // When
        String output = dateToString(year, month, day);
    }

    @Test(expected = IllegalArgumentException.class) // Then
    public void dateToString_with_negative_month() {
        // Given
        int year = 2020, month = -10, day = 20;
        // When
        String output = dateToString(year, month, day);
    }

    @Test(expected = IllegalArgumentException.class)// Then
    public void dateToString_with_day_over_21() {
        // Given
        int year = 2020, month = 10, day = 32;
        // When
        String output = dateToString(year, month, day);
    }

    @Test(expected = IllegalArgumentException.class)// Then
    public void dateToString_with_negative_day() {
        // Given
        int year = 2020, month = 10, day = -20;
        // When
        String output = dateToString(year, month, day);
    }

    ///////////////////////////////////////////////////////////////////////////
    // dateToString(int, int, int) - END
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // dateToString(Date) - START
    ///////////////////////////////////////////////////////////////////////////

    @Test
    public void testDateToString_with_zero_time() {
        // Given
        Date input = new Date(0);
        // When
        String output = dateToString(input);
        // Then
        Assert.assertEquals("01-01-1970", output);
    }

    ///////////////////////////////////////////////////////////////////////////
    // dateToString(Date) - END
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // getCurrentDate - START
    ///////////////////////////////////////////////////////////////////////////

    @Test
    public void getCurrentDate_greater_or_equals_to_todays_date() {
        Assert.assertFalse(getCurrentDate().compareTo(new Date()) >= 0);
    }

    @Test
    public void getCurrentDate_greater_than_date_of_time_zero() {
        Assert.assertFalse(getCurrentDate().compareTo(new Date(0)) < 0);
    }

    ///////////////////////////////////////////////////////////////////////////
    // getCurrentDate - END
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // fromStringToDate - START
    ///////////////////////////////////////////////////////////////////////////

    @Test(expected = ParseException.class) // Then
    public void fromStringToDate_invalid_input_format() throws ParseException {
        // Given
        String input = "12//////12/12";
        // When
        fromStringToDate(input);
    }

    @Test
    public void fromStringToDate_valid_input_format() throws ParseException {
        // Given
        String input = "08-01-2021";
        // When
        Date output = fromStringToDate(input);
        // Then
        Assert.assertEquals("Fri Jan 08 00:00:00 EET 2021", output.toString());
    }

    ///////////////////////////////////////////////////////////////////////////
    // fromStringToDate - END
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // isNearbyUser - START
    ///////////////////////////////////////////////////////////////////////////

    @Test
    public void isNearbyUser_in_same_position() {
        // Given
        LatLng latLng1 = new LatLng(1.00002D, 1.00002D);
        double lat2 = 1.00002D;
        double lng2 = 1.00002D;
        // When
        boolean output = isNearbyUser(latLng1, lat2, lng2);
        // Then
        Assert.assertTrue(output);
    }

    @Test
    public void isNearbyUser_out_of_range() {
        // Given
        LatLng latLng1 = new LatLng(2.00002D, 2.00002D);
        double lat2 = 1.00002D;
        double lng2 = 1.00002D;
        // When
        boolean output = isNearbyUser(latLng1, lat2, lng2);
        // Then
        Assert.assertFalse(output);
    }

    ///////////////////////////////////////////////////////////////////////////
    // isNearbyUser - END
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // getTimeOf - START
    ///////////////////////////////////////////////////////////////////////////

    @Test
    public void getTimeOf_single_digit_input() {
        // Given
        int hours = 1, minutes = 1;
        // When
        String output = getTimeOf(hours, minutes);
        // Then
        Assert.assertEquals("01 : 01", output);
    }

    @Test
    public void getTimeOf_double_digit_input() {
        // Given
        int hours = 10, minutes = 12;
        // When
        String output = getTimeOf(hours, minutes);
        // Then
        Assert.assertEquals("10 : 12", output);
    }

    @Test(expected = IllegalArgumentException.class)// Then
    public void getTimeOf_greater_hours() {
        // Given
        int hours = 24, minutes = 12;
        // When
        String output = getTimeOf(hours, minutes);
    }

    @Test(expected = IllegalArgumentException.class)// Then
    public void getTimeOf_negative_hours() {
        // Given
        int hours = -1, minutes = 12;
        // When
        String output = getTimeOf(hours, minutes);
    }

    @Test(expected = IllegalArgumentException.class)// Then
    public void getTimeOf_greater_minutes() {
        // Given
        int hours = 20, minutes = 60;
        // When
        String output = getTimeOf(hours, minutes);
    }

    @Test(expected = IllegalArgumentException.class)// Then
    public void getTimeOf_negative_minutes() {
        // Given
        int hours = 1, minutes = -1;
        // When
        String output = getTimeOf(hours, minutes);
    }

    ///////////////////////////////////////////////////////////////////////////
    // getTimeOf - END
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // getDateOf - START
    ///////////////////////////////////////////////////////////////////////////

    @Test
    public void getDateOf_with_valid_parameters() {
        // Given
        int[] years = {2001, 2020, 1950}, months = {1, 12, 4}, days = {1, 5, 8};
        String[] expectedDates = {"Mon Jan 01 00:00:00 EET 2001", "Sat Dec 05 00:00:00 EET 2020", "Sat Apr 08 00:00:00 EET 1950"};

        // When
        for (int i = 0; i < 3; i++) {
            getDateOf_with_valid_parameters(years[i], months[i], days[i], expectedDates[i]);
        }
    }

    public void getDateOf_with_valid_parameters(int year, int month, int day, String expectedResult) {
        // Given
        int y = year, m = month, d = day;

        // When
        Date output = getDateOf(y, m, d);

        // Then
        Assert.assertEquals(expectedResult, output.toString());
    }

    @Test(expected = IllegalArgumentException.class) // Then
    public void getDateOf_with_zero_year() {
        // Given
        int year = 0, month = 10, day = 20;

        // When
        Date output = getDateOf(year, month, day);
    }

    @Test(expected = IllegalArgumentException.class) // Then
    public void getDateOf_with_negative_year() {
        // Given
        int year = -10, month = 10, day = 20;

        // When
        Date output = getDateOf(year, month, day);
    }

    @Test(expected = IllegalArgumentException.class) // Then
    public void getDateOf_with_large_month() {
        // Given
        int year = 10, month = 30, day = 20;

        // When
        Date output = getDateOf(year, month, day);
    }

    @Test(expected = IllegalArgumentException.class) // Then
    public void getDateOf_with_zero_month() {
        // Given
        int year = 10, month = 0, day = 20;

        // When
        Date output = getDateOf(year, month, day);
    }

    @Test(expected = IllegalArgumentException.class) // Then
    public void getDateOf_with_greater_day() {
        // Given
        int year = 10, month = 10, day = 40;

        // When
        Date output = getDateOf(year, month, day);
    }

    @Test(expected = IllegalArgumentException.class) // Then
    public void getDateOf_with_zero_day() {
        // Given
        int year = 10, month = 10, day = 0;

        // When
        Date output = getDateOf(year, month, day);
    }

    ///////////////////////////////////////////////////////////////////////////
    // getDateOf - END
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // getVolume - START
    ///////////////////////////////////////////////////////////////////////////

    @Test
    public void getVolume_valid_parameters() {
        // Given
        float multiplicand = 1;
        int startFrom = 0, endTo = 3;
        // When
        String[] output = getVolume(multiplicand, startFrom, endTo);
        // Then
        Assert.assertArrayEquals(new String[]{"0.0", "1.0", "2.0"}, output);
    }

    @Test(expected = IllegalArgumentException.class) // Then
    public void getVolume_invalid_endTo() {
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
    // checkIfFieldsValid(int, int, int) - START
    ///////////////////////////////////////////////////////////////////////////

    @Test(expected = IllegalArgumentException.class)
    public void checkIfFieldsValid_invalid_year() {
        checkIfFieldsValid(0, 2, 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkIfFieldsValid_greater_month() {
        checkIfFieldsValid(1, 13, 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkIfFieldsValid_smaller_month() {
        checkIfFieldsValid(1, 0, 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkIfFieldsValid_greater_day() {
        checkIfFieldsValid(1, 12, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkIfFieldsValid_smaller_day() {
        checkIfFieldsValid(1, 1, 32);
    }

    ///////////////////////////////////////////////////////////////////////////
    // checkIfFieldsValid(int, int, int) - END
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // checkIfFieldsValid(int, int) - START
    ///////////////////////////////////////////////////////////////////////////

    @Test(expected = IllegalArgumentException.class)
    public void checkIfFieldsValid_greater_hour() {
        checkIfFieldsValid(24, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkIfFieldsValid_smaller_hour() {
        checkIfFieldsValid(-1, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkIfFieldsValid_greater_minute() {
        checkIfFieldsValid(1, 60);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkIfFieldsValid_smaller_minute() {
        checkIfFieldsValid(1, -1);
    }

    ///////////////////////////////////////////////////////////////////////////
    // checkIfFieldsValid(int, int) - END
    ///////////////////////////////////////////////////////////////////////////
}