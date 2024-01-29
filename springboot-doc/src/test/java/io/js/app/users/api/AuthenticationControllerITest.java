package io.js.app.users.api;

import com.jayway.jsonpath.JsonPath;
import io.js.app.infra.TestContainerConfiguration;
import io.js.app.security.refresh.RefreshTokenRepository;
import io.js.app.users.RoleEnum;
import io.js.app.users.User;
import io.js.app.users.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
@Import(TestContainerConfiguration.class)
public class AuthenticationControllerITest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp(){
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
        List<User> users = new ArrayList<>();
        users.add(new User(null, "User1", "user1@gmail.com", "$2a$10$i0R8wY4iRM1JeFRUgFRtKObmxi7pJW6J9fZlXGyPR9C4rLjjxyxlu", RoleEnum.ROLE_ADMIN));
        users.add(new User(null, "User2", "user2@gmail.com", "$2a$10$i0R8wY4iRM1JeFRUgFRtKObmxi7pJW6J9fZlXGyPR9C4rLjjxyxlu", RoleEnum.ROLE_USER));

        userRepository.saveAll(users);

    }

    @Test
    void shouldCreateAuthenticationToken() throws Exception {

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {"username":"user1@gmail.com","password":"password"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token",notNullValue()))
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.expires_at", notNullValue()));
    }

    @Test
    void shouldRejectInvalidCredentials() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"wronguser@gmail.com\",\"password\":\"wrongpassword\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectRequestWithMissingUsername() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\":\"password\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRefreshToken() throws Exception {
        // First, authenticate to get the refresh token
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user1@gmail.com\",\"password\":\"password\"}"))
                .andReturn();

        String refreshToken = JsonPath.read(result.getResponse().getContentAsString(), "$.token");

        // Now, use the refresh token to get a new access token
        mockMvc.perform(post("/api/auth/refreshToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"" + refreshToken + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token", notNullValue()));
    }

    @Test
    void shouldHandleInvalidRefreshToken() throws Exception {
        String invalidToken = "invalidOrExpiredToken";

        mockMvc.perform(post("/api/auth/refreshToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"" + invalidToken + "\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is("Associated user not found or token is invalid")));
    }

    @Test
    void shouldHandleLongUsernameAndPassword() throws Exception {
        String longUsername = String.join("", Collections.nCopies(31, "a"));
        String longPassword = String.join("", Collections.nCopies(51, "b"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + longUsername + "\",\"password\":\"" + longPassword + "\"}"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*]", hasItem("Username must be between 5 and 30 characters")))
                .andExpect(jsonPath("$[*]", hasItem("Password must be between 8 and 50 characters")));

    }

}
