package com.yallauni.yalla.core.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;

/**
 * JWT service for issuing and parsing tokens.
 */
@Service
public class JwtService {

    private final Key key;
    private final String issuer;
    private final String audience;
    private final long ttlMinutes;

    public JwtService(@Value("${app.jwt.secret}") final String secret,
            @Value("${app.jwt.issuer}") final String issuer,
            @Value("${app.jwt.audience}") final String audience,
            @Value("${app.jwt.ttl-minutes}") final long ttlMinutes) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("JWT secret must not be blank");
        }
        if (issuer == null || issuer.isBlank()) {
            throw new IllegalArgumentException("JWT issuer must not be blank");
        }
        if (audience == null || audience.isBlank()) {
            throw new IllegalArgumentException("JWT audience must not be blank");
        }
        if (ttlMinutes <= 0) {
            throw new IllegalArgumentException("JWT TTL must be positive");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.issuer = issuer;
        this.audience = audience;
        this.ttlMinutes = ttlMinutes;
    }

    /**
     * Issues a new JWT token for the given subject with roles.
     */
    public String issue(final String subject, final Collection<String> roles) {
        final Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(subject)
                .setIssuer(this.issuer)
                .setAudience(this.audience)
                .claim("roles", roles)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(Duration.ofMinutes(this.ttlMinutes))))
                .signWith(this.key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Parses and validates a JWT token.
     */
    public Claims parse(final String token) {
        return Jwts.parserBuilder()
                .requireAudience(this.audience)
                .requireIssuer(this.issuer)
                .setSigningKey(this.key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractSubject(final String token) {
        return parse(token).getSubject();
    }

    /**
     * Checks if the token is valid for the given username.
     */
    public boolean isTokenValid(final String token, final String username) {
        try {
            final String subject = extractSubject(token);
            return subject.equals(username) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(final String token) {
        return parse(token).getExpiration().before(new Date());
    }
}
