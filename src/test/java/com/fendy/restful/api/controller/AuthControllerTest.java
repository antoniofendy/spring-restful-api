package com.fendy.restful.api.controller;

import antlr.Token;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fendy.restful.api.entity.User;
import com.fendy.restful.api.model.LoginUserRequest;
import com.fendy.restful.api.model.TokenResponse;
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
class AuthControllerTest {

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
    void testLoginSuccess() throws Exception{

        User user = new User();
        user.setUsername("test_fendy");
        user.setPassword(BCrypt.hashpw("test_password", BCrypt.gensalt()));
        user.setName("test_fendy");

        userRepository.save(user);

        LoginUserRequest loginUserRequest = new LoginUserRequest();
        loginUserRequest.setUsername("test_fendy");
        loginUserRequest.setPassword("test_password");

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUserRequest))
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            String responseBody = result.getResponse().getContentAsString();
            JsonNode responseJson = objectMapper.readTree(responseBody);

            assertTrue(responseJson.get("errors").isNull());
            assertFalse(responseJson.get("data").get("token").isNull());
            assertFalse(responseJson.get("data").get("expiredAt").isNull());

            User userDb = userRepository.findById("test_fendy").orElse(null);
            assertNotNull(userDb);
            assertEquals(responseJson.get("data").get("token").asText(), userDb.getToken());
            assertEquals(responseJson.get("data").get("expiredAt").asLong(), userDb.getTokenExpiredAt());
        });
    }

    @Test
    void testLoginFailedUserNotFound() throws Exception{

        LoginUserRequest loginUserRequest = new LoginUserRequest();
        loginUserRequest.setUsername("test_fendy");
        loginUserRequest.setPassword("test_password");

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUserRequest))
        ).andExpect(
                status().isUnauthorized()
        ).andDo(result -> {
            String responeBody = result.getResponse().getContentAsString();
            JsonNode jsonNode = objectMapper.readTree(responeBody);

            assertFalse(jsonNode.get("errors").isNull());
        });
    }

    @Test
    void testLoginFailedWrongPassword() throws Exception{

        User user = new User();
        user.setUsername("test_fendy");
        user.setPassword(BCrypt.hashpw("test_password", BCrypt.gensalt()));
        user.setName("test_fendy");

        userRepository.save(user);

        LoginUserRequest loginUserRequest = new LoginUserRequest();
        loginUserRequest.setUsername("test_fendy");
        loginUserRequest.setPassword("test123_password");

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUserRequest))
        ).andExpect(
                status().isUnauthorized()
        ).andDo(result -> {
            String responseBody = result.getResponse().getContentAsString();
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            assertFalse(jsonNode.get("errors").isNull());
        });
    }

}
