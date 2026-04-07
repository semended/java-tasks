package com.mipt.semengolodniuk.config;

import java.util.UUID;

/**
 * Bean with prototype scope that generates a new identifier for each created task.
 */
public class PrototypeScopedBean {

    private final String generatedId = UUID.randomUUID().toString();

    public String generateTaskId() {
        return generatedId;
    }
}
