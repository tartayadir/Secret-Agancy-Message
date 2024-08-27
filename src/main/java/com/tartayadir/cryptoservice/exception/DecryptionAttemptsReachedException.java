package com.tartayadir.cryptoservice.exception;

public class DecryptionAttemptsReachedException extends EncryptionOperationException {

    public DecryptionAttemptsReachedException(String message, Throwable cause) {
        super(message, cause);
    }
}
