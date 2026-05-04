# java-tasks

Spring Boot homework project with a simple to-do list manager.

## What is inside

- CRUD API for tasks at `/api/tasks`
- statistics endpoint at `/api/statistics`
- scope demo endpoints at `/api/scope`
- `InMemoryTaskRepository` and `StubTaskRepository`
- `@PostConstruct`, `@PreDestroy`, `BeanPostProcessor`, and AOP logging
- config in `application.yaml` with `dev`, `prod`, and `test` profiles
- integration tests for TaskController

## Run

```bash
mvn spring-boot:run
```
