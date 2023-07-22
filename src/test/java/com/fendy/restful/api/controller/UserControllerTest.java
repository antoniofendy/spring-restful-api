package com.fendy.restful.api.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fendy.restful.api.entity.User;
import com.fendy.restful.api.model.RegisterUserRequest;
import com.fendy.restful.api.model.WebResponse;
import com.fendy.restful.api.repository.UserRepository;
import com.fendy.restful.api.security.BCrypt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testRegisterSuccess() throws Exception {

        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("test_fendy");
        request.setPassword("test_password");
        request.setName("test_fendy");

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            String responseBody = result.getResponse().getContentAsString();
            JsonNode responseJson = objectMapper.readTree(responseBody);

            assertEquals("OK", responseJson.get("data").asText());
        });
    }

    @Test
    void testRegisterBadRequest() throws Exception {

        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("");
        request.setPassword("");
        request.setName("");

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(
                status().isBadRequest()
        ).andDo(result -> {
            String responseBody = result.getResponse().getContentAsString();
            JsonNode responseJson = objectMapper.readTree(responseBody);

            assertNotNull(responseJson.get("errors"));
        });
    }

    @Test
    void testRegisterDuplicateUsername() throws Exception {

        User user = new User();
        user.setUsername("test_fendy");
        user.setPassword(BCrypt.hashpw("test_password", BCrypt.gensalt()));
        user.setName("test_fendy");

        userRepository.save(user);

        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("test_fendy");
        request.setPassword("test_password");
        request.setName("test_fendy");

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(
                status().isBadRequest()
        ).andDo(result -> {
            String responseBody = result.getResponse().getContentAsString();
            JsonNode responseJson = objectMapper.readTree(responseBody);

            assertNotNull(responseJson.get("errors"));
        });
    }

}
