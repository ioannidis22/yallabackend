package com.yallauni.yalla.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

// With this annotation, Spring reads values starting with 'app.password-reset' from the properties
@ConfigurationProperties(prefix = "app.password-reset")
public record PasswordResetProperties(
        // How many minutes the reset token is valid
        int tokenValidityMinutes) {
}
