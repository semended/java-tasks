package ru.mipt.todo.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.mipt.todo.model.TaskPriority;
import ru.mipt.todo.validation.OnCreate;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO для создания задачи.
 */
public class TaskCreateDto {

    @NotBlank(message = "Название задачи обязательно", groups = OnCreate.class)
    @Size(min = 3, max = 100, groups = OnCreate.class)
    private String title;

    @Size(max = 500)
    private String description;

    @FutureOrPresent(message = "Дедлайн должен быть сегодня или в будущем", groups = OnCreate.class)
    private LocalDate dueDate;

    @NotNull(message = "Приоритет обязателен", groups = OnCreate.class)
    private TaskPriority priority;

    @Size(max = 5)
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

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
