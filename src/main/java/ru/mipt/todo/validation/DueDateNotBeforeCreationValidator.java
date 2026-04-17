package ru.mipt.todo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

/**
 * Валидатор для {@link DueDateNotBeforeCreation}.
 * Пропускает null (nullable поле), проверяет что дата не в прошлом.
 */
public class DueDateNotBeforeCreationValidator
        implements ConstraintValidator<DueDateNotBeforeCreation, LocalDate> {

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return !value.isBefore(LocalDate.now());
    }
}
