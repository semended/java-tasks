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
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service layer that handles task CRUD operations and local cache management.
 */
@Service
public class TaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);
    private static final Path STATS_FILE = Path.of("service", "task-cache-statistics.txt");

    private final TaskRepository taskRepository;
    private final ObjectProvider<PrototypeScopedBean> prototypeScopedBeanProvider;
    private final Map<String, Task> taskCache = new LinkedHashMap<>();

    @Value("${app.name}")
    private String applicationName;

    @Value("${app.version}")
    private String applicationVersion;

    public TaskService(TaskRepository taskRepository,
                       ObjectProvider<PrototypeScopedBean> prototypeScopedBeanProvider) {
        this.taskRepository = taskRepository;
        this.prototypeScopedBeanProvider = prototypeScopedBeanProvider;
    }

    @PostConstruct
    public void initCache() {
        if (taskRepository.findAll().isEmpty()) {
            taskRepository.save(new Task("init-1", "Prepare Spring skeleton", "Create basic project structure", false));
            taskRepository.save(new Task("init-2", "Write controller tests", "Cover CRUD endpoints with tests", false));
        }

        taskCache.clear();
        taskRepository.findAll().forEach(task -> taskCache.put(task.getId(), task));

        LOGGER.info("Task cache initialized for {} {} with {} task(s)",
                applicationName,
                applicationVersion,
                taskCache.size());
    }

    @PreDestroy
    public void cleanup() {
        LOGGER.info("TaskService is shutting down. Cached task count: {}", taskCache.size());

        try {
            Files.createDirectories(STATS_FILE.getParent());
            Files.writeString(
                    STATS_FILE,
                    LocalDateTime.now() + " | cached tasks before destroy: " + taskCache.size() + System.lineSeparator(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException exception) {
            LOGGER.warn("Could not write cache statistics file", exception);
        }
    }

    public List<Task> getAllTasks(Boolean completed) {
        List<Task> tasks = taskRepository.findAll();
        if (completed == null) {
            return tasks;
        }
        return tasks.stream()
                .filter(task -> task.isCompleted() == completed)
                .collect(Collectors.toList());
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
