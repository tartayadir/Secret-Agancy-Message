package com.tartayadir.cryptoservice.service;

import com.tartayadir.cryptoservice.domain.message.Message;

import java.time.LocalDateTime;

public interface MessageService {

    Message findById(String id);

    String getEncryptedMessage(String id, String key);

    Message save(Message message);

    Message update(Message message);

    void deleteMessageById(String id);

    void deleteOldMessages(LocalDateTime cutoffTime);

    default void deleteAllMessagesOlderThanTwoDays() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(2);
        deleteOldMessages(cutoffTime);
    }
}
