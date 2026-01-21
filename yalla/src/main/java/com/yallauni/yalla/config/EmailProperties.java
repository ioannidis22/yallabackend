package com.yallauni.yalla.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

// Binds properties with prefix 'app.email' from application.properties or application.yml to this class
@ConfigurationProperties(prefix = "app.email")
// This record holds email configuration settings loaded from configuration
public record EmailProperties(
                boolean enabled,
                String from) {
}
