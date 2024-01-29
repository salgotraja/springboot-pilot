package io.js.app.users.api;

import com.jayway.jsonpath.JsonPath;
import io.js.app.config.ApplicationProperties;
import io.js.app.infra.TestContainerConfiguration;
import io.js.app.security.TokenHelper;
import io.js.app.security.refresh.RefreshTokenRepository;
import io.js.app.security.refresh.RefreshTokenService;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
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
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenHelper tokenHelper;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private  ApplicationProperties applicationProperties;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp(){
        userRepository.deleteAll();
        refreshTokenRepository.deleteAll();
        List<User> users = new ArrayList<>();
        users.add(new User(null, "User1", "user1@gmail.com", "$2a$10$6eIiLEgHeQQ3OSPXZ1kaleC.mq3F0N./WI/PbVBRFFe0I8aU7QE9C", RoleEnum.ROLE_ADMIN));
        users.add(new User(null, "User2", "user2@gmail.com", "$2a$10$6eIiLEgHeQQ3OSPXZ1kaleC.mq3F0N./WI/PbVBRFFe0I8aU7QE9C", RoleEnum.ROLE_USER));

        userRepository.saveAll(users);

    }

    @Test
    void shouldCreateAuthenticationToken() throws Exception {

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {"username":"user1@gmail.com","password":"secret"}
                                """))
                .andDo(print())
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
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectRequestWithMissingUsername() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\":\"secret\"}"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRefreshToken() throws Exception {
        // First, authenticate to get the refresh token
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user1@gmail.com\",\"password\":\"secret\"}"))
                .andReturn();

        String refreshToken = JsonPath.read(result.getResponse().getContentAsString(), "$.token");

        // Now, use the refresh token to get a new access token
        mockMvc.perform(post("/api/auth/refreshToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"" + refreshToken + "\"}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token", notNullValue()));
    }
}
