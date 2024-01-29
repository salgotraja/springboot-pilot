package io.js.app.users.api;

import io.js.app.config.ApplicationProperties;
import io.js.app.exception.TokenNotExistException;
import io.js.app.exception.UserNotFoundException;
import io.js.app.security.TokenHelper;
import io.js.app.security.refresh.RefreshToken;
import io.js.app.security.refresh.RefreshTokenRequestDto;
import io.js.app.security.refresh.RefreshTokenService;
import io.js.app.users.AuthenticationRequest;
import io.js.app.users.AuthenticationResponse;
import io.js.app.users.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final TokenHelper tokenHelper;
    private final RefreshTokenService refreshTokenService;
    private final ApplicationProperties applicationProperties;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> createAuthenticationToken(
            @RequestBody AuthenticationRequest credentials) {

        if (credentials.getUsername() == null || credentials.getUsername().isEmpty()
                || credentials.getPassword() == null || credentials.getPassword().isEmpty()) {
            throw new BadCredentialsException("Username and password must be provided");
        }

        try {
            var authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    credentials.getUsername(), credentials.getPassword()));

            log.info("authentication: {}", authentication);
            if (authentication.isAuthenticated()) {
                RefreshToken refreshToken = refreshTokenService.createRefreshToken(credentials.getUsername());
                log.info("refresh token: {}", refreshToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                var user = (UserDetails) authentication.getPrincipal();
                log.info("user: {}", user);
                String accessToken = tokenHelper.generateToken(user.getUsername());
                log.info("access token: {}", accessToken);
                return ResponseEntity.ok(getAuthenticationResponse(accessToken, refreshToken.getToken()));
            } else {
                throw new UserNotFoundException("Username not found");
            }
        }catch (BadCredentialsException ex) {
            log.error("{}", ex.getMessage(), ex);
            throw new UserNotFoundException(ex.getMessage());
        } catch (Exception e) {
            log.error("{}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<AuthenticationResponse> refreshToken(@RequestBody RefreshTokenRequestDto refreshTokenRequestDTO) {
        Optional<RefreshToken> refreshTokenOpt = refreshTokenService.findByToken(refreshTokenRequestDTO.getToken());

        RefreshToken refreshToken = refreshTokenOpt
                .map(refreshTokenService::verifyExpiration)
                .orElseThrow(() -> new TokenNotExistException("Refresh Token is not in DB"));

        User user = refreshToken.getUser();
        String accessToken = tokenHelper.generateToken(user.getEmail());
        AuthenticationResponse authResponse = getAuthenticationResponse(accessToken, refreshTokenRequestDTO.getToken());

        return ResponseEntity.ok(authResponse);
    }


    private AuthenticationResponse getAuthenticationResponse(String accessToken, String refreshToken) {
        return new AuthenticationResponse(
                accessToken,
                refreshToken,
                LocalDateTime.now().plusSeconds(applicationProperties.getJwt().getExpiresIn())
        );
    }
}
