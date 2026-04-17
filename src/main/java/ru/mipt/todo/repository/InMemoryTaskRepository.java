package ru.mipt.todo.repository;

import org.springframework.stereotype.Repository;
import ru.mipt.todo.model.Task;
import ru.mipt.todo.model.TaskPriority;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * In-memory репозиторий задач на базе ConcurrentHashMap.
 */
@Repository
public class InMemoryTaskRepository {

    private final Map<Long, Task> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public Task save(Task task) {
        if (task.getId() == null) {
            task.setId(idGenerator.getAndIncrement());
        }
        storage.put(task.getId(), task);
        return task;
    }

    public Optional<Task> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<Task> findAll() {
        return new ArrayList<>(storage.values());
    }

    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }

    public void deleteById(Long id) {
        storage.remove(id);
    }

    public List<Task> saveAll(List<Task> tasks) {
        tasks.forEach(this::save);
        return tasks;
    }

    public List<Task> findByCompleted(boolean completed) {
        return storage.values().stream()
                .filter(t -> t.isCompleted() == completed)
                .collect(Collectors.toList());
    }

    public List<Task> findByPriority(TaskPriority priority) {
        return storage.values().stream()
                .filter(t -> t.getPriority() == priority)
                .collect(Collectors.toList());
    }

    public List<Task> findTasksDueWithinDays(LocalDate now, LocalDate deadline) {
        return storage.values().stream()
                .filter(t -> t.getDueDate() != null)
                .filter(t -> !t.getDueDate().isBefore(now) && !t.getDueDate().isAfter(deadline))
                .collect(Collectors.toList());
    }
}
