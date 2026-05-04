package ru.mipt.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Resilient Secure HTTP Gateway application.
 * Provides an internal protected API with JWT auth and a gateway
 * to an external task service with resilience patterns.
 */
@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
