package com.mipt.semengolodniuk.config;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.annotation.RequestScope;

/**
 * Configuration for beans with non-singleton scopes.
 */
@Configuration
public class ScopeConfig {

    @Bean
    @RequestScope
    public RequestScopedBean requestScopedBean() {
        return new RequestScopedBean();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public PrototypeScopedBean prototypeScopedBean() {
        return new PrototypeScopedBean();
    }
}
