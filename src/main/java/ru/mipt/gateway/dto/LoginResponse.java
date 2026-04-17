package ru.mipt.gateway.dto;

/**
 * Response body returned after a successful login.
 *
 * @param token the JWT access token
 */
public record LoginResponse(String token) {
}
