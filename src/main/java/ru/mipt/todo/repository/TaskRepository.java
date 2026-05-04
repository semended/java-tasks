package ru.mipt.todo.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mipt.todo.model.Task;
import ru.mipt.todo.model.TaskPriority;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * JPA-репозиторий для задач.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByCompleted(boolean completed);

    List<Task> findByPriority(TaskPriority priority);

    @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN :now AND :deadline")
    List<Task> findTasksDueWithinDays(@Param("now") LocalDate now,
                                      @Param("deadline") LocalDate deadline);

    List<Task> findByCompletedAndPriority(boolean completed, TaskPriority priority);

    @EntityGraph(attributePaths = {"attachments", "tags"})
    Optional<Task> findWithDetailsById(Long id);

    @EntityGraph(attributePaths = {"attachments", "tags"})
    @Query("SELECT t FROM Task t")
    List<Task> findAllWithDetails();
}
