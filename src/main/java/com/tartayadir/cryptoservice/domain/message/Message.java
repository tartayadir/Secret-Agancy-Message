package com.tartayadir.cryptoservice.domain.message;

import com.tartayadir.cryptoservice.domain.CoreEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(schema = "message")
@Entity
public class Message extends CoreEntity {

    private String encryptedMessage;

    private int retries;
}
