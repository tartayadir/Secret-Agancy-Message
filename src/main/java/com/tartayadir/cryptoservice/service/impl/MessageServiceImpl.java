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
import org.springframework.stereotype.Service;

import java.security.*;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    
    private final MessageRepository messageRepository;
    private final CryptoService cryptoService;

    private static final int MAX_DECRYPTION_ATTEMPTS = 3;

    @Override
    public Message findById(String id) {
        return messageRepository
                .findById(id)
                .orElseThrow(() -> getMessageNotFoundException(id));
    }

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

    @Override
    public Message update(Message message) {
        Message existingMessage = checkExistence(message.getId());

        existingMessage.setRetries(message.getRetries());

        log.info("Updating message with id {}", message.getId());
        return messageRepository.save(existingMessage);
    }

    @Override
    public void deleteMessageById(String id) {
        checkExistence(id);
        log.info("Deleting message with id {}", id);
        messageRepository.deleteById(id);
    }

    @Override
    public void deleteOldMessages(LocalDateTime cutoffTime) {
        log.info("Deleting messages older than {}", cutoffTime);
        messageRepository.deleteOldMessages(cutoffTime);
    }

    public void deleteAllMessagesOlderThanTwoDays() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(2);
        log.info("Deleting messages older than two days ({})", cutoffTime);
        deleteOldMessages(cutoffTime);
    }

    private Message checkExistence(String id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> getMessageNotFoundException(id));
    }

    private static MessageNotFoundException getMessageNotFoundException(String id) {
        log.error("Message with id {} not found", id);
        return new MessageNotFoundException("Message with id " + id + " not found");
    }
}
