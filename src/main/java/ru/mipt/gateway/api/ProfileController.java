package ru.mipt.gateway.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Returns basic profile information for the currently authenticated user.
 * Requires ROLE_USER (enforced via SecurityConfig).
 */
@RestController
@RequestMapping("/api/v1")
public class ProfileController {

    /**
     * Returns the username and authorities of the authenticated principal.
     */
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(Authentication authentication) {
        String username = authentication.getName();
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(", "));

        return ResponseEntity.ok(Map.of(
                "username", username,
                "authorities", authorities
        ));
    }

    /**
     * Returns a list of available documents.  Requires READ_PRIVILEGE
     * (enforced via SecurityConfig).
     */
    @GetMapping("/docs")
    public ResponseEntity<Map<String, Object>> getDocs(Authentication authentication) {
        return ResponseEntity.ok(Map.of(
                "user", authentication.getName(),
                "documents", java.util.List.of(
                        Map.of("id", 1, "title", "Spring Security Guide"),
                        Map.of("id", 2, "title", "REST API Design"),
                        Map.of("id", 3, "title", "Resilience Patterns")
                )
        ));
    }
}
