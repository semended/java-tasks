package ru.mipt.todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mipt.todo.model.TaskAttachment;

import java.util.List;

/**
 * JPA-репозиторий для вложений.
 */
@Repository
public interface TaskAttachmentRepository extends JpaRepository<TaskAttachment, Long> {

    List<TaskAttachment> findByTaskId(Long taskId);
}
