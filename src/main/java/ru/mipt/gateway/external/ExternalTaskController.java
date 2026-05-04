package ru.mipt.gateway.external;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mipt.gateway.dto.TaskDto;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Emulates an external task CRUD service. All data lives in memory.
 * Supports special {@code mode} query parameter to simulate failures:
 * <ul>
 *     <li>{@code timeout} - delays the response by 15 seconds</li>
 *     <li>{@code 500} - returns an Internal Server Error</li>
 *     <li>{@code 429} - returns Too Many Requests</li>
 *     <li>{@code html} - returns HTML instead of JSON</li>
 * </ul>
 */
@RestController
@RequestMapping("/external/v1/tasks")
public class ExternalTaskController {

    private static final Logger log = LoggerFactory.getLogger(ExternalTaskController.class);

    private final List<TaskDto> tasks = new CopyOnWriteArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(required = false) String mode) {
        ResponseEntity<?> unstable = handleUnstableMode(mode);
        if (unstable != null) return unstable;
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id,
                                     @RequestParam(required = false) String mode) {
        ResponseEntity<?> unstable = handleUnstableMode(mode);
        if (unstable != null) return unstable;

        return tasks.stream()
                .filter(t -> t.id().equals(id))
                .findFirst()
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> {
                    ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                            HttpStatus.NOT_FOUND, "Task not found: id=" + id);
                    problem.setTitle("Not Found");
                    return ResponseEntity.status(404).body(problem);
                });
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody TaskDto dto,
                                    @RequestParam(required = false) String mode) {
        ResponseEntity<?> unstable = handleUnstableMode(mode);
        if (unstable != null) return unstable;

        long newId = idCounter.getAndIncrement();
        TaskDto created = new TaskDto(newId, dto.title(), dto.description(), dto.completed());
        tasks.add(created);

        URI location = URI.create("/external/v1/tasks/" + newId);
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody TaskDto dto,
                                    @RequestParam(required = false) String mode) {
        ResponseEntity<?> unstable = handleUnstableMode(mode);
        if (unstable != null) return unstable;

        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).id().equals(id)) {
                TaskDto updated = new TaskDto(id, dto.title(), dto.description(), dto.completed());
                tasks.set(i, updated);
                return ResponseEntity.ok(updated);
            }
        }

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, "Task not found: id=" + id);
        return ResponseEntity.status(404).body(problem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id,
                                    @RequestParam(required = false) String mode) {
        ResponseEntity<?> unstable = handleUnstableMode(mode);
        if (unstable != null) return unstable;

        boolean removed = tasks.removeIf(t -> t.id().equals(id));
        if (removed) {
            return ResponseEntity.noContent().build();
        }

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, "Task not found: id=" + id);
        return ResponseEntity.status(404).body(problem);
    }

    /**
     * Simulates failure modes for resilience testing.
     * Returns a non-null ResponseEntity if a failure was triggered.
     */
    private ResponseEntity<?> handleUnstableMode(String mode) {
        if (mode == null) return null;

        return switch (mode) {
            case "timeout" -> {
                log.info("Simulating timeout (15s delay)");
                try {
                    Thread.sleep(15_000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                yield ResponseEntity.ok(Map.of("message", "delayed response"));
            }
            case "500" -> {
                log.info("Simulating 500 Internal Server Error");
                yield ResponseEntity.status(500)
                        .body(Map.of("error", "Simulated internal server error"));
            }
            case "429" -> {
                log.info("Simulating 429 Too Many Requests");
                yield ResponseEntity.status(429)
                        .body(Map.of("error", "Simulated rate limit exceeded"));
            }
            case "html" -> {
                log.info("Simulating unexpected HTML response");
                yield ResponseEntity.ok()
                        .contentType(MediaType.TEXT_HTML)
                        .body("<html><body><h1>Unexpected HTML</h1></body></html>");
            }
            default -> null;
        };
    }
}
