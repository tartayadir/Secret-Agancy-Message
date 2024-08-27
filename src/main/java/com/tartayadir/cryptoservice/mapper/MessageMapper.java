package com.tartayadir.cryptoservice.mapper;

import com.tartayadir.cryptoservice.domain.message.EncryptedMassage;

public class MessageMapper {

    public static String convertMessageToNATSMessage(EncryptedMassage message) {
        return String.format("%s:%s", message.getId(), message.getKey());
    }
}
