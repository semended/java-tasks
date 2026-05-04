package com.mipt.semengolodniuk.service;

import com.mipt.semengolodniuk.config.PrototypeScopedBean;
import com.mipt.semengolodniuk.model.Task;
import com.mipt.semengolodniuk.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

/**
 * Service for working with tasks.
 */
@Service
public class TaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);
    private static final Path STATS_FILE = Path.of("task-cache-stats.txt");

    private final TaskRepository taskRepository;
    private final ObjectProvider<PrototypeScopedBean> prototypeScopedBeanProvider;
    private final Map<String, Task> taskCache = new LinkedHashMap<>();

    public TaskService(TaskRepository taskRepository,
                       ObjectProvider<PrototypeScopedBean> prototypeScopedBeanProvider) {
        this.taskRepository = taskRepository;
        this.prototypeScopedBeanProvider = prototypeScopedBeanProvider;
    }

    @PostConstruct
    public void initCache() {
        if (taskRepository.findAll().isEmpty()) {
            taskRepository.save(new Task("init-1", "Finish first homework", "Prepare Spring project", false));
            taskRepository.save(new Task("init-2", "Check tests", "Make sure all endpoints work", false));
        }

        taskCache.clear();
        taskRepository.findAll().forEach(task -> taskCache.put(task.getId(), task));
        LOGGER.info("Task cache initialized: {}", taskCache.size());
    }

    @PreDestroy
    public void cleanup() {
        LOGGER.info("TaskService destroy. Cache size: {}", taskCache.size());

        try {
            Files.writeString(
                    STATS_FILE,
                    LocalDateTime.now() + " cacheSize=" + taskCache.size() + System.lineSeparator(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException exception) {
            LOGGER.warn("Cannot write cache stats file", exception);
        }
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTaskById(String id) {
        return taskRepository.findById(id)
                .map(this::cacheTask)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    public Task createTask(Task task) {
        PrototypeScopedBean prototypeScopedBean = prototypeScopedBeanProvider.getObject();
        task.setId(prototypeScopedBean.generateTaskId());

        Task savedTask = taskRepository.save(task);
        return cacheTask(savedTask);
    }

    public Task updateTask(String id, Task task) {
        return taskRepository.update(id, task)
                .map(this::cacheTask)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    public void deleteTask(String id) {
        if (!taskRepository.deleteById(id)) {
            throw new TaskNotFoundException(id);
        }
        taskCache.remove(id);
    }

    public Map<String, Task> getTaskCache() {
        return new LinkedHashMap<>(taskCache);
    }

    private Task cacheTask(Task task) {
        taskCache.put(task.getId(), task);
        return task;
    }
}
