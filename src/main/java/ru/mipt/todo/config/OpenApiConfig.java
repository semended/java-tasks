package ru.mipt.todo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация SpringDoc OpenAPI / Swagger UI.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("To-Do List Manager API")
                        .version("1.0")
                        .description("REST API для управления задачами")
                        .contact(new Contact().name("MIPT Student")));
    }
}
