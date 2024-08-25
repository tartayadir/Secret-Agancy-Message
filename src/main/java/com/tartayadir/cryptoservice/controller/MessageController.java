package com.tartayadir.cryptoservice.controller;

import com.tartayadir.cryptoservice.controller.dto.MessageDto;
import com.tartayadir.cryptoservice.controller.dto.MessageResponseDto;
import com.tartayadir.cryptoservice.domain.message.EncryptedMassage;
import com.tartayadir.cryptoservice.domain.message.Message;
import com.tartayadir.cryptoservice.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//Temp controller to test logic
//TODO Add exception handling
@RestController
@RequestMapping("/api/v1/message")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMessage(@PathVariable String id, @RequestParam String key) {
        String decryptedMessage = messageService.getEncryptedMessage(id, key);
        return ResponseEntity.ok(decryptedMessage);
    }

    @PostMapping
    public ResponseEntity<MessageResponseDto> postMessage(@RequestBody MessageDto message) {
        Message message1 = new Message();
        message1.setEncryptedMessage(message.getMessage());

        EncryptedMassage savedMessage = (EncryptedMassage) messageService.save(message1);
        MessageResponseDto messageResponseDto = new MessageResponseDto();
        messageResponseDto.setId(savedMessage.getId());
        messageResponseDto.setKey(savedMessage.getKey());
        return ResponseEntity.ok(messageResponseDto);
    }

    @GetMapping("/older-than-two-days")
    public ResponseEntity<?> getMessagesOlderThanTwoDays() {
        messageService.deleteAllMessagesOlderThanTwoDays();
        return ResponseEntity.ok("messages");
    }
}
