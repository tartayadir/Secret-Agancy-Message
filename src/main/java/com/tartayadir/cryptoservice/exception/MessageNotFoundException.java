package com.tartayadir.cryptoservice.exception;

/**
 * MessageNotFoundException is a custom runtime exception that is thrown when a message with a specified ID is not found in the repository.
 *
 * <p>This exception is used to indicate that an operation has failed because the message could not be located,
 * typically when trying to retrieve or delete a message by its ID.</p>
 */
public class MessageNotFoundException extends RuntimeException {

    /**
     * Constructs a new MessageNotFoundException with the specified detail message.
     *
     * @param message The detail message, which provides more information about the cause of the exception.
     */
    public MessageNotFoundException(String message) {
        super(message);
    }
}
