package com.tartayadir.cryptoservice.service;

import com.tartayadir.cryptoservice.domain.message.Message;

import java.time.LocalDateTime;

/**
 * The MessageService interface defines the operations related to managing secret messages within the crypto service.
 * It provides methods for saving, updating, retrieving, and deleting messages, as well as a method for deleting old messages.
 *
 * <p>The service ensures that messages are securely handled, including encryption and decryption processes, and
 * enforces business rules such as message deletion after a certain number of failed decryption attempts or after being accessed.</p>
 */
public interface MessageService {

    /**
     * Finds a message by its unique identifier.
     *
     * @param id The ID of the message to find.
     * @return The found Message, or throws an exception if not found.
     */
    Message findById(String id);

    /**
     * Retrieves and decrypts a message by its ID and the provided decryption key.
     * If the decryption is successful, the message is deleted.
     *
     * @param id The ID of the message to retrieve.
     * @param key The decryption key associated with the message.
     * @return The decrypted message content.
     */
    String getEncryptedMessage(String id, String key);

    /**
     * Saves a new message to the repository, encrypting it before storage.
     *
     * @param message The Message entity to save.
     * @return The saved Message entity with the encryption key.
     */
    Message save(Message message);

    /**
     * Updates an existing message's metadata, such as retry count.
     *
     * @param message The Message entity with updated information.
     * @return The updated Message entity.
     */
    Message update(Message message);

    /**
     * Deletes a message by its unique identifier.
     *
     * @param id The ID of the message to delete.
     */
    void deleteMessageById(String id);

    /**
     * Deletes messages that were created before a specified cutoff time.
     * This method is typically used for cleaning up old messages.
     *
     * @param cutoffTime The cutoff time before which messages should be deleted.
     */
    void deleteOldMessages(LocalDateTime cutoffTime);
}
