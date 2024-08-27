package com.tartayadir.cryptoservice.service.impl;

import com.tartayadir.cryptoservice.domain.message.EncryptedMassage;
import com.tartayadir.cryptoservice.domain.message.Message;
import com.tartayadir.cryptoservice.exception.DecryptionAttemptsReachedException;
import com.tartayadir.cryptoservice.exception.EncryptionOperationException;
import com.tartayadir.cryptoservice.repository.MessageRepository;
import com.tartayadir.cryptoservice.service.CryptoService;
import com.tartayadir.cryptoservice.service.MessageService;
import com.tartayadir.cryptoservice.exception.MessageNotFoundException;
import com.tartayadir.cryptoservice.util.SecureRandomString;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.time.LocalDateTime;

/**
 * Implementation of the {@link MessageService} interface, providing concrete methods for handling secret messages.
 *
 * <p>This service includes logic for encryption and decryption of messages, tracking decryption attempts, and
 * managing the lifecycle of messages (such as automatic deletion after retrieval or after a certain period).</p>
 *
 * <p>Scheduled tasks are also implemented to automatically delete messages that are older than a specified period (e.g., two days).</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final CryptoService cryptoService;

    private static final int MAX_DECRYPTION_ATTEMPTS = 3;

    /**
     * {@inheritDoc}
     */
    @Override
    public Message findById(String id) {
        return messageRepository
                .findById(id)
                .orElseThrow(() -> getMessageNotFoundException(id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEncryptedMessage(String id, String key) {
        Message message = findById(id);
        String encryptedMessage = message.getEncryptedMessage();

        try {
            String decryptedMessage = cryptoService.decrypt(encryptedMessage, key);
            deleteMessageById(id);

            return decryptedMessage;
        } catch (GeneralSecurityException e) {
            log.error("Error during message decryption while getting message", e);

            int decryptionAttemptsCounter = message.getRetries();
            message.setRetries(++decryptionAttemptsCounter);
            update(message);

            if (decryptionAttemptsCounter >= MAX_DECRYPTION_ATTEMPTS) {
                deleteMessageById(id);

                String errorMassage = String.format("Message decryption attempts reached, message with id %s will be deleted.", message.getId());
                log.info(errorMassage);
                throw new DecryptionAttemptsReachedException("", e);
            } else {
                throw new EncryptionOperationException(e.getMessage(), e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message save(Message message) {
        message.setId(SecureRandomString.generate());

        try {
            log.info("Saving new message");

            String key = cryptoService.generateKey();
            String encryptedMessage = cryptoService.encrypt(message.getEncryptedMessage(), key);
            message.setEncryptedMessage(encryptedMessage);

            Message savedMessage = messageRepository.save(message);
            return new EncryptedMassage(savedMessage, key);

        } catch (GeneralSecurityException e) {
            String errorMessage = e.getMessage();
            log.error("Error during encryption operation while saving message", e);
            throw new EncryptionOperationException(errorMessage, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message update(Message message) {
        Message existingMessage = checkExistence(message.getId());

        existingMessage.setRetries(message.getRetries());

        log.info("Updating message with id {}", message.getId());
        return messageRepository.save(existingMessage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteMessageById(String id) {
        checkExistence(id);
        log.info("Deleting message with id {}", id);
        messageRepository.deleteById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteOldMessages(LocalDateTime cutoffTime) {
        log.info("Deleting messages older than {}", cutoffTime);
        messageRepository.deleteOldMessages(cutoffTime);
    }

    /**
     * Scheduled task that runs daily to delete all messages older than two days.
     * This method ensures that expired messages are cleaned up automatically.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteAllMessagesOlderThanTwoDays() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(2);
        log.info("Deleting messages older than two days ({})", cutoffTime);
        deleteOldMessages(cutoffTime);
    }

    /**
     * Checks whether a message with the given ID exists in the repository.
     *
     * @param id The ID of the message to check.
     * @return The found Message.
     * @throws MessageNotFoundException if the message is not found.
     */
    private Message checkExistence(String id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> getMessageNotFoundException(id));
    }

    /**
     * Creates an exception to indicate that a message with the specified ID was not found.
     *
     * @param id The ID of the message that was not found.
     * @return The MessageNotFoundException with a detailed error message.
     */
    private static MessageNotFoundException getMessageNotFoundException(String id) {
        log.error("Message with id {} not found", id);
        return new MessageNotFoundException("Message with id " + id + " not found");
    }
}
