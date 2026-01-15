// Declares the package where this class belongs
package com.yallauni.yalla.config;

// Importing Spring Boot annotation for binding configuration properties
import org.springframework.boot.context.properties.ConfigurationProperties;

// Binds properties with prefix 'app.api' from application.properties or application.yml to this class
@ConfigurationProperties(prefix = "app.api")
// This record holds API client credentials loaded from configuration
public record ApiClientProperties(
        // The client ID used to identify the application to the external API
        String clientId,
        // The client secret used for authenticating the application to the external API
        String clientSecret) {
}
