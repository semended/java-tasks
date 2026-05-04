package ru.mipt.todo.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.mipt.todo.model.Task;
import ru.mipt.todo.model.TaskPriority;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void findByCompleted_returnsCorrectTasks() {
        Task completed = createTask("Done", true, TaskPriority.LOW);
        Task pending = createTask("Pending", false, TaskPriority.HIGH);
        taskRepository.saveAll(List.of(completed, pending));

        List<Task> result = taskRepository.findByCompleted(true);

        assertEquals(1, result.size());
        assertEquals("Done", result.get(0).getTitle());
    }

    @Test
    void findByPriority_returnsCorrectTasks() {
        Task high = createTask("Urgent", false, TaskPriority.HIGH);
        Task low = createTask("Later", false, TaskPriority.LOW);
        taskRepository.saveAll(List.of(high, low));

        List<Task> result = taskRepository.findByPriority(TaskPriority.HIGH);

        assertEquals(1, result.size());
        assertEquals("Urgent", result.get(0).getTitle());
    }

    @Test
    void findTasksDueWithinDays_returnsUpcomingTasks() {
        Task soon = createTask("Soon", false, TaskPriority.MEDIUM);
        soon.setDueDate(LocalDate.now().plusDays(3));

        Task far = createTask("Far away", false, TaskPriority.LOW);
        far.setDueDate(LocalDate.now().plusDays(30));

        taskRepository.saveAll(List.of(soon, far));

        LocalDate now = LocalDate.now();
        List<Task> result = taskRepository.findTasksDueWithinDays(now, now.plusDays(7));

        assertEquals(1, result.size());
        assertEquals("Soon", result.get(0).getTitle());
    }

    @Test
    void saveAndDelete() {
        Task task = createTask("Temp", false, TaskPriority.LOW);
        Task saved = taskRepository.save(task);
        assertTrue(taskRepository.existsById(saved.getId()));

        taskRepository.deleteById(saved.getId());
        assertFalse(taskRepository.existsById(saved.getId()));
    }

    private Task createTask(String title, boolean completed, TaskPriority priority) {
        Task task = new Task();
        task.setTitle(title);
        task.setCompleted(completed);
        task.setPriority(priority);
        task.setCreatedAt(LocalDateTime.now());
        return task;
    }
}
