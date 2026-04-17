package ru.mipt.todo.lifecycle;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Бин со скоупом request — новый экземпляр на каждый HTTP-запрос.
 */
@Component
@RequestScope
public class RequestScopedBean {

    private final String requestId = UUID.randomUUID().toString();
    private final LocalDateTime startTime = LocalDateTime.now();

    public String getRequestId() {
        return requestId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }
}
