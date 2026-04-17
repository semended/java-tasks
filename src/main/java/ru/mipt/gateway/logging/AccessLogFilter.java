package ru.mipt.gateway.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Logs every HTTP request with method, URI, status and duration.
 * If an Authorization header is present, its JWT value is masked
 * to show only the first and last 6 characters.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class AccessLogFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(AccessLogFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        long start = System.currentTimeMillis();

        chain.doFilter(request, response);

        long duration = System.currentTimeMillis() - start;
        String authHeader = maskAuthorization(request.getHeader(HttpHeaders.AUTHORIZATION));

        log.info("{} {} -> {} ({}ms) auth=[{}]",
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus(),
                duration,
                authHeader);
    }

    /**
     * Masks a Bearer token to reveal only the first 6 and last 6 characters,
     * for example: "Bearer eyJhbG...abc123".
     */
    private String maskAuthorization(String header) {
        if (header == null || header.isBlank()) {
            return "none";
        }
        String token = header;
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (token.length() <= 12) {
            return "***";
        }
        return token.substring(0, 6) + "..." + token.substring(token.length() - 6);
    }
}
