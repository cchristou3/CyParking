package io.github.cchristou3.CyParking.view.utility;

import org.jetbrains.annotations.NotNull;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Purpose: <p>Contains methods used to hash data</p>
 *
 * @author Charalambos Christou
 * @version 1.0 05/11/20
 */
public class ShaUtility {

    private static final String SHA256 = "SHA-256";

    @NotNull
    public static String digest(byte[] input) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance(SHA256);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
        byte[] result = md.digest(input);
        return bytesToHex(result);
    }

    @NotNull
    private static String bytesToHex(@NotNull byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
