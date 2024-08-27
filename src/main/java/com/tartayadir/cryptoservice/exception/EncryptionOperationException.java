package com.tartayadir.cryptoservice.exception;

/**
 * EncryptionOperationException is a custom runtime exception that is thrown when an error occurs during an encryption or decryption operation.
 *
 * <p>This exception is the base class for other exceptions related to encryption and decryption processes within the crypto service,
 * such as errors in algorithm execution, invalid keys, or other security-related issues.</p>
 */
public class EncryptionOperationException extends RuntimeException {

    /**
     * Constructs a new EncryptionOperationException with the specified detail message and cause.
     *
     * @param message The detail message, which provides more information about the cause of the exception.
     * @param cause The cause of the exception, which can be another throwable that triggered this exception.
     */
    public EncryptionOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
