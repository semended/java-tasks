package ru.mipt.gateway.exception;

/**
 * Indicates that the external task service returned an unexpected or
 * erroneous response (5xx, unexpected content type, timeout, etc.).
 */
public class ExternalServiceException extends RuntimeException {

    private final int statusCode;

    public ExternalServiceException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 502;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
