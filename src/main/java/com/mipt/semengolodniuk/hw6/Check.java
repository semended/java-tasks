package com.mipt.semengolodniuk.hw6;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Check {
    boolean notNull() default false;
    long min() default Long.MIN_VALUE;
    long max() default Long.MAX_VALUE;
    int minLen() default -1;
    int maxLen() default -1;
}
