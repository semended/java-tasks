package ru.mipt.gateway.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import ru.mipt.gateway.dto.TaskDto;
import ru.mipt.gateway.exception.ExternalServiceException;
import ru.mipt.gateway.exception.TaskNotFoundException;

import java.util.List;

/**
 * HTTP client that talks to the external task service via {@link RestClient}.
 * Handles response status codes, unexpected content types, and maps errors
 * to domain exceptions.
 */
@Component
public class ExternalTaskClient {

    private static final Logger log = LoggerFactory.getLogger(ExternalTaskClient.class);

    private final RestClient restClient;

    public ExternalTaskClient(@Lazy RestClient externalRestClient) {
        this.restClient = externalRestClient;
    }

    /**
     * Fetches all tasks, optionally with a mode query param for resilience testing.
     */
    public List<TaskDto> getAllTasks(String mode) {
        try {
            TaskDto[] result = restClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/tasks");
                        if (mode != null) builder.queryParam("mode", mode);
                        return builder.build();
                    })
                    .retrieve()
                    .onStatus(this::isError, (req, resp) -> handleErrorResponse(resp.getStatusCode()))
                    .body(TaskDto[].class);
            return result != null ? List.of(result) : List.of();
        } catch (ExternalServiceException | TaskNotFoundException e) {
            throw e;
        } catch (RestClientException e) {
            log.error("Failed to fetch tasks from external service: {}", e.getMessage());
            throw new ExternalServiceException("External service unavailable", e);
        }
    }

    /**
     * Fetches a single task by ID.
     */
    public TaskDto getTask(Long id, String mode) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/tasks/{id}");
                        if (mode != null) builder.queryParam("mode", mode);
                        return builder.build(id);
                    })
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, resp) -> {
                        if (resp.getStatusCode().value() == 404) {
                            throw new TaskNotFoundException(id);
                        }
                        handleErrorResponse(resp.getStatusCode());
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (req, resp) ->
                            handleErrorResponse(resp.getStatusCode()))
                    .body(TaskDto.class);
        } catch (ExternalServiceException | TaskNotFoundException e) {
            throw e;
        } catch (RestClientException e) {
            log.error("Failed to fetch task {} from external service: {}", id, e.getMessage());
            throw new ExternalServiceException("External service unavailable", e);
        }
    }

    /**
     * Creates a new task. Logs the Location header from the 201 response.
     */
    public TaskDto createTask(TaskDto dto, String mode) {
        try {
            return restClient.post()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/tasks");
                        if (mode != null) builder.queryParam("mode", mode);
                        return builder.build();
                    })
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(dto)
                    .exchange((req, resp) -> {
                        if (resp.getStatusCode().is2xxSuccessful()) {
                            String location = resp.getHeaders().getFirst(HttpHeaders.LOCATION);
                            if (location != null) {
                                log.debug("Created task at location: {}", location);
                            }
                            return resp.bodyTo(TaskDto.class);
                        }
                        handleErrorResponse(resp.getStatusCode());
                        return null; // unreachable
                    });
        } catch (ExternalServiceException | TaskNotFoundException e) {
            throw e;
        } catch (RestClientException e) {
            log.error("Failed to create task via external service: {}", e.getMessage());
            throw new ExternalServiceException("External service unavailable", e);
        }
    }

    /**
     * Updates an existing task.
     */
    public TaskDto updateTask(Long id, TaskDto dto, String mode) {
        try {
            return restClient.put()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/tasks/{id}");
                        if (mode != null) builder.queryParam("mode", mode);
                        return builder.build(id);
                    })
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(dto)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, resp) -> {
                        if (resp.getStatusCode().value() == 404) {
                            throw new TaskNotFoundException(id);
                        }
                        handleErrorResponse(resp.getStatusCode());
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (req, resp) ->
                            handleErrorResponse(resp.getStatusCode()))
                    .body(TaskDto.class);
        } catch (ExternalServiceException | TaskNotFoundException e) {
            throw e;
        } catch (RestClientException e) {
            log.error("Failed to update task {} via external service: {}", id, e.getMessage());
            throw new ExternalServiceException("External service unavailable", e);
        }
    }

    /**
     * Deletes a task. Expects 204 No Content on success.
     */
    public void deleteTask(Long id, String mode) {
        try {
            restClient.delete()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/tasks/{id}");
                        if (mode != null) builder.queryParam("mode", mode);
                        return builder.build(id);
                    })
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, resp) -> {
                        if (resp.getStatusCode().value() == 404) {
                            throw new TaskNotFoundException(id);
                        }
                        handleErrorResponse(resp.getStatusCode());
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (req, resp) ->
                            handleErrorResponse(resp.getStatusCode()))
                    .toBodilessEntity();
        } catch (ExternalServiceException | TaskNotFoundException e) {
            throw e;
        } catch (RestClientException e) {
            log.error("Failed to delete task {} via external service: {}", id, e.getMessage());
            throw new ExternalServiceException("External service unavailable", e);
        }
    }

    private boolean isError(HttpStatusCode status) {
        return status.is4xxClientError() || status.is5xxServerError();
    }

    private void handleErrorResponse(HttpStatusCode status) {
        int code = status.value();
        log.warn("External service returned error status: {}", code);
        throw new ExternalServiceException("External service returned HTTP " + code, code);
    }
}
