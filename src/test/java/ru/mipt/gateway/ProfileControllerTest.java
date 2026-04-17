package ru.mipt.gateway;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void profileWithValidToken() throws Exception {
        String token = login("user", "password");

        mockMvc.perform(get("/api/v1/profile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"));
    }

    @Test
    void profileWithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/profile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void docsWithReadPrivilege() throws Exception {
        String token = login("reader", "password");

        mockMvc.perform(get("/api/v1/docs")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documents").isArray());
    }

    @Test
    void docsWithoutReadPrivilege() throws Exception {
        // "user" has ROLE_USER but not READ_PRIVILEGE, should get 403
        String token = login("user", "password");

        mockMvc.perform(get("/api/v1/docs")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
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
