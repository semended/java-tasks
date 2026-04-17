package ru.mipt.gateway;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TaskGatewayControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        token = login("user", "password");
    }

    @Test
    void createAndGetTask() throws Exception {
        // Create
        MvcResult createResult = mockMvc.perform(post("/api/v1/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Test Task\",\"description\":\"Some description\",\"completed\":false}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andReturn();

        long taskId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("id").asLong();

        // Get by ID
        mockMvc.perform(get("/api/v1/tasks/" + taskId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    void updateTask() throws Exception {
        // Create first
        MvcResult createResult = mockMvc.perform(post("/api/v1/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Original\",\"description\":\"Original desc\",\"completed\":false}"))
                .andExpect(status().isCreated())
                .andReturn();

        long taskId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("id").asLong();

        // Update
        String updateJson = String.format(
                "{\"id\":%d,\"title\":\"Updated\",\"description\":\"Updated desc\",\"completed\":true}", taskId);

        mockMvc.perform(put("/api/v1/tasks/" + taskId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void deleteTask() throws Exception {
        // Create first
        MvcResult createResult = mockMvc.perform(post("/api/v1/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"To Delete\",\"description\":\"Will be deleted\",\"completed\":false}"))
                .andExpect(status().isCreated())
                .andReturn();

        long taskId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("id").asLong();

        // Delete
        mockMvc.perform(delete("/api/v1/tasks/" + taskId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        // Verify it's gone
        mockMvc.perform(get("/api/v1/tasks/" + taskId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllTasks() throws Exception {
        mockMvc.perform(get("/api/v1/tasks")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void tasksRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isUnauthorized());
    }

    private String login(String username, String password) throws Exception {
        String json = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("token").asText();
    }
}
