package ru.mipt.gateway.api;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mipt.gateway.dto.TaskDto;
import ru.mipt.gateway.service.TaskGatewayService;

import java.util.List;

/**
 * Gateway controller that proxies task CRUD requests to the external service.
 * All endpoints require ROLE_USER.  Rate limiting and circuit breaking are
 * applied at the service layer via Resilience4j annotations.
 */
@RestController
@RequestMapping("/api/v1/tasks")
public class TaskGatewayController {

    private final TaskGatewayService gatewayService;

    public TaskGatewayController(TaskGatewayService gatewayService) {
        this.gatewayService = gatewayService;
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> getAll(@RequestParam(required = false) String mode) {
        return ResponseEntity.ok(gatewayService.getAllTasks(mode));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getById(@PathVariable Long id,
                                           @RequestParam(required = false) String mode) {
        return ResponseEntity.ok(gatewayService.getTask(id, mode));
    }

    @PostMapping
    public ResponseEntity<TaskDto> create(@RequestBody TaskDto dto,
                                          @RequestParam(required = false) String mode) {
        TaskDto created = gatewayService.createTask(dto, mode);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> update(@PathVariable Long id,
                                          @RequestBody TaskDto dto,
                                          @RequestParam(required = false) String mode) {
        return ResponseEntity.ok(gatewayService.updateTask(id, dto, mode));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @RequestParam(required = false) String mode) {
        gatewayService.deleteTask(id, mode);
        return ResponseEntity.noContent().build();
    }

    /**
     * Catches rate limiter rejections and returns a 429 with a helpful message.
     */
    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<ProblemDetail> handleRateLimit(RequestNotPermitted ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.TOO_MANY_REQUESTS,
                "Rate limit exceeded for task gateway. Try again later.");
        problem.setTitle("Too Many Requests");
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(problem);
    }
}
