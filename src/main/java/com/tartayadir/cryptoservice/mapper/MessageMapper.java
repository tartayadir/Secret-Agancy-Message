package com.tartayadir.cryptoservice.mapper;

import com.tartayadir.cryptoservice.domain.message.EncryptedMassage;

/**
 * MessageMapper is a utility class that provides methods for converting message entities into formats suitable for NATS messaging.
 *
 * <p>This class handles the mapping of {@link EncryptedMassage} objects into string representations that can be
 * transmitted over NATS, ensuring that the necessary information (such as message ID and encryption key) is correctly formatted.</p>
 */
public class MessageMapper {

    /**
     * Converts an {@link EncryptedMassage} object into a NATS message string.
     *
     * <p>The resulting string is formatted as "messageId:key", which is the expected format for messages transmitted via NATS.</p>
     *
     * @param message The EncryptedMassage object to convert.
     * @return A formatted string representing the message, suitable for NATS transmission.
     */
    public static String convertMessageToNATSMessage(EncryptedMassage message) {
        return String.format("%s:%s", message.getId(), message.getKey());
    }
}
