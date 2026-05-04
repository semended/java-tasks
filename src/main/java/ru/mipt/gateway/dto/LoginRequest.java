package ru.mipt.gateway.dto;

/**
 * Request body for the login endpoint.
 *
 * @param username the user's login name
 * @param password the user's plain-text password
 */
public record LoginRequest(String username, String password) {
}
