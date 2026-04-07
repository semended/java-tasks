package com.mipt.semengolodniuk.service;

/**
 * Exception thrown when a task cannot be found by its identifier.
 */
public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(String id) {
        super("Task with id '" + id + "' was not found");
    }
}
