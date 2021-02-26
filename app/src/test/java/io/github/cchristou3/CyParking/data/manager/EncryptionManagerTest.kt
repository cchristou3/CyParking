package io.github.cchristou3.CyParking.data.manager

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Test
import org.junit.runner.RunWith
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.BadPaddingException
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException

/**
 * Unit tests for the [EncryptionManager].
 */
@RunWith(AndroidJUnit4::class)
class EncryptionManagerTest {

    var managerEnc = EncryptionManager()

    @Test
    @Throws(NoSuchPaddingException::class, InvalidKeyException::class, NoSuchAlgorithmException::class, IllegalBlockSizeException::class, BadPaddingException::class, InvalidAlgorithmParameterException::class)
    fun encrypt_longMessage_decryptReturnsOriginalMessage() {
        // Given we want to securely encrypt and decrypt a message without changing its contents
        val message = "What is Lorem Ipsum?\n" +
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum."

        // Encryption process //
        // Encrypt a message -> byte array.
        val cipherInBytes = managerEnc.encrypt(message)
        // Then convert the byte array into a string
        val cipherInString = EncryptionManager.hex(cipherInBytes)

        // Decryption process //
        // Convert string back to bytes
        val cipherBackToBytes = EncryptionManager.hexStringToByteArray(cipherInString)
        val originalMessage = managerEnc.decrypt(cipherBackToBytes)

        MatcherAssert.assertThat(message,
                CoreMatchers.`is`(originalMessage))
    }

    @Test
    @Throws(NoSuchPaddingException::class, InvalidKeyException::class, NoSuchAlgorithmException::class, IllegalBlockSizeException::class, BadPaddingException::class, InvalidAlgorithmParameterException::class)
    fun encrypt_emptyMessage_decryptReturnsOriginalMessage() {
        // Given we want to securely encrypt and decrypt a message without changing its contents
        val message = ""

        // Encryption process //
        // Encrypt a message -> byte array.
        val cipherInBytes = managerEnc.encrypt(message)
        // Then convert the byte array into a string
        val cipherInString = EncryptionManager.hex(cipherInBytes)

        // Decryption process //
        // Convert string back to bytes
        val cipherBackToBytes = EncryptionManager.hexStringToByteArray(cipherInString)
        val originalMessage = managerEnc.decrypt(cipherBackToBytes)

        MatcherAssert.assertThat(message,
                CoreMatchers.`is`(originalMessage))
    }

    @Test
    @Throws(NoSuchPaddingException::class, InvalidKeyException::class, NoSuchAlgorithmException::class, IllegalBlockSizeException::class, BadPaddingException::class, InvalidAlgorithmParameterException::class)
    fun encrypt_whiteSpaceMessage_decryptReturnsOriginalMessage() {
        // Given we want to securely encrypt and decrypt a message without changing its contents
        val message = " "

        // Encryption process //
        // Encrypt a message -> byte array.
        val cipherInBytes = managerEnc.encrypt(message)
        // Then convert the byte array into a string
        val cipherInString = EncryptionManager.hex(cipherInBytes)

        // Decryption process //
        // Convert string back to bytes
        val cipherBackToBytes = EncryptionManager.hexStringToByteArray(cipherInString)
        val originalMessage = managerEnc.decrypt(cipherBackToBytes)

        MatcherAssert.assertThat(message,
                CoreMatchers.`is`(originalMessage))
    }

    @Test
    @Throws(NoSuchPaddingException::class, InvalidKeyException::class, NoSuchAlgorithmException::class, IllegalBlockSizeException::class, BadPaddingException::class, InvalidAlgorithmParameterException::class)
    fun encrypt_160bitMessage_decryptReturnsOriginalMessage() {
        // Given we want to securely encrypt and decrypt a message without changing its contents
        val message = "C9327616F6130346DD0E6F52A0F7C859172CB69C"

        // Encryption process //
        // Encrypt a message -> byte array.
        val cipherInBytes = managerEnc.encrypt(message)
        // Then convert the byte array into a string
        val cipherInString = EncryptionManager.hex(cipherInBytes)

        // Decryption process //
        // Convert string back to bytes
        val cipherBackToBytes = EncryptionManager.hexStringToByteArray(cipherInString)
        val originalMessage = managerEnc.decrypt(cipherBackToBytes)

        MatcherAssert.assertThat(message,
                CoreMatchers.`is`(originalMessage))
    }

    @Test
    @Throws(NoSuchPaddingException::class, InvalidKeyException::class, NoSuchAlgorithmException::class, IllegalBlockSizeException::class, BadPaddingException::class, InvalidAlgorithmParameterException::class)
    fun encrypt_256bitMessage_decryptReturnsOriginalMessage() {
        // Given we want to securely encrypt and decrypt a message without changing its contents
        val message = "DE55BEFC0C74E5F41712D6FE9DB6B465A7A0924EAD5F7772C3ED21FF09498721"

        // Encryption process //
        // Encrypt a message -> byte array.
        val cipherInBytes = managerEnc.encrypt(message)
        // Then convert the byte array into a string
        val cipherInString = EncryptionManager.hex(cipherInBytes)

        // Decryption process //
        // Convert string back to bytes
        val cipherBackToBytes = EncryptionManager.hexStringToByteArray(cipherInString)
        val originalMessage = managerEnc.decrypt(cipherBackToBytes)

        MatcherAssert.assertThat(message,
                CoreMatchers.`is`(originalMessage))
    }

    @Test
    @Throws(NoSuchPaddingException::class, InvalidKeyException::class, NoSuchAlgorithmException::class, IllegalBlockSizeException::class, BadPaddingException::class, InvalidAlgorithmParameterException::class)
    fun encrypt_512bitMessage_decryptReturnsOriginalMessage() {
        // Given we want to securely encrypt and decrypt a message without changing its contents
        val message = "8A85FF63A97923B2BE265C3D0A507D6C0A6BDD9C9F46DB06D2FC0FC3EA39FEFC549540EDF0AFE2ED6ABB8754208B206E7A75C1BC64129DA148D8D348CC8FB3E8"

        // Encryption process //
        // Encrypt a message -> byte array.
        val cipherInBytes = managerEnc.encrypt(message)
        // Then convert the byte array into a string
        val cipherInString = EncryptionManager.hex(cipherInBytes)

        // Decryption process //
        // Convert string back to bytes
        val cipherBackToBytes = EncryptionManager.hexStringToByteArray(cipherInString)
        val originalMessage = managerEnc.decrypt(cipherBackToBytes)

        MatcherAssert.assertThat(message,
                CoreMatchers.`is`(originalMessage))
    }

    @Test
    @Throws(NoSuchPaddingException::class, InvalidKeyException::class, NoSuchAlgorithmException::class, IllegalBlockSizeException::class, BadPaddingException::class, InvalidAlgorithmParameterException::class)
    fun encrypt_2048CharactersMessage_decryptReturnsOriginalMessage() {
        // Given we want to securely encrypt and decrypt a message without changing its contents
        val message = "HEhCZ4Y5qEdemGBwCnCPzM7xFqLD2HZ4EGPD6xYymYMBsvF9tHHzymxgf7MkbWTzr32Lr9ySbx8eeUAJ9Y8y9dGNxwnFdAbcNxW5Wt9dqE3PkEGhbKynYM2JhK4n377pMzeB3fktg4wFuqkKbsAvBmHkcFQ3wkEvdV9MXgXB8XTTfgg5Uuwc3ZXG9vwp8scX5mMGFKN6rdekJnDJGSqtRLJ3eEw97r5KEfPabf38dMAmwzTVPtNaYYhJHuHaYDZFjTfLhpEaESsCSGwJuukK4eK3KD6UmzqFtX83JvMRYGJ7BHXYfyuJ7F4rv49hsZgM6UwWVCGPDy2Xbqe8Yt9Lgpexzqukv29HsDgAF6LYW2n9LfDJp3wYZXgDsZeD6ktjJ5jpZH9xM7xKPaXWywMdPRW7NhHYNVHR36Gfv6xqKNzQSCG3BQR3ks7arE8btk82XrZvUGKSHvfurzLT8Y8s286G2djhQNxvwu6LUHPJ76TdabxerrAZgsnDrMkarjsCGWPyHsVSCFQFAF6223T8y76vrU3SHjNGkakyf6jA967x2r9XNn6RJNJhYPyefB2hDdphptRaqSzJqVCecg2wmCtdZeeRAZd4net26uqwv9JJ3KHxaheWdZJHZz6FTFJRLdTXxgpfyHsWezqASAwVJE5TySKAuSvyk94ACsgGHhQabpxu68w3Lfwe498ZNVUEmjFsADMNnHVmmaaJQxbrbHZDYEAQwfrT7f2uJZ2e29n8Hv64aH3xUYMmARBtLbsjeeXFAutzBzZCgFJQSAv6kZu2VQZHNwaJ2rPqBFZDGUbfUMgYYXuUYuXed9KVgSfE3sgw3M8pa7pVRHLeNUusLG7G3DXKyZsWNDqTuzV4fwm7zuWd2fLhuHgERDftFR9HZaqccXPGxa7Rb4bvfdMwFm7ShgkvjR9dcvwM7WVzTRnBr5F6mJM7R6SfhxLA7pWgzyn29yw8GwURx3Trz3DhJd6DyfW5yCsbTBwqA7vpz2NEAu73Vt9CmySz9STW7cAQjmtJEtaJDQsaLTwENy9XBdjk9R8VSHVE8SJHYbtHmeDAYfX7YwQYb9sTvs3c52uL8NVUHj6EeNxRcurNuADau6XKJbhJLcb3cKZcphTf7BSx4xkqU9M6BDhQTDQjHBz8cNwHDQqw8FcPgrerFCBY3yZYhNkUWRPRTFFg6GvxR9aqTmNdUUKeJDcudFAayFMGC2UXXBCAdNM6a5uM6mx4h2S3upqqzage8FBtZHJ69urj6PFefguHVCKWr3JMCQXMDdfYYJzXTV7DYQ2ZLGVJWFGF4tTZAgNNg52cEY5W2svggLeLFFbdAeZ5E3Mp53UUXTHMPmkdLbEaDh454vv5fa3aMMtaWXB3rvrpwuGWSyHKgbGPvczbESHPKnPLe9zwyhUXxMpf8Y9q4u6e9LDvN5JdBSJqqDjx6qdemxGYzsSf9GzWZktmU8NQLZSrfbfMf3CBEDtdj8XCbJ8wMchSx6S8HMf8b44czL58k9jAQm9gWQSMUCU8hhxHZhQYck7PVWHZpPJG6bLZq3TvPFxeWCnMgLPWZzhqnc9m8hZBEgzVhJPg9vaqKXTRYzBqTWXycwp5PvzgFXaQqQtmmMhSURXmUvCNTvbPJK3BsCsE7RRd3YQHtMHtv7X6mcGNAFpP5aAyrG44LeW5r66DtKbhBBw25YuB5Qgw2P82q6sJZDhcfwwfpqRbxL2UGE9mBZGPrHwWF6fJVq8f4NKcdG5xpJP5uHBwaGWBttWe7VxPZMZvDZK7cn5mpxhEtdaFpbkraQVdj2XapfbmpL6QbPpDjrnYY8MTMZZLauKgqq7WRG2NEcCCec5cvPj6JtRBkmg3R5AvhBpfTRYNaVe7u58VqbZwEZaS7T3VKcsaCTMMh9Z3uxCn5tW8pLRKMUcDfvVaMR97gpfwQv7NTN7VeKJkLLHdcaaFYt6vtSeAP8q5fsJYmTDEQu6b3ZGGJNnXQ3gPtLdWj6yY5UQfE7c5TDyW9bmdAvYRCuH944qyPkzQNxxxCADHHL8y4Sf2F8yQv2T9"

        // Encryption process //
        // Encrypt a message -> byte array.
        val cipherInBytes = managerEnc.encrypt(message)
        // Then convert the byte array into a string
        val cipherInString = EncryptionManager.hex(cipherInBytes)

        // Decryption process //
        // Convert string back to bytes
        val cipherBackToBytes = EncryptionManager.hexStringToByteArray(cipherInString)
        val originalMessage = managerEnc.decrypt(cipherBackToBytes)

        MatcherAssert.assertThat(message,
                CoreMatchers.`is`(originalMessage))
    }

    /**
     * Here we are testing whether the encrypting
     * will generate a unique cipher with each call.
     * We are using an empty string as it is the
     * smallest message we can have. The smaller
     * the more likely it is to generate the same
     * cipher.
     */
    @Test
    fun encrypt_ReturnsNewCipherWithEachCall() {
        // Given we want encrypt a message multiple times
        val message = ""

        val listOfCiphers = mutableListOf<String>()
        for (i in 0..1000) {
            listOfCiphers.add(EncryptionManager.hex(managerEnc.encrypt(message)))
        }

        MatcherAssert.assertThat(listOfCiphers.size,
                // Should have the same size if there were no duplicates
                CoreMatchers.`is`(listOfCiphers.toSet().size))

    }
}