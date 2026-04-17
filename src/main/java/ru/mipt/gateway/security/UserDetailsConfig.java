package ru.mipt.gateway.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * Defines two in-memory users with BCrypt-hashed passwords that include
 * a pepper suffix.  "user" has ROLE_USER; "reader" also has READ_PRIVILEGE.
 */
@Configuration
public class UserDetailsConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder,
                                                 @Value("${app.pepper}") String pepper) {
        String rawPassword = "password" + pepper;

        UserDetails userEntry = User.builder()
                .username("user")
                .password(encoder.encode(rawPassword))
                .authorities(new SimpleGrantedAuthority("ROLE_USER"))
                .build();

        UserDetails readerEntry = User.builder()
                .username("reader")
                .password(encoder.encode(rawPassword))
                .authorities(
                        new SimpleGrantedAuthority("ROLE_USER"),
                        new SimpleGrantedAuthority("READ_PRIVILEGE"))
                .build();

        return new InMemoryUserDetailsManager(userEntry, readerEntry);
    }
}
