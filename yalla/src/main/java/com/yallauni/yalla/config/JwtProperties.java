// Declares the package for this configuration class
package com.yallauni.yalla.config;

// Importing Spring Boot annotation for binding configuration properties
import org.springframework.boot.context.properties.ConfigurationProperties;

// Binds properties with prefix 'app.jwt' from application.properties or application.yml to this class
@ConfigurationProperties(prefix = "app.jwt")
// This record holds JWT (JSON Web Token) configuration settings loaded from configuration
public record JwtProperties(
        // The issuer of the JWT, usually the application name or URL
        String issuer,
        // The intended audience for the JWT
        String audience,
        // The secret key used to sign and verify JWTs
        String secret,
        // The time-to-live (TTL) for the JWT in minutes
        long ttlMinutes) {
}
