package io.github.cchristou3.CyParking.utils;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

import static io.github.cchristou3.CyParking.utils.DateTimeUtility.checkIfFieldsValid;
import static io.github.cchristou3.CyParking.utils.DateTimeUtility.dateToString;
import static io.github.cchristou3.CyParking.utils.DateTimeUtility.fromStringToDate;
import static io.github.cchristou3.CyParking.utils.DateTimeUtility.getCurrentDate;
import static io.github.cchristou3.CyParking.utils.DateTimeUtility.getDateOf;
import static io.github.cchristou3.CyParking.utils.DateTimeUtility.isGreaterOrEqualToCurrentDate;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for the {@link DateTimeUtility} class.
 */
public class DateTimeUtilityTest {

    ///////////////////////////////////////////////////////////////////////////
    // dateToString(int, int, int) - START
    ///////////////////////////////////////////////////////////////////////////
    @Test
    public void dateToString_validYMD_returnsSameYMD() {
        // Given
        int year = 2020, month = 11, day = 20;
        // When
        String output = dateToString(year, month, day);
        // Then
        assertEquals("20/11/2020", output);
    }

    // subjectUnderTest_actionOrInput_resultState
    @Test(expected = IllegalArgumentException.class) // Then
    public void dateToString_monthOver12_throwsException() {
        // Given
        int year = 2020, month = 13, day = 20;
        // When
        String output = dateToString(year, month, day);
    }

    @Test(expected = IllegalArgumentException.class) // Then
    public void dateToString_negativeMonth_throwsException() {
        // Given
        int year = 2020, month = -10, day = 20;
        // When
        String output = dateToString(year, month, day);
    }

    @Test(expected = IllegalArgumentException.class)// Then
    public void dateToString_dayOver21_throwsException() {
        // Given
        int year = 2020, month = 10, day = 32;
        // When
        String output = dateToString(year, month, day);
    }

    @Test(expected = IllegalArgumentException.class)// Then
    public void dateToString_negativeDay_throwsException() {
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
    public void testDateToString_zeroTime_returnsFirstDate() {
        // Given
        Date input = new Date(0);
        // When
        String output = dateToString(input);
        // Then
        assertEquals("01/01/1970", output);
    }

    ///////////////////////////////////////////////////////////////////////////
    // dateToString(Date) - END
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // getCurrentDate - START
    ///////////////////////////////////////////////////////////////////////////

    @Test
    public void getCurrentDate_greaterOrEqualsToTodayDate_returnsNumberGreaterOrEqualToZero() {
        Assert.assertFalse(getCurrentDate().compareTo(new Date()) >= 0);
    }

    @Test
    public void getCurrentDate_greaterThanDateOfTimeZero_returnsNegativeNumber() {
        Assert.assertFalse(getCurrentDate().compareTo(new Date(0)) < 0);
    }

    ///////////////////////////////////////////////////////////////////////////
    // getCurrentDate - END
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // fromStringToDate - START
    ///////////////////////////////////////////////////////////////////////////

    @Test(expected = ParseException.class) // Then
    public void fromStringToDate_invalidInputFormat_throwsException() throws ParseException {
        // Given
        String input = "12//////12/12";
        // When
        fromStringToDate(input);
    }

    @Test
    public void fromStringToDate_validInputFormat_returnsExpectedString() throws ParseException {
        // Given
        String input = "08/01/2021";
        // When
        Date output = fromStringToDate(input);
        // Then
        assertEquals("Fri Jan 08 00:00:00 EET 2021", output.toString());
    }

    ///////////////////////////////////////////////////////////////////////////
    // getDateOf - START
    ///////////////////////////////////////////////////////////////////////////

    @Test
    public void getDateOf_validParameters_returnsExpectedString() {
        // Given
        int[] years = {2001, 2020, 1950}, months = {1, 11, 4}, days = {1, 5, 8};
        String[] expectedDates = {"Mon Jan 01 00:00:00 EET 2001", "Thu Nov 05 00:00:00 EET 2020", "Sat Apr 08 00:00:00 EET 1950"};

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
        assertEquals(expectedResult, output.toString());
    }

    @Test(expected = IllegalArgumentException.class) // Then
    public void getDateOf_zeroYear_throwsException() {
        // Given
        int year = 0, month = 10, day = 20;

        // When
        Date output = getDateOf(year, month, day);
    }

    @Test(expected = IllegalArgumentException.class) // Then
    public void getDateOf_negativeYear_throwsException() {
        // Given
        int year = -10, month = 10, day = 20;

        // When
        Date output = getDateOf(year, month, day);
    }

    @Test(expected = IllegalArgumentException.class) // Then
    public void getDateOf_largeMonth_throwsException() {
        // Given
        int year = 10, month = 30, day = 20;

        // When
        Date output = getDateOf(year, month, day);
    }

    @Test(expected = IllegalArgumentException.class) // Then
    public void getDateOf_negativeMonth_throwsException() {
        // Given
        int year = 10, month = -1, day = 20;

        // When
        Date output = getDateOf(year, month, day);
    }

    @Test(expected = IllegalArgumentException.class) // Then
    public void getDateOf_greaterDay_throwsException() {
        // Given
        int year = 10, month = 10, day = 40;

        // When
        Date output = getDateOf(year, month, day);
    }

    @Test(expected = IllegalArgumentException.class) // Then
    public void getDateOf_zeroDay_throwsException() {
        // Given
        int year = 10, month = 10, day = 0;

        // When
        Date output = getDateOf(year, month, day);
    }

    ///////////////////////////////////////////////////////////////////////////
    // getDateOf - END
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // checkIfFieldsValid(int, int, int) - START
    ///////////////////////////////////////////////////////////////////////////

    @Test(expected = IllegalArgumentException.class)
    public void checkIfFieldsValid_invalidYear_throwsException() {
        checkIfFieldsValid(0, 2, 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkIfFieldsValid_greaterMonth_throwsException() {
        checkIfFieldsValid(1, 13, 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkIfFieldsValid_smallerMonth_throwsException() {
        checkIfFieldsValid(1, -1, 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkIfFieldsValid_greaterDay_throwsException() {
        checkIfFieldsValid(1, 12, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkIfFieldsValid_smallerDay_throwsException() {
        checkIfFieldsValid(1, 1, 32);
    }

    ///////////////////////////////////////////////////////////////////////////
    // checkIfFieldsValid(int, int, int) - END
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // isGreaterOrEqualToCurrentDate(date) - START
    ///////////////////////////////////////////////////////////////////////////

    @Test
    public void isGreaterOrEqualToCurrentDate_sameDate_returnsTrue() {
        assertThat(isGreaterOrEqualToCurrentDate(getCurrentDate()), is(true));
    }

    @Test
    public void isGreaterOrEqualToCurrentDate_newDate_returnsTrue() {
        assertThat(isGreaterOrEqualToCurrentDate(new Date()), is(true));
    }

    ///////////////////////////////////////////////////////////////////////////
    // isGreaterOrEqualToCurrentDate(date) - END
    ///////////////////////////////////////////////////////////////////////////
}