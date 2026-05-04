package ru.mipt.todo.repository;

import org.springframework.stereotype.Repository;
import ru.mipt.todo.model.TaskAttachment;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * In-memory репозиторий вложений.
 */
@Repository
public class InMemoryAttachmentRepository {

    private final Map<Long, TaskAttachment> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public TaskAttachment save(TaskAttachment attachment) {
        if (attachment.getId() == null) {
            attachment.setId(idGenerator.getAndIncrement());
        }
        storage.put(attachment.getId(), attachment);
        return attachment;
    }

    public Optional<TaskAttachment> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<TaskAttachment> findByTaskId(Long taskId) {
        return storage.values().stream()
                .filter(a -> a.getTask() != null && taskId.equals(a.getTask().getId()))
                .collect(Collectors.toList());
    }

    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }

    public void delete(TaskAttachment attachment) {
        storage.remove(attachment.getId());
    }
}
