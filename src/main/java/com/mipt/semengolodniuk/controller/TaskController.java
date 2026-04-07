package com.mipt.semengolodniuk.controller;

import com.mipt.semengolodniuk.config.RequestScopedBean;
import com.mipt.semengolodniuk.model.Task;
import com.mipt.semengolodniuk.service.TaskService;
import com.mipt.semengolodniuk.service.TaskStatisticsService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller that provides CRUD operations for tasks.
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;
    private final TaskStatisticsService taskStatisticsService;
    private final RequestScopedBean requestScopedBean;

    public TaskController(TaskService taskService,
                          TaskStatisticsService taskStatisticsService,
                          RequestScopedBean requestScopedBean) {
        this.taskService = taskService;
        this.taskStatisticsService = taskStatisticsService;
        this.requestScopedBean = requestScopedBean;
    }

    @GetMapping
    public List<Task> getAllTasks(@RequestParam(required = false) Boolean completed) {
        logRequestInfo("getAllTasks");
        return taskService.getAllTasks(completed);
    }

    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable String id) {
        logRequestInfo("getTaskById");
        return taskService.getTaskById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task createTask(@Valid @RequestBody Task task) {
        logRequestInfo("createTask");
        return taskService.createTask(task);
    }

    @PutMapping("/{id}")
    public Task updateTask(@PathVariable String id, @Valid @RequestBody Task task) {
        logRequestInfo("updateTask");
        return taskService.updateTask(id, task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        logRequestInfo("deleteTask");
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/repositories")
    public Map<String, Object> getRepositoryStatistics() {
        logRequestInfo("getRepositoryStatistics");
        return taskStatisticsService.getRepositoryStatistics();
    }

    private void logRequestInfo(String methodName) {
        LOGGER.info("Controller method {} is handling requestId={} startedAt={}",
                methodName,
                requestScopedBean.getRequestId(),
                requestScopedBean.getStartedAt());
    }
}
