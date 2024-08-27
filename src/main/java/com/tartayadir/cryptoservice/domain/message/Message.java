package com.tartayadir.cryptoservice.domain.message;

import com.tartayadir.cryptoservice.domain.CoreEntity;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

/**
 * Message represents an encrypted message entity within the crypto service.
 *
 * <p>This entity is responsible for storing the encrypted version of the message and tracking the number of retries
 * the recipient has made to decrypt the message. It extends {@link CoreEntity} to inherit common properties like
 * the entity ID and creation timestamp.</p>
 *
 * <p>Fields:</p>
 * <ul>
 *   <li><strong>encryptedMessage:</strong> The encrypted content of the message. (Will be renamed to 'message' for clarity)</li>
 *   <li><strong>retries:</strong> The number of attempts the recipient has made to decrypt the message. This field is used to enforce
 *   the rule that a recipient has a limited number of attempts to enter the correct password.</li>
 * </ul>
 *
 * <p>Annotations:</p>
 * <ul>
 *   <li>@Entity: Specifies that this class is an entity and is mapped to a database table.</li>
 * </ul>
 *
 * @see CoreEntity
 */
@Getter
@Setter
@Entity
public class Message extends CoreEntity {

    private String encryptedMessage;//TODO Rename to message

    private int retries;
}
