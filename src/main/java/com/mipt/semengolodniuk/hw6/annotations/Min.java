package com.mipt.semengolodniuk.hw6.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Min {
    long value();
    String message() default "";
}
