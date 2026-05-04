package ru.mipt.todo.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.mipt.todo.dto.TaskCreateDto;
import ru.mipt.todo.dto.TaskResponseDto;
import ru.mipt.todo.dto.TaskUpdateDto;
import ru.mipt.todo.exception.TaskNotFoundException;
import ru.mipt.todo.mapper.TaskMapper;
import ru.mipt.todo.model.Task;
import ru.mipt.todo.repository.TaskRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Сервис для работы с задачами.
 */
@Service
public class TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final Map<Long, Task> taskCache = new HashMap<>();

    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    @PostConstruct
    public void init() {
        log.info("TaskService initializing, loading cache...");
        taskRepository.findAll().forEach(task -> taskCache.put(task.getId(), task));
        log.info("Loaded {} tasks into cache", taskCache.size());
    }

    @PreDestroy
    public void cleanup() {
        log.info("TaskService shutting down, cache contained {} tasks", taskCache.size());
        taskCache.clear();
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDto> getAllTasks() {
        return taskRepository.findAllWithDetails().stream()
                .map(taskMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskResponseDto getTaskById(Long id) {
        Task task = taskRepository.findWithDetailsById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        return taskMapper.toResponseDto(task);
    }

    @Transactional
    public TaskResponseDto createTask(TaskCreateDto dto) {
        Task task = taskMapper.toEntity(dto);
        Task saved = taskRepository.save(task);
        taskCache.put(saved.getId(), saved);
        return taskMapper.toResponseDto(saved);
    }

    @Transactional
    public TaskResponseDto updateTask(Long id, TaskUpdateDto dto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        taskMapper.updateEntity(dto, task);
        Task saved = taskRepository.save(task);
        taskCache.put(saved.getId(), saved);
        return taskMapper.toResponseDto(saved);
    }

    @Transactional
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        taskRepository.deleteById(id);
        taskCache.remove(id);
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDto> getTasksDueSoon() {
        LocalDate now = LocalDate.now();
        LocalDate deadline = now.plusDays(7);
        return taskRepository.findTasksDueWithinDays(now, deadline).stream()
                .map(taskMapper::toResponseDto)
                .toList();
    }

    /**
     * Массовое завершение задач. Если хотя бы одна задача не найдена,
     * транзакция откатывается целиком.
     */
    @Transactional(rollbackFor = TaskNotFoundException.class, isolation = Isolation.READ_COMMITTED)
    public List<TaskResponseDto> bulkCompleteTasks(List<Long> ids) {
        List<Task> tasks = ids.stream()
                .map(id -> taskRepository.findById(id)
                        .orElseThrow(() -> new TaskNotFoundException(id)))
                .toList();

        tasks.forEach(task -> task.setCompleted(true));
        List<Task> saved = taskRepository.saveAll(tasks);
        return saved.stream()
                .map(taskMapper::toResponseDto)
                .toList();
    }
}
