package ru.mipt.todo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Конфигурация JPA: включение автоматического аудита (createdAt, updatedAt).
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
