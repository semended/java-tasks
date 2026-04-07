package com.mipt.semengolodniuk.config;

import java.util.UUID;

/**
 * Бин со scope prototype.
 */
public class PrototypeScopedBean {

    private final String generatedId = UUID.randomUUID().toString();

    public String generateTaskId() {
        return generatedId;
    }
}
