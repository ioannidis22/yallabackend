package com.yallauni.yalla.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

// With this annotation, Spring reads values starting with 'stripe' from the properties
@ConfigurationProperties(prefix = "stripe")
public record StripeProperties(
        // The secret key needed for Stripe API to work
        String secretKey) {
}
