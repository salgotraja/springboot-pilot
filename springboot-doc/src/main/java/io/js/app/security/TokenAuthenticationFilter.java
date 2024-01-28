package io.js.app.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;

@Component
@AllArgsConstructor
@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final TokenHelper tokenHelper;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;
    @Override
    public void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain)
            throws IOException, ServletException {

        try {
            String authToken = tokenHelper.getToken(request);
            if (authToken != null) {
                String email = tokenHelper.getUsernameFromToken(authToken);
                log.info("email: {}", email);
                if (email != null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                    if (tokenHelper.validateToken(authToken, userDetails)) {
                        TokenBasedAuthentication authentication =
                                new TokenBasedAuthentication(authToken, userDetails);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
            chain.doFilter(request, response);
        } catch (ExpiredJwtException ex) {
            log.info("Token expired: {}", ex.getMessage());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            /*Problem problem = Problem.builder()
                    .withStatus(Status.FORBIDDEN)
                    .withTitle("Token expired")
                    .withDetail(ex.getMessage())
                    .build();*/
            ProblemDetail problemDetail = handleJWTExpiredException(ex);
            response.setContentType("application/problem+json");

            response.getWriter().write(objectMapper.writeValueAsString(problemDetail));
        }
    }


    ProblemDetail handleJWTExpiredException(ExpiredJwtException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, e.getMessage());
        problemDetail.setTitle("Token expired");
        problemDetail.setType(URI.create("https://api/auth/login/errors/not-found"));
        //problemDetail.setProperty("errorCategory", "Generic");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}
