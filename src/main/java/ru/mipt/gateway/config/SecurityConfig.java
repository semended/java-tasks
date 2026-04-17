package ru.mipt.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.mipt.gateway.security.JwtAuthenticationFilter;
import ru.mipt.gateway.security.JsonUnauthorizedEntryPoint;

/**
 * Configures Spring Security for stateless JWT-based authentication.
 * CSRF is disabled (stateless API), sessions are never created,
 * and the JWT filter runs before the standard username/password filter.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final JsonUnauthorizedEntryPoint unauthorizedEntryPoint;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter,
                          JsonUnauthorizedEntryPoint unauthorizedEntryPoint) {
        this.jwtFilter = jwtFilter;
        this.unauthorizedEntryPoint = unauthorizedEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // login and external emulator are public
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/external/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        // docs require READ_PRIVILEGE
                        .requestMatchers("/api/v1/docs").hasAuthority("READ_PRIVILEGE")
                        // profile requires ROLE_USER
                        .requestMatchers("/api/v1/profile").hasRole("USER")
                        // task gateway requires ROLE_USER
                        .requestMatchers("/api/v1/tasks/**").hasRole("USER")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(unauthorizedEntryPoint)
                )
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
