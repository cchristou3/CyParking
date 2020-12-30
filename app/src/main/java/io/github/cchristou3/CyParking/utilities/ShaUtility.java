package io.github.cchristou3.CyParking.utilities;

import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Purpose: <p>Contains methods used to hash data</p>
 * TODO: If class is being used frequently, make singleton
 * as the call to MessageDigest#getInstance is rather expensive.
 *
 * @author Charalambos Christou
 * @version 2.0 07/11/20
 */
public class ShaUtility {

    private static final String SHA256 = "SHA-256";
    private static final String UTF_8 = "UTF-8";

    /**
     * Hashes the specified input using a hashing algorithm
     *
     * @param input The String to be hashed
     * @return The hashed version (digest) of the given String
     */
    @NotNull
    public static String digest(@NotNull String input) {
        try {
            byte[] bytesOfInput = input.getBytes(UTF_8);
            byte[] result = MessageDigest.getInstance(SHA256).digest(bytesOfInput);
            return bytesToHex(result);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            return input;
        }
    }

    /**
     * Converts the specified array of bytes into a String
     *
     * @param bytes An array of bytes
     * @return The corresponding String of the given bytes
     */
    @NotNull
    private static String bytesToHex(@NotNull byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
