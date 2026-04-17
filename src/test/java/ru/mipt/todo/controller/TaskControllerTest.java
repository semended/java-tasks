package ru.mipt.todo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import ru.mipt.todo.dto.ErrorResponse;
import ru.mipt.todo.dto.TaskResponseDto;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TaskControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createTask_success() {
        Map<String, Object> body = Map.of(
                "title", "Test task",
                "description", "Some description",
                "priority", "HIGH",
                "dueDate", LocalDate.now().plusDays(5).toString()
        );

        ResponseEntity<TaskResponseDto> response = restTemplate.postForEntity(
                "/api/tasks", body, TaskResponseDto.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test task", response.getBody().getTitle());
        assertFalse(response.getBody().isCompleted());
    }

    @Test
    void createTask_missingTitle_returns400() {
        Map<String, Object> body = Map.of(
                "priority", "LOW"
        );

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                "/api/tasks", body, ErrorResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getTaskById_notFound() {
        ResponseEntity<ErrorResponse> response = restTemplate.getForEntity(
                "/api/tasks/99999", ErrorResponse.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getTaskById_success() {
        Map<String, Object> body = Map.of(
                "title", "Findable task",
                "priority", "MEDIUM",
                "dueDate", LocalDate.now().plusDays(3).toString()
        );
        ResponseEntity<TaskResponseDto> created = restTemplate.postForEntity(
                "/api/tasks", body, TaskResponseDto.class);
        Long taskId = created.getBody().getId();

        ResponseEntity<TaskResponseDto> response = restTemplate.getForEntity(
                "/api/tasks/" + taskId, TaskResponseDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Findable task", response.getBody().getTitle());
    }

    @Test
    void getAllTasks_success() {
        ResponseEntity<TaskResponseDto[]> response = restTemplate.getForEntity(
                "/api/tasks", TaskResponseDto[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getHeaders().get("X-Total-Count"));
    }

    @Test
    void updateTask_success() {
        Map<String, Object> createBody = Map.of(
                "title", "To update",
                "priority", "LOW",
                "dueDate", LocalDate.now().plusDays(10).toString()
        );
        ResponseEntity<TaskResponseDto> created = restTemplate.postForEntity(
                "/api/tasks", createBody, TaskResponseDto.class);
        Long taskId = created.getBody().getId();

        Map<String, Object> updateBody = Map.of("title", "Updated title");
        ResponseEntity<TaskResponseDto> response = restTemplate.exchange(
                "/api/tasks/" + taskId, HttpMethod.PUT,
                new HttpEntity<>(updateBody), TaskResponseDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated title", response.getBody().getTitle());
    }

    @Test
    void updateTask_notFound() {
        Map<String, Object> updateBody = Map.of("title", "Nope");
        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                "/api/tasks/99999", HttpMethod.PUT,
                new HttpEntity<>(updateBody), ErrorResponse.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteTask_success() {
        Map<String, Object> body = Map.of(
                "title", "To delete",
                "priority", "HIGH",
                "dueDate", LocalDate.now().plusDays(1).toString()
        );
        ResponseEntity<TaskResponseDto> created = restTemplate.postForEntity(
                "/api/tasks", body, TaskResponseDto.class);
        Long taskId = created.getBody().getId();

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/tasks/" + taskId, HttpMethod.DELETE,
                null, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void deleteTask_notFound() {
        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                "/api/tasks/99999", HttpMethod.DELETE,
                null, ErrorResponse.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
