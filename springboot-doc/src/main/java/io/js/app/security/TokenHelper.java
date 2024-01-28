package io.js.app.security;

import io.js.app.config.ApplicationProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenHelper {
    private final ApplicationProperties applicationProperties;

    public String getUsernameFromToken(String token) {
        final Claims claims = this.getAllClaimsFromToken(token);
        return claims.getSubject();
    }

    public String generateToken(String username) {
        byte[] decodedKey = Base64.getDecoder().decode(applicationProperties.getJwt().getSecret());
        SecretKeySpec secretKeySpec = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA512");
        log.info("decode key length: {}", decodedKey.length);
        return Jwts.builder()
                .issuer(applicationProperties.getJwt().getIssuer())
                .subject(username)
                .issuedAt(new Date())
                .expiration(generateExpirationDate())
                .signWith(secretKeySpec)
                .compact();
    }


   private Claims getAllClaimsFromToken(String token) {
        byte[] decodedKey = Base64.getDecoder().decode(applicationProperties.getJwt().getSecret());
        SecretKeySpec secretKeySpec = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA512");
        var parser = Jwts
                .parser()
                .verifyWith(secretKeySpec).build();

        return parser.parseSignedClaims(token).getPayload();
    }

    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + applicationProperties.getJwt().getExpiresIn() * 1000);
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        log.info("username: {}", username);

        final Claims claims = this.getAllClaimsFromToken(token);
        final Date expirationDate = claims.getExpiration();
        log.info("expirationDate: {}", expirationDate);

        boolean isTokenExpired = expirationDate.before(new Date());
        log.info("isTokenExpired: {}", isTokenExpired);

        return username != null && username.equals(userDetails.getUsername()) && !isTokenExpired;
    }


    public String getToken(HttpServletRequest request) {
        String authHeader = request.getHeader(applicationProperties.getJwt().getHeader());
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
