package io.js.app.users.api;

import io.js.app.config.ApplicationProperties;
import io.js.app.security.TokenHelper;
import io.js.app.users.AuthenticationRequest;
import io.js.app.users.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final TokenHelper tokenHelper;
    private final ApplicationProperties applicationProperties;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> createAuthenticationToken(
            @RequestBody AuthenticationRequest credentials) {
        log.info("inside login");
        try {
            var authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    credentials.getUsername(), credentials.getPassword()));

            log.info("authentication: {}", authentication);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            var user = (UserDetails) authentication.getPrincipal();
            log.info("user: {}", user);
            String accessToken = tokenHelper.generateToken(user.getUsername());
            log.info("access token: {}", accessToken);
            return ResponseEntity.ok(getAuthenticationResponse(accessToken));
        } catch (Exception e) {
            log.error("{}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    private AuthenticationResponse getAuthenticationResponse(String accessToken) {
        return new AuthenticationResponse(
                accessToken,
                LocalDateTime.now().plusSeconds(applicationProperties.getJwt().getExpiresIn())
        );
    }
}
