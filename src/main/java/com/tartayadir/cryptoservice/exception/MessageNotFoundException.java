package com.tartayadir.cryptoservice.exception;

public class MessageNotFoundException extends RuntimeException {

    public MessageNotFoundException(String message) {
        super(message);
    }
}