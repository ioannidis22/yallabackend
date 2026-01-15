package com.yallauni.yalla.web.rest.model;

/**
 * Response body for client token authentication.
 */
public record ClientTokenResponse(
        String accessToken, // The issued access token
        String tokenType,   // The type of the token (e.g., "Bearer")
        long expiresIn      // Token expiration time in seconds
) {
}
