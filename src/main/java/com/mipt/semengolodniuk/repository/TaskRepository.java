package com.mipt.semengolodniuk.repository;

import com.mipt.semengolodniuk.model.Task;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс репозитория задач.
 */
public interface TaskRepository {

    List<Task> findAll();

    Optional<Task> findById(String id);

    Task save(Task task);

    Optional<Task> update(String id, Task task);

    boolean deleteById(String id);
}
