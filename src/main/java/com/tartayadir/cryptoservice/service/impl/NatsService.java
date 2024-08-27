package com.tartayadir.cryptoservice.service.impl;

import com.tartayadir.cryptoservice.domain.message.EncryptedMassage;
import com.tartayadir.cryptoservice.exception.EncryptionOperationException;
import com.tartayadir.cryptoservice.exception.MessageNotFoundException;
import com.tartayadir.cryptoservice.service.MessageProcessor;
import com.tartayadir.cryptoservice.service.MessageService;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Message;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.tartayadir.cryptoservice.mapper.MessageMapper.convertMessageToNATSMessage;
import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
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

    private void onMessage(Message msg) {
        //TODO verify method necessity and implement if so
    }

    private void handleSaveMessage(Message msg) {
        processMessage(msg, () -> {
            String originalMessage = new String(msg.getData(), UTF_8);

            com.tartayadir.cryptoservice.domain.message.Message message = new com.tartayadir.cryptoservice.domain.message.Message();
            message.setEncryptedMessage(originalMessage);
            EncryptedMassage savedMessage = (EncryptedMassage) messageService.save(message);

            return convertMessageToNATSMessage(savedMessage);
        });
    }

    private void handleReceiveMessage(Message msg) {
        processMessage(msg, () -> {
            String[] parts = new String(msg.getData(), UTF_8).split(":", 2);
            if (parts.length < 2) {
                throw new IllegalArgumentException("Invalid message format");
            }

            String messageId = parts[0];
            String providedKey = parts[1];

            String decryptedMessage = messageService.getEncryptedMessage(messageId, providedKey);
            if (decryptedMessage == null) {
                throw new IllegalArgumentException("Decryption failed or message not found");
            }

            return decryptedMessage;
        });
    }

    private void processMessage(Message msg, MessageProcessor processor) {
        try {
            if (msg.getReplyTo() == null) {
                throw new IllegalArgumentException("No reply to message");
            }

            String response = processor.process();
            natsConnection.publish(msg.getReplyTo(), response.getBytes(UTF_8));
        } catch (Exception e) {
            log.error("Error processing message", e);
            String errorMessage = e instanceof MessageNotFoundException || e instanceof EncryptionOperationException
                    ? "Error: " + e.getMessage()
                    : "Unhandled exception";

            natsConnection.publish(msg.getReplyTo(), errorMessage.getBytes(UTF_8));
        }
    }
}
