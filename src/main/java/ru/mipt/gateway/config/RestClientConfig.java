package ru.mipt.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import ru.mipt.gateway.exception.ExternalServiceException;

import java.time.Duration;

/**
 * Provides a configured {@link RestClient} bean pointing at the external
 * task service.  Timeouts: 5 seconds for connect, 10 seconds for read.
 * The bean is lazy so that property placeholders like {@code local.server.port}
 * can be resolved after the server has started.
 */
@Configuration
public class RestClientConfig {

    private static final Logger log = LoggerFactory.getLogger(RestClientConfig.class);

    @Bean
    @Lazy
    public RestClient externalRestClient(@Value("${app.external.base-url}") String baseUrl) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(5));
        factory.setReadTimeout(Duration.ofSeconds(10));

        ClientHttpRequestInterceptor contentTypeGuard = (request, body, execution) -> {
            var response = execution.execute(request, body);
            MediaType contentType = response.getHeaders().getContentType();
            if (response.getStatusCode().is2xxSuccessful()
                    && contentType != null
                    && !contentType.isCompatibleWith(MediaType.APPLICATION_JSON)) {
                byte[] preview = response.getBody().readNBytes(200);
                log.warn("Unexpected Content-Type '{}' from {}. Body preview: {}",
                        contentType, request.getURI(), new String(preview));
                throw new ExternalServiceException(
                        "Unexpected Content-Type: " + contentType, 502);
            }
            return response;
        };

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(new BufferingClientHttpRequestFactory(factory))
                .requestInterceptor(contentTypeGuard)
                .defaultHeader("Accept", "application/json")
                .defaultHeader("User-Agent", "http-gateway/1.0")
                .build();
    }
}
