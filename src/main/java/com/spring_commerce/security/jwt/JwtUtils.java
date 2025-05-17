package com.spring_commerce.security.jwt;

import java.security.Key;
import java.util.Date;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import com.spring_commerce.security.services.UserDetailsImplementation;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@ConfigurationProperties(prefix = "spring.app")
@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${spring.app.jwtExpirationMs}")
    private int JwtExpirationMs;

    @Value("${spring.app.jwtSecret}")
    private String JwtSecret;

    @Value("${spring.app.jwtCookieName}")
    private String jwtCookieName;

    // public String getJwtFromHeader(HttpServletRequest request) {
    // String token = request.getHeader("Authorization");
    // logger.debug(String.format("Authorization Header: %s", token));

    // // Return null if header is missing or does not start with expected prefix
    // if (token == null || !token.startsWith("Bearer ")) {
    // return null;
    // }

    // // Extracts the token part after "Bearer "
    // return token.substring("Bearer ".length());
    // }

    // Retrieves the JWT token from cookies in the HTTP request
    public String getJwtFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtCookieName);
        return (cookie != null) ? cookie.getValue() : null;
    }

    // Generates a JWT cookie for the authenticated user
    public ResponseCookie generateJwtCookie(UserDetailsImplementation userPrincipal) {
        String jwt = generateTokenFromUsername(userPrincipal.getUsername());

        return ResponseCookie.from(jwtCookieName, jwt)
                .path("/api")
                .maxAge(24 * 60 * 60) // 1 day in seconds
                .httpOnly(false)
                .build();
    }

    // Generates a JWT token for the given username
    public String generateTokenFromUsername(String username) {
        return Jwts.builder()
                .subject(username) // Set subject as username
                .issuedAt(new Date()) // Token issue time
                .expiration(new Date(System.currentTimeMillis() + JwtExpirationMs)) // Token expiration
                .signWith(key()) // Sign with secret key
                .compact(); // Build the token
    }

    // Extracts the username (subject) from a valid JWT token
    public String getUsernameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key()) // Verify token with signing key
                .build()
                .parseSignedClaims(token) // Parse the JWT
                .getPayload()
                .getSubject(); // Return the subject (username)
    }

    // Generates the signing key from the Base64-encoded JWT secret
    public Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(JwtSecret));
    }

    // Validates the JWT token; returns true if valid, false otherwise
    public Boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) key()) // Validate signature with secret key
                    .build()
                    .parseSignedClaims(authToken); // Parse and validate token
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: Malformed", e);
        } catch (ExpiredJwtException e) {
            logger.warn("JWT token is expired", e);
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token", e);
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty or null", e);
        }

        return false;
    }

    // Returns a cleared JWT cookie (used for logout or invalidation)
    public ResponseCookie getCleanJwtCookie() {
        return ResponseCookie.from(jwtCookieName, null)
                .path("/api")
                .build();
    }

}
