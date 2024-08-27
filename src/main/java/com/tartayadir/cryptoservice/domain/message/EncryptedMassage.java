package com.tartayadir.cryptoservice.domain.message;

import lombok.Getter;
import lombok.Setter;

/**
 * EncryptedMassage is an extension of the {@link Message} entity that adds a key used for decrypting the message.
 *
 * <p>This class is used to encapsulate both the encrypted message and its decryption key, which is not stored in the
 * database but is generated and managed separately according to the service's security requirements.</p>
 *
 * <p>Fields:</p>
 * <ul>
 *   <li><strong>key:</strong> The decryption key associated with the message. This key is provided separately to the recipient and is not stored with the message entity.</li>
 * </ul>
 *
 * <p>Constructors:</p>
 * <ul>
 *   <li>Constructor that initializes an EncryptedMassage instance based on an existing Message entity and a provided decryption key.</li>
 * </ul>
 *
 * @see Message
 */
@Getter
@Setter
public class EncryptedMassage extends Message{

    private String key;

    /**
     * Constructs an EncryptedMassage by copying the properties from a given {@link Message} instance and adding the provided key.
     *
     * @param message The Message entity whose properties are to be copied.
     * @param key The decryption key associated with this EncryptedMassage.
     */
    public EncryptedMassage(Message message, String key) {
        this.setId(message.getId());
        this.setCreateTime(message.getCreateTime());
        this.setRetries(message.getRetries());
        this.setEncryptedMessage(message.getEncryptedMessage());
        this.key = key;
    }
}
