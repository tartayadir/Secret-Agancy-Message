package com.tartayadir.cryptoservice.repository;

import com.tartayadir.cryptoservice.domain.message.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * MessageRepository provides database access functionality for the {@link Message} entity.
 *
 * <p>This repository extends {@link JpaRepository}, which provides standard CRUD operations, and includes custom methods
 * for updating and deleting messages. The repository also leverages Spring Data JPA's capability to define custom queries
 * using JPQL (Java Persistence Query Language).</p>
 *
 * <p>Custom Methods:</p>
 * <ul>
 *   <li><strong>updateMessage:</strong> Updates the encrypted message and retries count for a specific message by its ID.</li>
 *   <li><strong>deleteOldMessages:</strong> Deletes messages that were created before a specified cutoff time, used for periodic cleanup.</li>
 * </ul>
 *
 * <p>Annotations:</p>
 * <ul>
 *   <li>@Repository: Marks this interface as a Spring Data repository, which will be automatically implemented by Spring.</li>
 *   <li>@Transactional: Ensures that update and delete operations are executed within a transactional context.</li>
 * </ul>
 *
 * @see JpaRepository
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, String> {

    @Override
    Optional<Message> findById(String id);

    @Override
    <S extends Message> S save(S entity);

    /**
     * Updates the encrypted message content and retry count for a message identified by the given ID.
     *
     * @param id The ID of the message to update.
     * @param encryptedMessage The new encrypted message content.
     * @param retries The new retry count.
     * @return The number of entities updated (should be 1 if successful).
     */
    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.encryptedMessage = :encryptedMessage, m.retries = :retries WHERE m.id = :id")
    int updateMessage(String id, String encryptedMessage, int retries);

    @Override
    void deleteById(String id);

    /**
     * Deletes all messages that were created before the specified cutoff time.
     * This method is used for cleaning up old messages according to the business rule that messages should be auto-deleted after a certain period.
     *
     * @param cutoffTime The cutoff time before which messages should be deleted.
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Message m WHERE m.createTime < :cutoffTime")
    void deleteOldMessages(LocalDateTime cutoffTime);
}
