package ru.mipt.gateway.external;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Dedicated endpoint that simulates various failure modes for resilience testing.
 * Accepts a {@code mode} query parameter: timeout, 500, 429, html.
 */
@RestController
@RequestMapping("/external/v1/unstable")
public class UnstableController {

    private static final Logger log = LoggerFactory.getLogger(UnstableController.class);

    @GetMapping
    public ResponseEntity<?> unstable(@RequestParam String mode) {
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
            default -> ResponseEntity.ok(Map.of("status", "ok", "mode", mode));
        };
    }
}
