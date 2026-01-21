package com.yallauni.yalla.web.rest.model;

/**
 * Response body for client token authentication.
 * Contains the JWT token issued after successful client authentication.
 */
public record ClientTokenResponse(
                String accessToken, // The issued access token
                String tokenType, // The type of the token ("Bearer")
                long expiresIn // Token expiration time in seconds
) {
}
