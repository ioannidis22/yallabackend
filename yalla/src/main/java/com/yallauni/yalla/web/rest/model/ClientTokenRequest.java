package com.yallauni.yalla.web.rest.model;

import jakarta.validation.constraints.NotBlank;

/**
 * Request body for client token authentication.
 */
public record ClientTokenRequest(
        @NotBlank(message = "clientId is required") String clientId,

        @NotBlank(message = "clientSecret is required") String clientSecret) {
}
