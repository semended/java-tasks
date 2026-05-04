package ru.mipt.todo.lifecycle;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Бин со скоупом prototype — новый экземпляр при каждом обращении.
 * Генерирует уникальный ID на базе UUID.
 */
@Component
@Scope("prototype")
public class PrototypeScopedBean {

    private final String generatedId = UUID.randomUUID().toString();

    public String getGeneratedId() {
        return generatedId;
    }
}
