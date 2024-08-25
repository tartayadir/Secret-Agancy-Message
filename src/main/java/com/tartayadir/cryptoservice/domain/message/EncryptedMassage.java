package com.tartayadir.cryptoservice.domain.message;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EncryptedMassage extends Message{

    private String key;

    public EncryptedMassage(Message message, String key) {
        this.setId(message.getId());
        this.setCreateTime(message.getCreateTime());
        this.setRetries(message.getRetries());
        this.setEncryptedMessage(message.getEncryptedMessage());
        this.key = key;
    }
}
