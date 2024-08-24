package com.tartayadir.cryptoservice.domain.message;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message {

    private String encryptedMessage;

    private int retries;
}
