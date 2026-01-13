package com.yallauni.yalla.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.api")
public record ApiClientProperties(
        String clientId,
        String clientSecret) {
}
