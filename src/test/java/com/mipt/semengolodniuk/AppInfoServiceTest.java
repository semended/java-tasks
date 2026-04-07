package com.mipt.semengolodniuk;

import com.mipt.semengolodniuk.service.AppInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for application properties.
 */
@SpringBootTest
@ActiveProfiles("test")
class AppInfoServiceTest {

    @Autowired
    private AppInfoService appInfoService;

    @Test
    void shouldLoadContext() {
        assertNotNull(appInfoService);
    }

    @Test
    void shouldReadValuesFromYaml() {
        assertEquals("To-Do List Manager", appInfoService.getAppName());
        assertEquals("1.0.0", appInfoService.getAppVersion());
    }
}
