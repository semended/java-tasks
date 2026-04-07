package com.mipt.semengolodniuk.repository;

import com.mipt.semengolodniuk.model.Task;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

/**
 * Main repository implementation that stores tasks in memory.
 */
@Repository
@Primary
public class InMemoryTaskRepository implements TaskRepository {

    private final ConcurrentMap<String, Task> tasks = new ConcurrentHashMap<>();

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Optional<Task> findById(String id) {
        return Optional.ofNullable(tasks.get(id));
    }

    @Override
    public Task save(Task task) {
        tasks.put(task.getId(), copy(task));
        return copy(task);
    }

    @Override
    public Optional<Task> update(String id, Task task) {
        if (!tasks.containsKey(id)) {
            return Optional.empty();
        }
        Task updatedTask = copy(task);
        updatedTask.setId(id);
        tasks.put(id, updatedTask);
        return Optional.of(copy(updatedTask));
    }

    @Override
    public boolean deleteById(String id) {
        return tasks.remove(id) != null;
    }

    private Task copy(Task task) {
        return new Task(task.getId(), task.getTitle(), task.getDescription(), task.isCompleted());
    }
}
