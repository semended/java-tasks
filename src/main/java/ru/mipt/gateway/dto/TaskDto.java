package ru.mipt.gateway.dto;

/**
 * Data transfer object representing a task in the external service.
 *
 * @param id          unique task identifier (may be null on creation)
 * @param title       short title of the task
 * @param description detailed description
 * @param completed   whether the task is done
 */
public record TaskDto(Long id, String title, String description, boolean completed) {
}
