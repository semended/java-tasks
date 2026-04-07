# java-tasks

Домашнее задание по Java и Spring Boot.

## Что есть в проекте

- CRUD API для задач по адресу `/api/tasks`
- два репозитория: основной `InMemoryTaskRepository` и `StubTaskRepository`
- сервисная логика с `@PostConstruct` и `@PreDestroy`
- `BeanPostProcessor` для логирования жизненного цикла бинов
- аспект с `@Around` для логирования вызовов сервисов
- конфигурация через `application.yaml` и профили `dev`, `prod`, `test`
- request/prototype scoped бины
- интеграционные тесты для всех endpoint'ов

## Запуск

```bash
mvn spring-boot:run
```

По умолчанию приложение стартует на порту `8081`.
