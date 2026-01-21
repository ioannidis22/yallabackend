package com.yallauni.yalla.core.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Simple implementation of {@link ClientDetailsService} using application
 * properties.
 */
@Service
public class InMemoryClientDetailsService implements ClientDetailsService {

    private final String clientId;
    private final String clientSecretHash;
    private final PasswordEncoder passwordEncoder;

    public InMemoryClientDetailsService(
            @Value("${app.api.client-id:yalla-mobile-app}") final String clientId,
            @Value("${app.api.client-secret:changeme}") final String clientSecret,
            final PasswordEncoder passwordEncoder) {
        this.clientId = clientId;
        this.clientSecretHash = passwordEncoder.encode(clientSecret);
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<ClientDetails> authenticate(final String id, final String secret) {
        if (id == null || secret == null) {
            return Optional.empty();
        }
        if (!id.equals(this.clientId)) {
            return Optional.empty();
        }
        if (!this.passwordEncoder.matches(secret, this.clientSecretHash)) {
            return Optional.empty();
        }
        return Optional.of(new ClientDetails(id, List.of("API_CLIENT")));
    }
}
