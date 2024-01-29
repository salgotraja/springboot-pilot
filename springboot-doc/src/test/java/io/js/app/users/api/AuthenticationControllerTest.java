package io.js.app.users.api;

import io.js.app.config.ApplicationProperties;
import io.js.app.exception.TokenNotExistException;
import io.js.app.security.TokenHelper;
import io.js.app.security.refresh.RefreshToken;
import io.js.app.security.refresh.RefreshTokenService;
import io.js.app.users.RoleEnum;
import io.js.app.users.User;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = AuthenticationController.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RefreshTokenService refreshTokenService;

    @MockBean
    private TokenHelper tokenHelper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private  ApplicationProperties applicationProperties;

    @MockBean
    private SecurityFilterChain filterChain;

    @Autowired
    private WebApplicationContext context;


    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        ApplicationProperties.JwtConfig jwtConfig = mock(ApplicationProperties.JwtConfig.class);
        given(jwtConfig.getExpiresIn()).willReturn(3600L);
        given(applicationProperties.getJwt()).willReturn(jwtConfig);
    }


    @WithMockUser
    @Test
    public void authenticate_WhenValidCredentials_ShouldReturnToken() throws Exception {
        String username = "testuser";
        String password = "testpassword";
        String token = "validToken";
        String refreshToken = "refreshToken";

        UserDetails mockUserDetails = new org.springframework.security.core.userdetails.User(
                username, password, Collections.emptyList());

        Authentication mockAuthentication = mock(Authentication.class);
        given(mockAuthentication.isAuthenticated()).willReturn(true);
        given(mockAuthentication.getPrincipal()).willReturn(mockUserDetails);

        given(authenticationManager.authenticate(any(Authentication.class))).willReturn(mockAuthentication);
        given(refreshTokenService.createRefreshToken(username)).willReturn(RefreshToken.builder().token(refreshToken).build());
        given(tokenHelper.generateToken(username)).willReturn(token);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token", equalTo(token)))
                .andExpect(jsonPath("$.token", equalTo(refreshToken)));
    }



    @WithMockUser
    @Test
    public void refreshToken_WhenTokenNotExist_ShouldReturnNotFound() throws Exception {
        String token = "invalidToken";
       // when(refreshTokenService.findByToken(token)).thenReturn(Optional.empty());
        given(refreshTokenService.findByToken(token)).willReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/refreshToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"" + token + "\"}"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", equalTo("Associated user not found or token is invalid")));

    }

    @WithMockUser
    @Test
    public void givenInvalidCredentials_whenAuthenticate_thenShouldReturnUnauthorized() throws Exception {
        // Arrange
        String invalidUsername = "invalidUsername";
        String invalidPassword = "invalidPassword";

        // Mock the AuthenticationManager to throw a BadCredentialsException when invalid credentials are used
        given(authenticationManager.authenticate(any(Authentication.class)))
                .willThrow(new BadCredentialsException("Invalid credentials"));

        String jsonBody = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", invalidUsername, invalidPassword);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser
    @Test
    public void givenInvalidCredentials_whenAuthenticate_thenShouldReturnBadCredentials() throws Exception {

        // Mock the AuthenticationManager to throw a BadCredentialsException when invalid credentials are used
        given(authenticationManager.authenticate(any(Authentication.class)))
                .willThrow(new BadCredentialsException("Bad credentials"));


        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"username\"}"))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser
    @Test
    public void refreshToken_WhenTokenExists_ShouldReturnNewToken() throws Exception {
        String token = "validToken";
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);

        User user = new User(1L, "name", "user@example.com", "secret", RoleEnum.ROLE_ADMIN);
        refreshToken.setUser(user);

        given(refreshTokenService.findByToken(token)).willReturn(Optional.of(refreshToken));
        given(refreshTokenService.verifyExpiration(refreshToken)).willReturn(refreshToken);

        mockMvc.perform(post("/api/auth/refreshToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"" + token + "\"}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()));
    }

    @WithMockUser
    @Test
    public void givenValidToken_whenValidateToken_thenShouldReturnTrue() {
        String token = "validToken";
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                "testuser", "testpassword", Collections.emptyList());

        given(tokenHelper.validateToken(token, userDetails)).willReturn(true);

        Boolean result = tokenHelper.validateToken(token, userDetails);

        assertThat(result).isTrue();
    }

    @Test
    public void getUsernameFromToken_WhenValidToken_ShouldReturnUsername() {
        String token = "validToken";
        String username = "testuser";

        given(tokenHelper.getUsernameFromToken(token)).willReturn(username);

        String result = tokenHelper.getUsernameFromToken(token);

        assertThat(result).isEqualTo(username);
    }

    @Test
    public void getToken_WhenValidRequest_ShouldReturnToken()  {
        String token = "validToken";
        HttpServletRequest request = mock(HttpServletRequest.class);
        given(tokenHelper.getToken(request)).willReturn(token);

        String result = tokenHelper.getToken(request);

        assertThat(result).isEqualTo(token);
    }

    @Test
    public void givenValidToken_whenGenerateToken_thenShouldReturnToken() {
        String username = "testuser";
        String token = "validToken";

        given(tokenHelper.generateToken(username)).willReturn(token);

        String result = tokenHelper.generateToken(username);

        assertThat(result).isEqualTo(token);
    }

    @Test
    public void givenValidToken_whenVerifyExpiration_thenShouldReturnToken() {
        String token = "validToken";
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);

        given(refreshTokenService.verifyExpiration(refreshToken)).willReturn(refreshToken);

        RefreshToken result = refreshTokenService.verifyExpiration(refreshToken);

        assertThat(result).isEqualTo(refreshToken);
    }

    @Test
    public void givenValidToken_whenFindByToken_thenShouldReturnRefreshToken() {
        String token = "validToken";
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);

        given(refreshTokenService.findByToken(token)).willReturn(Optional.of(refreshToken));

        Optional<RefreshToken> result = refreshTokenService.findByToken(token);

        assertThat(result).isEqualTo(Optional.of(refreshToken));
    }

    @Test
    public void givenValidToken_whenCreateRefreshToken_thenShouldReturnRefreshToken() {
        String email = "test@example.com";
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("validToken");

        given(refreshTokenService.createRefreshToken(email)).willReturn(refreshToken);

        RefreshToken result = refreshTokenService.createRefreshToken(email);

        assertThat(result).isEqualTo(refreshToken);
    }

    @WithMockUser
    @Test
    public void refreshToken_WhenTokenIsInvalidOrExpired_ShouldReturnAppropriateError() throws Exception {
        String invalidToken = "expiredOrInvalidToken";
        given(refreshTokenService.findByToken(invalidToken))
                .willThrow(new TokenNotExistException("Refresh Token is not in DB or has expired"));

        mockMvc.perform(post("/api/auth/refreshToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"" + invalidToken + "\"}"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", equalTo("Associated user not found or token is invalid")));
    }

    @WithMockUser
    @Test
    public void authenticate_WhenUsernameOrPasswordTooLong_ShouldReturnValidationErrors() throws Exception {
        String longUsername = "a".repeat(31);
        String longPassword = "a".repeat(51);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"username\":\"%s\",\"password\":\"%s\"}", longUsername, longPassword)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*]", hasItem("Username must be between 5 and 30 characters")))
                .andExpect(jsonPath("$[*]", hasItem("Password must be between 8 and 50 characters")));
    }

    @WithMockUser
    @Test
    public void refreshToken_WhenTokenValidButUserNonExisting_ShouldReturnNotFoundOrAppropriateError() throws Exception {
        String validToken = "validTokenButNoUser";
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(validToken);
        refreshToken.setUser(null);

        given(refreshTokenService.findByToken(validToken)).willReturn(Optional.of(refreshToken));

        mockMvc.perform(post("/api/auth/refreshToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"" + validToken + "\"}"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", equalTo("Associated user not found or token is invalid")));
    }

    @WithMockUser
    @Test
    public void authenticate_WhenMalformedJson_ShouldReturnBadRequest() throws Exception {
        String malformedJson = "{\"username\":\"testuser\", \"password\" \"testpassword\"";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockUser
    @Test
    public void refreshToken_WhenMalformedJson_ShouldReturnBadRequest() throws Exception {
        String malformedJson = "{\"token\" \"validToken\"";

        mockMvc.perform(post("/api/auth/refreshToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());
    }
}