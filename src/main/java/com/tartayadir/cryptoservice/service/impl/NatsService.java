package com.tartayadir.cryptoservice.service.impl;

import com.tartayadir.cryptoservice.controller.dto.MessageResponseDto;
import com.tartayadir.cryptoservice.domain.message.EncryptedMassage;
import com.tartayadir.cryptoservice.service.MessageService;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Message;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class NatsService {

    private final Connection natsConnection;

    private final MessageService messageService;

    @PostConstruct
    public void init() {
        Dispatcher dispatcher = natsConnection.createDispatcher(this::onMessage);

        dispatcher.subscribe("save.msg", this::handleSaveMessage);
        dispatcher.subscribe("receive.msg", this::handleReceiveMessage);
    }

    private void onMessage(Message msg) {}

    private void handleSaveMessage(Message msg) {
        String originalMessage = new String(msg.getData(), StandardCharsets.UTF_8);

        com.tartayadir.cryptoservice.domain.message.Message message = new com.tartayadir.cryptoservice.domain.message.Message();
        message.setEncryptedMessage(originalMessage);
        EncryptedMassage savedMessage = (EncryptedMassage) messageService.save(message);

        natsConnection.publish(msg.getReplyTo(), (savedMessage.getId() + ":" + savedMessage.getKey()).getBytes(StandardCharsets.UTF_8));
    }

    private void handleReceiveMessage(Message msg) {
        String[] parts = new String(msg.getData(), StandardCharsets.UTF_8).split(":", 2);
        String messageId = parts[0];
        String providedKey = parts[1];

        String decryptedMessage = messageService.getEncryptedMessage(messageId, providedKey);
        natsConnection.publish(msg.getReplyTo(), decryptedMessage.getBytes(StandardCharsets.UTF_8));
    }
}
