package com.mipt.semengolodniuk.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for scope demo endpoints.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ScopeDemoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnRequestScopeInfo() throws Exception {
        mockMvc.perform(get("/api/scope/request"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").exists())
                .andExpect(jsonPath("$.startedAt").exists());
    }

    @Test
    void shouldReturnPrototypeGeneratedId() throws Exception {
        mockMvc.perform(get("/api/scope/prototype"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.generatedTaskId").exists());
    }
}
