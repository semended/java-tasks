package ru.mipt.todo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Проверяет, что дата выполнения не раньше текущего момента.
 */
@Documented
@Constraint(validatedBy = DueDateNotBeforeCreationValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DueDateNotBeforeCreation {

    String message() default "Due date must not be in the past";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
