package com.tartayadir.cryptoservice.exception;

/**
 * DecryptionAttemptsReachedException is a custom exception that is thrown when the maximum number of decryption attempts is reached.
 *
 * <p>This exception extends {@link EncryptionOperationException} and is used specifically to handle cases where the recipient has
 * failed to correctly decrypt a message after the allowed number of attempts, leading to the message being deleted.</p>
 */
public class DecryptionAttemptsReachedException extends EncryptionOperationException {

    /**
     * Constructs a new DecryptionAttemptsReachedException with the specified detail message and cause.
     *
     * @param message The detail message, which provides more information about the cause of the exception.
     * @param cause The cause of the exception, which can be another throwable that triggered this exception.
     */
    public DecryptionAttemptsReachedException(String message, Throwable cause) {
        super(message, cause);
    }
}
