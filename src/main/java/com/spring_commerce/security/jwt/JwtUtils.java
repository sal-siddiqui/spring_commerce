package com.spring_commerce.security.jwt;

import java.security.Key;
import java.util.Date;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

@ConfigurationProperties(prefix = "spring.app")
@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${spring.app.jwtExpirationMs}")
    private int JwtExpirationMs;

    @Value("${spring.app.jwtSecret}")
    private String JwtSecret;

    public String getJwtFromHeader(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        logger.debug(String.format("Authorization Header: %s", token));

        // Return null if header is missing or does not start with expected prefix
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }

        // Extracts the token part after "Bearer "
        return token.substring("Bearer ".length());
    }

    public String generateTokenFromUsername(UserDetails userDetails) {
        String username = userDetails.getUsername();

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + JwtExpirationMs))
                .signWith(key())
                .compact();
    }

    public String getUsernameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build().parseSignedClaims(token)
                .getPayload().getSubject();
    }

    public Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(JwtSecret));
    }

    public Boolean validateJwtToken(String authToken) {
        try {
            // Parse and validate the token
            Jwts.parser()
                    .verifyWith((SecretKey) key())
                    .build()
                    .parseSignedClaims(authToken);
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
}
