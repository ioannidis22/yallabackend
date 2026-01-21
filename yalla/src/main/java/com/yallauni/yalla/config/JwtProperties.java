package com.yallauni.yalla.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

// Binds properties with prefix 'app.jwt' from application.properties or application.yml to this class
@ConfigurationProperties(prefix = "app.jwt")
// This record holds JWT (JSON Web Token) configuration settings loaded from
// configuration
public record JwtProperties(
                // Ussuer of the JWT, usually the application name or URL
                String issuer,
                // Intended audience for the JWT
                String audience,
                // The secret key used to sign and verify JWTs
                String secret,
                // The time-to-live for the JWT in minutes
                long ttlMinutes) {
}
