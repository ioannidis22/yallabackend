package com.yallauni.yalla.web.rest.model;

import jakarta.validation.constraints.NotBlank;

/**
 * Request body for client token authentication.
 * Contains the credentials for API authentication.
 */
public record ClientTokenRequest(
                @NotBlank(message = "clientId is required") // The client ID for authentication
                String clientId,

                @NotBlank(message = "clientSecret is required") // The client secret for authentication
                String clientSecret) {
}
