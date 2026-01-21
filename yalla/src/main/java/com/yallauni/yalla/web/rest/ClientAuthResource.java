package com.yallauni.yalla.web.rest;

import com.yallauni.yalla.core.security.ClientDetails;
import com.yallauni.yalla.core.security.ClientDetailsService;
import com.yallauni.yalla.core.security.JwtService;
import com.yallauni.yalla.web.rest.model.ClientTokenRequest;
import com.yallauni.yalla.web.rest.model.ClientTokenResponse;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * REST controller for API client authentication (machine-to-machine).
 * Handles authentication requests from API clients and issues JWT tokens.
 */
@RestController
@RequestMapping(value = "/api/v1/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class ClientAuthResource {

    private final ClientDetailsService clientDetailsService; // Service for authenticating API clients
    private final JwtService jwtService; // Service for issuing JWT tokens

    public ClientAuthResource(final ClientDetailsService clientDetailsService,
            final JwtService jwtService) {
        if (clientDetailsService == null) {
            throw new NullPointerException("clientDetailsService must not be null");
        }
        if (jwtService == null) {
            throw new NullPointerException("jwtService must not be null");
        }
        this.clientDetailsService = clientDetailsService;
        this.jwtService = jwtService;
    }

    /**
     * Authenticates an API client and returns a JWT token.
     * 
     * @param request The client credentials
     * @return JWT token response if authentication is successful
     * @throws ResponseStatusException if credentials are invalid
     */
    @PostMapping("/client-tokens")
    public ClientTokenResponse clientToken(@RequestBody @Valid ClientTokenRequest request) {
        final String clientId = request.clientId();
        final String clientSecret = request.clientSecret();

        // Authenticate client
        final ClientDetails client = this.clientDetailsService
                .authenticate(clientId, clientSecret)
                .orElse(null);

        if (client == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid client credentials");
        }

        // Issue JWT token
        final String token = this.jwtService.issue("client:" + client.id(), client.roles());
        return new ClientTokenResponse(token, "Bearer", 60 * 60); // 1 hour
    }
}
