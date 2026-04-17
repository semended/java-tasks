package ru.mipt.todo.exception;

/**
 * Исключение: вложение не найдено.
 */
public class AttachmentNotFoundException extends RuntimeException {

    public AttachmentNotFoundException(Long id) {
        super("Attachment not found: id=" + id);
    }
}
