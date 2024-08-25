package com.tartayadir.cryptoservice.exception;

public class EncryptionOperationException extends RuntimeException {

    public EncryptionOperationException() {}

    public EncryptionOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
