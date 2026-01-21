package com.yallauni.yalla.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for password reset functionality.
 * Binds properties prefixed with 'app.password-reset' from application.yml.
 */
@ConfigurationProperties(prefix = "app.password-reset")
public record PasswordResetProperties(
                int tokenValidityMinutes) {
}
