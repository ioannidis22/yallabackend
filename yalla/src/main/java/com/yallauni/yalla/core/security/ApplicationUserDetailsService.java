package com.yallauni.yalla.core.security;

import com.yallauni.yalla.core.model.User;
import com.yallauni.yalla.core.model.repository.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementation of Spring's {@link UserDetailsService} for providing
 * application users.
 */
@Service
public class ApplicationUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public ApplicationUserDetailsService(final UserRepository userRepository) {
        if (userRepository == null) {
            throw new NullPointerException("userRepository must not be null");
        }
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username must not be blank");
        }

        final User user = this.userRepository
                .findByEmailAddress(username)
                .orElse(null);

        if (user == null) {
            throw new UsernameNotFoundException("User with email " + username + " not found");
        }

        return new ApplicationUserDetails(
                user.getUserID(),
                user.getEmailAddress(),
                user.getPassword(),
                user.getUserType());
    }
}
