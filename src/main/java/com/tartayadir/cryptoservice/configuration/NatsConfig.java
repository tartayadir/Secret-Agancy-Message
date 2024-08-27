package com.tartayadir.cryptoservice.configuration;

import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * NatsConfig is a Spring configuration class that sets up the NATS connection for the crypto service.
 *
 * <p>This configuration class reads the NATS server URL from application properties and creates a
 * {@link io.nats.client.Connection} bean that can be injected wherever it is needed in the application.</p>
 *
 * <p>NATS (NATS Streaming Server) is a lightweight, high-performance messaging system used for communication between
 * distributed systems. This class ensures that the application is correctly configured to connect to the NATS server.</p>
 */
@Configuration
public class NatsConfig {

    /**
     * The URL of the NATS server, injected from the application properties.
     */
    @Value("${nats.url}")
    private String natsUrl;

    /**
     * Creates and configures a NATS {@link io.nats.client.Connection} bean.
     *
     * <p>This method builds a connection using the specified NATS server URL, which is read from the application's
     * configuration properties. The connection is established and then managed by the Spring context.</p>
     *
     * @return The NATS connection instance.
     * @throws IOException if an I/O error occurs when attempting to connect.
     * @throws InterruptedException if the connection attempt is interrupted.
     */
    @Bean
    public Connection natsConnection() throws IOException, InterruptedException {
        Options options = new Options.Builder().server(natsUrl).build();
        return Nats.connect(options);
    }
}
