package com.tartayadir.cryptoservice.service;

import com.tartayadir.cryptoservice.domain.message.Message;

import java.time.LocalDateTime;

public interface MessageService {

    Message findById(Long id);

    Message save(Message message);

    Message update(Message message);

    void deleteMessageById(Long id);

    void deleteOldMessages(LocalDateTime cutoffTime);

    default void deleteAllMessagesOlderThanTwoDays() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(2);
        deleteOldMessages(cutoffTime);
    }
}