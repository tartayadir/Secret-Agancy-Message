package com.tartayadir.cryptoservice.util;

import java.util.UUID;

/**
 * SecureRandomString is a utility class that provides methods for generating cryptographically secure random strings.
 *
 * <p>The generated strings are based on UUIDs but are formatted without hyphens, making them suitable for use
 * as unique identifiers or secret tokens within the application.</p>
 */
public class SecureRandomString {

    /**
     * Generates a secure random string based on a UUID, with hyphens removed.
     *
     * @return A 32-character secure random string.
     */
    public static String generate() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "");
    }
}
