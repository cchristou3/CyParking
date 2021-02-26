package io.github.cchristou3.CyParking.data.manager

import android.util.Log
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.*
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Purpose: encrypt and decrypt secrets of the application.
 *
 * @author Charalambos Christou
 * @since 25/02/21
 */
class EncryptionManager {

    /**
     * Initializes [mSecretKey] with an instance of
     * [SecretKeySpec] based on a the object's
     * [KEY].
     */
    init {
        mSecretKey = SecretKeySpec(
                KEY.toByteArray(CHARSET_NAME), // MUST BE 16 or 32 bytes
                ENCRYPTION_ALGORITHM
        )
    }

    companion object {
        val CHARSET_NAME: Charset = StandardCharsets.UTF_8
        private const val TAG = "EncryptionManagerTAG"
        private const val KEY = "EncryptionManagerAESGCMNoPadding" // TODO: 26/02/2021 dynamically add one depending on the user and the operator
        // Must be 16 or 32 byte long

        private const val ENCRYPTION_ALGORITHM = "AES/GCM/NoPadding"
        private const val GCM_IV_LENGTH = 12
        private lateinit var mSecretKey: SecretKey;
        var associatedData: ByteArray? = // TODO: 26/02/2021 dynamically add one depending on the user and the operator
                "ProtocolVersion1" // optional, additional (public) data to verify on decryption with GCM auth tag
                        .toByteArray(CHARSET_NAME) //meta data you want to verify with the secret message

        /**
         * Converts the given array of bytes into a hex String.
         *
         * @param byteArray to be translated.
         * @return The hex string representation of the given byte array.
         */
        @JvmStatic
        fun hex(byteArray: ByteArray): String {
            val result = StringBuilder()
            byteArray.forEach {
                result.append(String.format("%02x", it))
            }
            return result.toString()
        }

        /**
         * Converts the given hex String an array of bytes into.
         * **encodedMessage must be an even-length string.**
         *
         * @param encodedMessage to be translated.
         * @return The byte array representation of the given hex string.
         */
        @JvmStatic
        fun hexStringToByteArray(encodedMessage: String): ByteArray {
            val len = encodedMessage.length
            val data = ByteArray(len / 2)
            var i = 0
            while (i < len) {
                data[i / 2] = ((Character.digit(encodedMessage[i], 16) shl 4)
                        + Character.digit(encodedMessage[i + 1], 16)).toByte()
                i += 2
            }
            return data
        }
    }

    /**
     * Encrypt a plaintext with given key (see [decrypt]).
     *
     * @param plaintext to encrypt (utf-8 encoding will be used)
     * @return encrypted message
     * @throws NoSuchPaddingException             if anything goes wrong
     * @throws NoSuchAlgorithmException           if anything goes wrong
     * @throws InvalidAlgorithmParameterException if anything goes wrong
     * @throws InvalidKeyException                if anything goes wrong
     * @throws BadPaddingException                if anything goes wrong
     * @throws IllegalBlockSizeException          if anything goes wrong
     */
    @Throws(NoSuchPaddingException::class, NoSuchAlgorithmException::class,
            InvalidAlgorithmParameterException::class, InvalidKeyException::class,
            BadPaddingException::class, IllegalBlockSizeException::class)
    fun encrypt(plaintext: String): ByteArray {
        val iv = ByteArray(GCM_IV_LENGTH) // NEVER REUSE THIS IV WITH SAME KEY
        SecureRandom().nextBytes(iv)
        val cipher = Cipher.getInstance(mSecretKey.algorithm)
        val parameterSpec = GCMParameterSpec(128, iv) //128 bit auth tag length
        cipher.init(Cipher.ENCRYPT_MODE, mSecretKey, parameterSpec)

        if (associatedData != null) {
            cipher.updateAAD(associatedData)
        }

        val cipherText = cipher.doFinal(plaintext.toByteArray(CHARSET_NAME))

        Log.d(TAG, "encrypt: size is " + cipherText.size)

        val byteBuffer = ByteBuffer.allocate(iv.size + cipherText.size)
        byteBuffer.put(iv)
        byteBuffer.put(cipherText)
        Log.d(TAG, "Cipher has " + byteBuffer.array().size + " bytes.")

        return byteBuffer.array()
    }

    /**
     * Decrypts encrypted message (see [encrypt]).
     *
     * @param cipherMessage iv with cipher text
     * @return original plaintext
     * @throws NoSuchPaddingException             if anything goes wrong
     * @throws NoSuchAlgorithmException           if anything goes wrong
     * @throws InvalidAlgorithmParameterException if anything goes wrong
     * @throws InvalidKeyException                if anything goes wrong
     * @throws BadPaddingException                if anything goes wrong
     * @throws IllegalBlockSizeException          if anything goes wrong
     */
    @Throws(NoSuchPaddingException::class, NoSuchAlgorithmException::class, InvalidAlgorithmParameterException::class, InvalidKeyException::class, BadPaddingException::class, IllegalBlockSizeException::class)
    fun decrypt(cipherMessage: ByteArray): String {
        val cipher = Cipher.getInstance(mSecretKey.algorithm)
        // use first 12 bytes for iv
        Log.d(TAG, "Cipher has " + cipherMessage.size + " bytes.")
        val gcmIv: AlgorithmParameterSpec = GCMParameterSpec(128, cipherMessage, 0, GCM_IV_LENGTH)
        cipher.init(Cipher.DECRYPT_MODE, mSecretKey, gcmIv)
        if (associatedData != null) {
            cipher.updateAAD(associatedData)
        }
        //use everything from 12 bytes on as cipher text
        val plainText = cipher.doFinal(cipherMessage, GCM_IV_LENGTH, cipherMessage.size - GCM_IV_LENGTH)
        return String(plainText, CHARSET_NAME)
    }

}