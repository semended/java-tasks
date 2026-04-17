package ru.mipt.todo.dto;

import jakarta.validation.constraints.Size;
import ru.mipt.todo.model.TaskPriority;
import ru.mipt.todo.validation.DueDateNotBeforeCreation;
import ru.mipt.todo.validation.OnUpdate;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO для обновления задачи.
 */
public class TaskUpdateDto {

    @Size(min = 3, max = 100, groups = OnUpdate.class)
    private String title;

    @Size(max = 500, groups = OnUpdate.class)
    private String description;

    @DueDateNotBeforeCreation(groups = OnUpdate.class)
    private LocalDate dueDate;

    private TaskPriority priority;
    private Boolean completed;
    private List<String> tags;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
