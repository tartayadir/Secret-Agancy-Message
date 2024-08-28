package com.tartayadir.cryptoservice.util;

import java.util.Base64;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class StringAssertHelper {

    private static final Pattern ID_PATTERN = Pattern.compile("^[a-fA-F0-9]{32}$");
    private static final Pattern BASE64_PATTERN = Pattern.compile("^[A-Za-z0-9+/=]+$");

    /**
     * Asserts that the given string is in the format <id>:<key>.
     *
     * @param idAndKey The string to check.
     * @throws IllegalArgumentException if the string does not match the expected format.
     */
    public static void assertIdAndKeyFormat(String idAndKey) {
        assertTrue(idAndKey == null || !idAndKey.contains(":"), "String does not contain a ':' separator");

        String[] parts = idAndKey.split(":", 2);
        assertTrue(parts.length != 2, "String must contain exactly one ':' separator");

        String id = parts[0];
        String key = parts[1];
        assertFalse(ID_PATTERN.matcher(id).matches(), "ID part of the string is not a valid 32-character hexadecimal string");

        assertFalse(BASE64_PATTERN.matcher(key).matches(), "Key part of the string is not a valid Base64-encoded string");

        assertDoesNotThrow(() -> Base64.getDecoder().decode(key),
                "Key part of the string is not properly Base64-encoded");
    }
}
