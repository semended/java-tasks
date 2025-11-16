package com.mipt.semengolodniuk.hw6.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Max {
    long value();
    String message() default "";
}
