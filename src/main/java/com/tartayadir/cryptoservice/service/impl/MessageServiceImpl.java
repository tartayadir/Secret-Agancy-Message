package com.tartayadir.cryptoservice.service.impl;

import com.tartayadir.cryptoservice.domain.message.Message;
import com.tartayadir.cryptoservice.repository.MessageRepository;
import com.tartayadir.cryptoservice.service.MessageService;
import com.tartayadir.cryptoservice.exception.MessageNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    
    private final MessageRepository messageRepository;

    @Override
    public Message findById(Long id) {
        return messageRepository
                .findById(id)
                .orElseThrow(() -> getMessageNotFoundException(id));
    }

    @Override
    public Message save(Message message) {
        log.info("Saving new message");
        return messageRepository.save(message);
    }

    @Override
    public Message update(Message message) {
        Message existingMessage = checkExistence(message.getId());

        existingMessage.setEncryptedMessage(message.getEncryptedMessage());
        existingMessage.setRetries(message.getRetries());

        log.info("Updating message with id {}", message.getId());
        return messageRepository.save(existingMessage);
    }

    @Override
    public void deleteMessageById(Long id) {
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

    private Message checkExistence(Long id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> getMessageNotFoundException(id));
    }

    private static MessageNotFoundException getMessageNotFoundException(Long id) {
        log.error("Message with id {} not found", id);
        return new MessageNotFoundException("Message with id " + id + " not found");
    }
}
