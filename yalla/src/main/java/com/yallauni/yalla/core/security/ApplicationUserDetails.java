package com.yallauni.yalla.core.security;

import com.yallauni.yalla.core.model.User;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Implementation of Spring's {@link UserDetails} for representing a
 * user at runtime.
 */
public final class ApplicationUserDetails implements UserDetails {

    private final Long userId;
    private final String emailAddress;
    private final String passwordHash;
    private final User.UserType userType;

    public ApplicationUserDetails(final Long userId,
            final String emailAddress,
            final String passwordHash,
            final User.UserType userType) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("userId must be positive");
        }
        if (emailAddress == null || emailAddress.isBlank()) {
            throw new IllegalArgumentException("emailAddress must not be blank");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("passwordHash must not be blank");
        }
        if (userType == null) {
            throw new NullPointerException("userType must not be null");
        }

        this.userId = userId;
        this.emailAddress = emailAddress;
        this.passwordHash = passwordHash;
        this.userType = userType;
    }

    public Long getUserId() {
        return this.userId;
    }

    public User.UserType getUserType() {
        return this.userType;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        final String role = "ROLE_" + this.userType.name();
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return this.passwordHash;
    }

    @Override
    public String getUsername() {
        return this.emailAddress;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
