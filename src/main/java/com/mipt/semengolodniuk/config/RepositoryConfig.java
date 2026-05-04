package com.mipt.semengolodniuk.config;

import com.mipt.semengolodniuk.repository.StubTaskRepository;
import com.mipt.semengolodniuk.repository.TaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация для stub репозитория.
 */
@Configuration
public class RepositoryConfig {

    @Bean("stubTaskRepository")
    public TaskRepository stubTaskRepository() {
        return new StubTaskRepository();
    }
}
