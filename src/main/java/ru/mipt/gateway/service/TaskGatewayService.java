package ru.mipt.gateway.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.mipt.gateway.client.ExternalTaskClient;
import ru.mipt.gateway.dto.TaskDto;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gateway service that proxies task CRUD operations to the external service.
 * Protected by Resilience4j rate limiter and circuit breaker.  The circuit
 * breaker fallback returns cached data when available, or an error indicator.
 */
@Service
public class TaskGatewayService {

    private static final Logger log = LoggerFactory.getLogger(TaskGatewayService.class);

    private final ExternalTaskClient client;

    /** Simple cache for fallback: stores the last successful response per task ID. */
    private final Map<Long, TaskDto> taskCache = new ConcurrentHashMap<>();
    private volatile List<TaskDto> allTasksCache = List.of();

    public TaskGatewayService(ExternalTaskClient client) {
        this.client = client;
    }

    @RateLimiter(name = "taskGateway")
    @CircuitBreaker(name = "taskGateway", fallbackMethod = "getAllTasksFallback")
    public List<TaskDto> getAllTasks(String mode) {
        List<TaskDto> tasks = client.getAllTasks(mode);
        allTasksCache = tasks;
        tasks.forEach(t -> taskCache.put(t.id(), t));
        return tasks;
    }

    @RateLimiter(name = "taskGateway")
    @CircuitBreaker(name = "taskGateway", fallbackMethod = "getTaskFallback")
    public TaskDto getTask(Long id, String mode) {
        TaskDto task = client.getTask(id, mode);
        taskCache.put(id, task);
        return task;
    }

    @RateLimiter(name = "taskGateway")
    @CircuitBreaker(name = "taskGateway", fallbackMethod = "createTaskFallback")
    public TaskDto createTask(TaskDto dto, String mode) {
        TaskDto created = client.createTask(dto, mode);
        taskCache.put(created.id(), created);
        return created;
    }

    @RateLimiter(name = "taskGateway")
    @CircuitBreaker(name = "taskGateway", fallbackMethod = "updateTaskFallback")
    public TaskDto updateTask(Long id, TaskDto dto, String mode) {
        TaskDto updated = client.updateTask(id, dto, mode);
        taskCache.put(id, updated);
        return updated;
    }

    @RateLimiter(name = "taskGateway")
    @CircuitBreaker(name = "taskGateway", fallbackMethod = "deleteTaskFallback")
    public void deleteTask(Long id, String mode) {
        client.deleteTask(id, mode);
        taskCache.remove(id);
    }

    // --- Fallback methods ---

    @SuppressWarnings("unused")
    private List<TaskDto> getAllTasksFallback(String mode, Throwable ex) {
        log.warn("Circuit breaker fallback for getAllTasks: {}", ex.getMessage());
        return allTasksCache;
    }

    @SuppressWarnings("unused")
    private TaskDto getTaskFallback(Long id, String mode, Throwable ex) {
        if (ex instanceof ru.mipt.gateway.exception.TaskNotFoundException) {
            throw (ru.mipt.gateway.exception.TaskNotFoundException) ex;
        }
        log.warn("Circuit breaker fallback for getTask({}): {}", id, ex.getMessage());
        TaskDto cached = taskCache.get(id);
        if (cached != null) {
            return cached;
        }
        throw new ru.mipt.gateway.exception.ExternalServiceException(
                "Service unavailable and no cached data for task " + id, 503);
    }

    @SuppressWarnings("unused")
    private TaskDto createTaskFallback(TaskDto dto, String mode, Throwable ex) {
        log.warn("Circuit breaker fallback for createTask: {}", ex.getMessage());
        throw new ru.mipt.gateway.exception.ExternalServiceException(
                "Service unavailable, cannot create task", 503);
    }

    @SuppressWarnings("unused")
    private TaskDto updateTaskFallback(Long id, TaskDto dto, String mode, Throwable ex) {
        if (ex instanceof ru.mipt.gateway.exception.TaskNotFoundException) {
            throw (ru.mipt.gateway.exception.TaskNotFoundException) ex;
        }
        log.warn("Circuit breaker fallback for updateTask({}): {}", id, ex.getMessage());
        throw new ru.mipt.gateway.exception.ExternalServiceException(
                "Service unavailable, cannot update task " + id, 503);
    }

    @SuppressWarnings("unused")
    private void deleteTaskFallback(Long id, String mode, Throwable ex) {
        if (ex instanceof ru.mipt.gateway.exception.TaskNotFoundException) {
            throw (ru.mipt.gateway.exception.TaskNotFoundException) ex;
        }
        log.warn("Circuit breaker fallback for deleteTask({}): {}", id, ex.getMessage());
        throw new ru.mipt.gateway.exception.ExternalServiceException(
                "Service unavailable, cannot delete task " + id, 503);
    }
}
