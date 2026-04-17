package ru.mipt.todo.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Аспект для логирования вызовов методов в слое сервисов.
 */
@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("within(ru.mipt.todo.service..*)")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        log.debug(">> {} args={}", methodName, Arrays.toString(args));
        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long elapsed = System.currentTimeMillis() - start;
            if (result != null) {
                log.debug("<< {} returned={} ({}ms)", methodName, result, elapsed);
            } else {
                log.debug("<< {} void ({}ms)", methodName, elapsed);
            }
            return result;
        } catch (Throwable ex) {
            long elapsed = System.currentTimeMillis() - start;
            log.warn("<< {} threw {} ({}ms)", methodName, ex.getClass().getSimpleName(), elapsed);
            throw ex;
        }
    }
}
