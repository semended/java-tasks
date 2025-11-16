package com.mipt.semengolodniuk.hw6;

import com.mipt.semengolodniuk.hw6.annotations.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public final class Validator {

    public static List<ValidationError> validate(Object target) {
        List<ValidationError> errors = new ArrayList<>();
        if (target == null) {
            errors.add(new ValidationError("<root>", "object is null"));
            return errors;
        }

        for (Field f : target.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            Object value;
            try {
                value = f.get(target);
            } catch (IllegalAccessException e) {
                continue;
            }

            NotNull nn = f.getAnnotation(NotNull.class);
            if (nn != null && value == null) {
                errors.add(new ValidationError(f.getName(), msgOr(nn.message(), "must not be null")));
                continue; // дальше Min/Max смысла нет
            }

            Min min = f.getAnnotation(Min.class);
            if (min != null && value != null && value instanceof Number n) {
                if (n.doubleValue() < min.value()) {
                    errors.add(new ValidationError(f.getName(), msgOr(min.message(), "must be >= " + min.value())));
                }
            }

            Max max = f.getAnnotation(Max.class);
            if (max != null && value != null && value instanceof Number n) {
                if (n.doubleValue() > max.value()) {
                    errors.add(new ValidationError(f.getName(), msgOr(max.message(), "must be <= " + max.value())));
                }
            }
        }
        return errors;
    }

    private static String msgOr(String candidate, String fallback) {
        return (candidate == null || candidate.isBlank()) ? fallback : candidate;
    }
}
