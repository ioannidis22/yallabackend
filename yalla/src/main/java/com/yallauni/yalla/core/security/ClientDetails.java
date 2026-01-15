// Immutable details of an API client (record, student-style comment)
package com.yallauni.yalla.core.security;

import java.util.Collection; // list of roles

/**
 * Immutable details of an API client.
 */
public record ClientDetails(
        String id,
        Collection<String> roles) {
    public ClientDetails {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be blank");
        }
        if (roles == null) {
            throw new NullPointerException("roles must not be null");
        }
    }
}
