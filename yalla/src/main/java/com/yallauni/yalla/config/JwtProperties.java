package com.yallauni.yalla.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        String issuer,
        String audience,
        String secret,
        long ttlMinutes) {
}
