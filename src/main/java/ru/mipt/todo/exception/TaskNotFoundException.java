package ru.mipt.todo.exception;

/**
 * Исключение: задача не найдена.
 */
public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(Long id) {
        super("Task not found: id=" + id);
    }
}
