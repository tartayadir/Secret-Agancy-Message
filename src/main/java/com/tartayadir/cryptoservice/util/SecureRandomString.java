package com.tartayadir.cryptoservice.util;

import java.util.UUID;

public class SecureRandomString {
    public static String generate() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "");
    }
}
