// Service interface for authentication of API clients (student-style comment)
package com.yallauni.yalla.core.security;

import java.util.Optional; // for returning result

/**
 * Service for managing REST API (integration) clients.
 */
public interface ClientDetailsService {

    /**
     * Authenticates a client by ID and secret.
     * @param id     the client ID
     * @param secret the client secret
     * @return the client details if authentication succeeds, empty otherwise
     */
    Optional<ClientDetails> authenticate(final String id, final String secret);
}
