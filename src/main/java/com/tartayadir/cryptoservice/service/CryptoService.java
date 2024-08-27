package com.tartayadir.cryptoservice.service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * The CryptoService interface defines the cryptographic operations required by the crypto service.
 *
 * <p>This includes generating encryption keys, encrypting messages, and decrypting messages. Implementations
 * of this interface are responsible for ensuring that the cryptographic operations are performed securely and
 * according to the specified algorithms and parameters.</p>
 */
public interface CryptoService {

    /**
     * Generates a new encryption key for use in encrypting messages.
     *
     * @return The generated encryption key as a Base64 encoded string.
     * @throws NoSuchAlgorithmException if the specified algorithm for key generation is not available.
     * @throws NoSuchProviderException if the specified provider for key generation is not available.
     */
    String generateKey() throws NoSuchAlgorithmException, NoSuchProviderException;

    /**
     * Encrypts a given message using the provided encryption key.
     *
     * @param message The plaintext message to encrypt.
     * @param key The encryption key to use, encoded in Base64.
     * @return The encrypted message as a Base64 encoded string.
     * @throws NoSuchAlgorithmException if the specified algorithm for encryption is not available.
     * @throws NoSuchProviderException if the specified provider for encryption is not available.
     * @throws NoSuchPaddingException if the specified padding mechanism is not available.
     * @throws InvalidAlgorithmParameterException if the specified algorithm parameters are not valid.
     * @throws InvalidKeyException if the provided key is invalid.
     * @throws IllegalBlockSizeException if the provided block size is invalid.
     * @throws BadPaddingException if padding is applied incorrectly.
     */
    String encrypt(String message, String key) throws NoSuchAlgorithmException, NoSuchProviderException,
            NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException;

    /**
     * Decrypts a given encrypted message using the provided decryption key.
     *
     * @param message The encrypted message to decrypt, encoded in Base64.
     * @param key The decryption key to use, encoded in Base64.
     * @return The decrypted plaintext message.
     * @throws NoSuchAlgorithmException if the specified algorithm for decryption is not available.
     * @throws NoSuchProviderException if the specified provider for decryption is not available.
     * @throws NoSuchPaddingException if the specified padding mechanism is not available.
     * @throws InvalidAlgorithmParameterException if the specified algorithm parameters are not valid.
     * @throws InvalidKeyException if the provided key is invalid.
     * @throws IllegalBlockSizeException if the provided block size is invalid.
     * @throws BadPaddingException if padding is applied incorrectly.
     */
    String decrypt(String message, String key) throws NoSuchAlgorithmException, NoSuchProviderException,
            NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException;
}
