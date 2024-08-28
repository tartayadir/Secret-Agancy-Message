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

/**
 * The NatsService class handles communication between the crypto service and the NATS messaging server.
 *
 * <p>This service subscribes to NATS topics for saving and receiving messages, processes the messages using the
 * provided {@link MessageService}, and responds back to the NATS server with the appropriate responses.</p>
 *
 * <p>Initialization of NATS subscriptions occurs after the service is constructed, ensuring that the service
 * is ready to handle incoming messages.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NatsService {

    private final Connection natsConnection;
    private final MessageService messageService;

    public static final String SAVE_MESSAGE_SUBJECT = "save.msg";
    public static final String RECEIVE_MESSAGE_SUBJECT = "receive.msg";

    /**
     * Initializes the NATS service by setting up subscriptions to handle incoming messages.
     * Subscribes to the "save.msg" and "receive.msg" topics for processing save and receive operations.
     */
    @PostConstruct
    public void init() {
        Dispatcher dispatcher = natsConnection.createDispatcher(this::onMessage);

        dispatcher.subscribe(SAVE_MESSAGE_SUBJECT, this::handleSaveMessage);
        dispatcher.subscribe(RECEIVE_MESSAGE_SUBJECT, this::handleReceiveMessage);
    }

    /**
     * Processes incoming NATS messages generically.
     *
     * @param msg The incoming NATS message to process.
     */
    private void onMessage(Message msg) {
        // TODO verify method necessity and implement if so
    }

    /**
     * Handles the "save.msg" topic by saving the incoming message and returning an acknowledgment.
     *
     * @param msg The incoming NATS message containing the message to save.
     */
    private void handleSaveMessage(Message msg) {
        processMessage(msg, () -> {
            String originalMessage = new String(msg.getData(), UTF_8);

            com.tartayadir.cryptoservice.domain.message.Message message = new com.tartayadir.cryptoservice.domain.message.Message();
            message.setEncryptedMessage(originalMessage);
            EncryptedMassage savedMessage = (EncryptedMassage) messageService.save(message);

            return convertMessageToNATSMessage(savedMessage);
        });
    }

    /**
     * Handles the "receive.msg" topic by retrieving and decrypting a message based on its ID and key.
     *
     * @param msg The incoming NATS message containing the ID and key for the message retrieval.
     */
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

    /**
     * Generic method for processing NATS messages using a {@link MessageProcessor}.
     * Handles exceptions and sends appropriate error responses if necessary.
     *
     * @param msg The incoming NATS message to process.
     * @param processor The processor function to handle the message.
     */
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
