package com.yallauni.yalla.web.rest.model;

/**
 * Response body for client token authentication.
 */
public record ClientTokenResponse(
        String accessToken,
        String tokenType,
        long expiresIn) {
}
