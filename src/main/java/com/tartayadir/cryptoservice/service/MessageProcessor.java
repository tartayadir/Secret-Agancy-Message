package com.tartayadir.cryptoservice.service;

/**
 * Functional interface representing a process that handles a message.
 *
 * <p>This interface is typically used for processing incoming messages in the NATS service. It allows
 * for handling messages in a flexible and decoupled manner, with the actual processing logic defined
 * in the implementing class or method.</p>
 *
 * <p>Since this is a functional interface, it can be used as the assignment target for a lambda expression
 * or method reference.</p>
 */
@FunctionalInterface
public interface MessageProcessor {

    /**
     * Processes a message and returns the result as a String.
     *
     * @return The processed message result.
     * @throws Exception if an error occurs during processing.
     */
    String process() throws Exception;
}
