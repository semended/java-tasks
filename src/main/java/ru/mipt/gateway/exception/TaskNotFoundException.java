package ru.mipt.gateway.exception;

/**
 * Thrown when a requested task cannot be found, either locally in the
 * emulator or via the external service response (404).
 */
public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(Long id) {
        super("Task not found: id=" + id);
    }

    public TaskNotFoundException(String message) {
        super(message);
    }
}
