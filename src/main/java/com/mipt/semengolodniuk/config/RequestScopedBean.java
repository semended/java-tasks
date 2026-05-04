package com.mipt.semengolodniuk.config;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Бин со scope request.
 */
public class RequestScopedBean {

    private final String requestId;
    private final LocalDateTime startedAt;

    public RequestScopedBean() {
        this.requestId = UUID.randomUUID().toString();
        this.startedAt = LocalDateTime.now();
    }

    public String getRequestId() {
        return requestId;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }
}
