package com.yallauni.yalla.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.password-reset")
public record PasswordResetProperties(
        int tokenValidityMinutes) {
}
