package com.mipt.semengolodniuk.config;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Bean with request scope to show that each HTTP request receives its own instance.
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
