package io.github.cchristou3.CyParking.utilities;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * Unit tests for the {@link ShaUtility} class.
 */
public class ShaUtilityTest {

    @Test
    public void digest_hash_works_as_expected() {
        // Given
        String input = "1";

        // When
        String output = ShaUtility.digest(input);

        // Then
        Assert.assertEquals("6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b", output);
    }

    @Test
    public void digest_hash_empty() {
        // Given
        String input = "";

        // When
        String output = ShaUtility.digest(input);

        // Then
        Assert.assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", output);
    }

    @Test
    public void digest_hash_with_null() {
        // Given
        String input = null;

        // When
        String output = ShaUtility.digest(input);

        // Then
        Assert.assertNull(output);
    }

    @Test
    public void bytesToHex_with_normal_values() {
        // Given
        byte[] input = new byte[10];
        Arrays.fill(input, (byte) 0);

        // When
        String output = ShaUtility.bytesToHex(input);

        // Then
        Assert.assertEquals("00000000000000000000", output);
    }

    @Test(expected = NullPointerException.class)
    public void bytesToHex_with_null() {
        // Given
        byte[] input = null;

        // When
        String output = ShaUtility.bytesToHex(input);

        // Then - See Annotation
    }

}