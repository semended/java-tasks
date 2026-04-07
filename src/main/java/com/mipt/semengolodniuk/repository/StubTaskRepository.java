package com.mipt.semengolodniuk.repository;

import com.mipt.semengolodniuk.model.Task;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Заглушка репозитория с фиксированными задачами.
 */
public class StubTaskRepository implements TaskRepository {

    private final Map<String, Task> tasks = new LinkedHashMap<>();

    public StubTaskRepository() {
        tasks.put("stub-1", new Task("stub-1", "Read Spring docs", "Stub repository task", false));
        tasks.put("stub-2", new Task("stub-2", "Prepare demo", "Second stub task", true));
    }

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
