package ru.mipt.todo.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.mipt.todo.dto.TaskCreateDto;
import ru.mipt.todo.dto.TaskResponseDto;
import ru.mipt.todo.dto.TaskUpdateDto;
import ru.mipt.todo.exception.TaskNotFoundException;
import ru.mipt.todo.model.TaskPriority;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TaskServiceTest {

    @Autowired
    private TaskService taskService;

    @Test
    void createAndGetTask() {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Service test");
        dto.setPriority(TaskPriority.MEDIUM);
        dto.setDueDate(LocalDate.now().plusDays(5));

        TaskResponseDto created = taskService.createTask(dto);
        assertNotNull(created.getId());

        TaskResponseDto fetched = taskService.getTaskById(created.getId());
        assertEquals("Service test", fetched.getTitle());
    }

    @Test
    void updateTask_changesTitle() {
        TaskCreateDto createDto = new TaskCreateDto();
        createDto.setTitle("Before");
        createDto.setPriority(TaskPriority.LOW);
        TaskResponseDto created = taskService.createTask(createDto);

        TaskUpdateDto updateDto = new TaskUpdateDto();
        updateDto.setTitle("After");
        TaskResponseDto updated = taskService.updateTask(created.getId(), updateDto);

        assertEquals("After", updated.getTitle());
    }

    @Test
    void deleteTask_removesIt() {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("To remove");
        dto.setPriority(TaskPriority.HIGH);
        TaskResponseDto created = taskService.createTask(dto);

        taskService.deleteTask(created.getId());

        assertThrows(TaskNotFoundException.class,
                () -> taskService.getTaskById(created.getId()));
    }

    @Test
    void bulkComplete_rollsBackOnMissing() {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Exists");
        dto.setPriority(TaskPriority.LOW);
        TaskResponseDto existing = taskService.createTask(dto);

        List<Long> ids = List.of(existing.getId(), 99999L);
        assertThrows(TaskNotFoundException.class,
                () -> taskService.bulkCompleteTasks(ids));
    }

    @Test
    void bulkComplete_success() {
        TaskCreateDto dto1 = new TaskCreateDto();
        dto1.setTitle("Task 1");
        dto1.setPriority(TaskPriority.LOW);
        TaskCreateDto dto2 = new TaskCreateDto();
        dto2.setTitle("Task 2");
        dto2.setPriority(TaskPriority.MEDIUM);

        TaskResponseDto t1 = taskService.createTask(dto1);
        TaskResponseDto t2 = taskService.createTask(dto2);

        List<TaskResponseDto> result = taskService.bulkCompleteTasks(
                List.of(t1.getId(), t2.getId()));

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(TaskResponseDto::isCompleted));
    }
}
