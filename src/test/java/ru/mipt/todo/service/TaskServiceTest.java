package ru.mipt.todo.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.mipt.todo.dto.TaskCreateDto;
import ru.mipt.todo.dto.TaskResponseDto;
import ru.mipt.todo.dto.TaskUpdateDto;
import ru.mipt.todo.exception.TaskNotFoundException;
import ru.mipt.todo.model.TaskPriority;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
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
}
