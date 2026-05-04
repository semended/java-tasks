package ru.mipt.todo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mipt.todo.dto.TaskCreateDto;
import ru.mipt.todo.dto.TaskResponseDto;
import ru.mipt.todo.dto.TaskUpdateDto;
import ru.mipt.todo.service.TaskService;
import ru.mipt.todo.validation.OnCreate;
import ru.mipt.todo.validation.OnUpdate;

import java.util.List;

/**
 * REST-контроллер для управления задачами.
 */
@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "Управление задачами")
public class TaskController {

    private final TaskService taskService;
    private final String apiVersion;

    public TaskController(TaskService taskService,
                          @Value("${app.api-version:2.0.0}") String apiVersion) {
        this.taskService = taskService;
        this.apiVersion = apiVersion;
    }

    @GetMapping
    @Operation(summary = "Получить все задачи")
    @ApiResponse(responseCode = "200", description = "Список задач")
    public ResponseEntity<List<TaskResponseDto>> getAllTasks() {
        List<TaskResponseDto> tasks = taskService.getAllTasks();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(tasks.size()))
                .header("X-API-Version", apiVersion)
                .body(tasks);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить задачу по ID")
    @ApiResponse(responseCode = "200", description = "Задача найдена")
    @ApiResponse(responseCode = "404", description = "Задача не найдена")
    public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable Long id) {
        TaskResponseDto task = taskService.getTaskById(id);
        return ResponseEntity.ok()
                .header("X-API-Version", apiVersion)
                .body(task);
    }

    @PostMapping
    @Operation(summary = "Создать новую задачу")
    @ApiResponse(responseCode = "201", description = "Задача создана")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации")
    public ResponseEntity<TaskResponseDto> createTask(
            @Validated(OnCreate.class) @RequestBody TaskCreateDto dto) {
        TaskResponseDto created = taskService.createTask(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("X-API-Version", apiVersion)
                .body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить задачу")
    @ApiResponse(responseCode = "200", description = "Задача обновлена")
    @ApiResponse(responseCode = "404", description = "Задача не найдена")
    public ResponseEntity<TaskResponseDto> updateTask(
            @PathVariable Long id,
            @Validated(OnUpdate.class) @RequestBody TaskUpdateDto dto) {
        TaskResponseDto updated = taskService.updateTask(id, dto);
        return ResponseEntity.ok()
                .header("X-API-Version", apiVersion)
                .body(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить задачу")
    @ApiResponse(responseCode = "204", description = "Задача удалена")
    @ApiResponse(responseCode = "404", description = "Задача не найдена")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent()
                .header("X-API-Version", apiVersion)
                .build();
    }

}
