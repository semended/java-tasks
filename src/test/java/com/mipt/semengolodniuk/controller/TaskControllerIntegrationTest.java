package com.mipt.semengolodniuk.controller;

import com.mipt.semengolodniuk.model.Task;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for task controller endpoints.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TaskControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void shouldReturnAllTasks() {
        ResponseEntity<Task[]> response = testRestTemplate.getForEntity(baseUrl(), Task[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length >= 2);
    }

    @Test
    void shouldReturnBadRequestForInvalidCompletedFilter() {
        ResponseEntity<String> response = testRestTemplate.getForEntity(baseUrl() + "?completed=wrong", String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void shouldReturnTaskById() {
        Task createdTask = createTask("Read book", "Finish Spring chapter", false);

        ResponseEntity<Task> response = testRestTemplate.getForEntity(baseUrl() + "/" + createdTask.getId(), Task.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(createdTask.getId(), response.getBody().getId());
    }

    @Test
    void shouldReturnNotFoundForMissingTaskById() {
        ResponseEntity<Map> response = testRestTemplate.getForEntity(baseUrl() + "/missing-task", Map.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().get("status"));
    }

    @Test
    void shouldCreateTask() {
        Task request = new Task(null, "Create report", "Prepare weekly report", false);

        ResponseEntity<Task> response = testRestTemplate.postForEntity(baseUrl(), request, Task.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals("Create report", response.getBody().getTitle());
    }

    @Test
    void shouldRejectTaskCreationWithBlankTitle() {
        Task request = new Task(null, "   ", "No title here", false);

        ResponseEntity<Map> response = testRestTemplate.postForEntity(baseUrl(), request, Map.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().get("status"));
    }

    @Test
    void shouldUpdateTask() {
        Task createdTask = createTask("Old title", "Old description", false);
        Task updateRequest = new Task(null, "New title", "New description", true);

        ResponseEntity<Task> response = testRestTemplate.exchange(
                baseUrl() + "/" + createdTask.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                Task.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("New title", response.getBody().getTitle());
        assertTrue(response.getBody().isCompleted());
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingMissingTask() {
        Task updateRequest = new Task(null, "Updated", "Still missing", true);

        ResponseEntity<Map> response = testRestTemplate.exchange(
                baseUrl() + "/unknown-id",
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                Map.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().get("status"));
    }

    @Test
    void shouldDeleteTask() {
        Task createdTask = createTask("Delete me", "Temporary task", false);

        ResponseEntity<Void> deleteResponse = testRestTemplate.exchange(
                baseUrl() + "/" + createdTask.getId(),
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Void.class
        );

        ResponseEntity<Map> getAfterDeleteResponse = testRestTemplate.getForEntity(
                baseUrl() + "/" + createdTask.getId(),
                Map.class
        );

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, getAfterDeleteResponse.getStatusCode());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingMissingTask() {
        ResponseEntity<Map> response = testRestTemplate.exchange(
                baseUrl() + "/missing-delete-id",
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Map.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().get("status"));
    }

    private Task createTask(String title, String description, boolean completed) {
        Task request = new Task(null, title, description, completed);
        ResponseEntity<Task> response = testRestTemplate.postForEntity(baseUrl(), request, Task.class);
        return response.getBody();
    }

    private String baseUrl() {
        return "http://localhost:" + port + "/api/tasks";
    }
}
