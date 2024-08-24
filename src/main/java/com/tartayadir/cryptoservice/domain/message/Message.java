package com.tartayadir.cryptoservice.domain.message;

import com.tartayadir.cryptoservice.domain.CoreEntity;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Message extends CoreEntity {

    private String encryptedMessage;

    private int retries;
}
