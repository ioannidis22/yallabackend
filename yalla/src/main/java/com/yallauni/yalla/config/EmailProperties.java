// Declares the package for this configuration class
package com.yallauni.yalla.config;

// Importing Spring Boot annotation for binding configuration properties
import org.springframework.boot.context.properties.ConfigurationProperties;

// Binds properties with prefix 'app.email' from application.properties or application.yml to this class
@ConfigurationProperties(prefix = "app.email")
// This record holds email configuration settings loaded from configuration
public record EmailProperties(
        // Indicates if email functionality is enabled in the application
        boolean enabled,
        // The default sender email address for outgoing emails
        String from) {
}
