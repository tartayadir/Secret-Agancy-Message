package com.tartayadir.cryptoservice.service;

@FunctionalInterface
public interface MessageProcessor {

    String process() throws Exception;
}
