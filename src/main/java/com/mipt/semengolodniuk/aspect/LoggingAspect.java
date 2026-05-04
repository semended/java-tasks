package com.mipt.semengolodniuk.aspect;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Аспект для логирования вызовов сервисов.
 */
@Aspect
@Component
public class LoggingAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class);
    private static final Path LOG_FILE = Path.of("service", "service.log");

    @Around("execution(* com.mipt.semengolodniuk.service..*(..))")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        String arguments = Arrays.toString(joinPoint.getArgs());

        writeToFile(LocalDateTime.now() + " | START | " + methodName + " | args=" + arguments);

        try {
            Object result = joinPoint.proceed();
            String resultValue = result == null ? "void" : result.toString();
            writeToFile(LocalDateTime.now() + " | FINISH | " + methodName + " | result=" + resultValue);
            return result;
        } catch (Throwable exception) {
            writeToFile(LocalDateTime.now() + " | ERROR | " + methodName + " | message=" + exception.getMessage());
            throw exception;
        }
    }

    private void writeToFile(String message) {
        try {
            Files.createDirectories(LOG_FILE.getParent());
            Files.writeString(
                    LOG_FILE,
                    message + System.lineSeparator(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException exception) {
            LOGGER.warn("Cannot write service log file", exception);
        }
    }
}
