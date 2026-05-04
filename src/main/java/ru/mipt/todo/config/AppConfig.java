package ru.mipt.todo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * Общая конфигурация приложения с демонстрацией @Value.
 */
@Configuration
public class AppConfig {

    private static final Logger log = LoggerFactory.getLogger(AppConfig.class);

    @Value("${app.name:Todo List Manager}")
    private String appName;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @PostConstruct
    public void logConfig() {
        log.info("Starting {} v{}", appName, appVersion);
    }
}
