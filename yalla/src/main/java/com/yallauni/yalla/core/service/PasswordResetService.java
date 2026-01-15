// Service for handling password reset operations (student-style comment)
package com.yallauni.yalla.core.service;

import com.yallauni.yalla.core.model.PasswordResetToken;
import com.yallauni.yalla.core.model.User;
import com.yallauni.yalla.core.model.repository.PasswordResetTokenRepository;
import com.yallauni.yalla.core.model.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * Service for handling password reset operations.
 */
@Service
public class PasswordResetService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordResetService.class);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final long tokenValidityMinutes;

    public PasswordResetService(
            final UserRepository userRepository,
            final PasswordResetTokenRepository tokenRepository,
            final PasswordEncoder passwordEncoder,
            final EmailService emailService,
            @Value("${app.password-reset.token-validity-minutes:30}") final long tokenValidityMinutes) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.tokenValidityMinutes = tokenValidityMinutes;
    }

    /**
     * Initiates a password reset request.
     * Generates a token and sends it to the user's email.
     * 
     * @param email the user's email address
     * @return true if email was sent (or would be sent), false otherwise
     */
    @Transactional
    public boolean requestPasswordReset(final String email) {
        if (email == null || email.isBlank()) {
            return false;
        }

        final Optional<User> userOpt = userRepository.findByEmailAddress(email.trim().toLowerCase());
        if (userOpt.isEmpty()) {
            // Don't reveal if email exists - return true anyway for security
            LOGGER.info("Password reset requested for non-existent email: {}", email);
            return true;
        }

        final User user = userOpt.get();

        // Delete any existing tokens for this user
        tokenRepository.deleteByUser(user);

        // Generate a 6-digit numeric code (easy to type on mobile)
        final String resetCode = generateResetCode();

        // Create token with expiration
        final Instant expiresAt = Instant.now().plus(tokenValidityMinutes, ChronoUnit.MINUTES);
        final PasswordResetToken token = new PasswordResetToken(resetCode, user, expiresAt);
        tokenRepository.save(token);

        // Send email with reset code
        emailService.sendPasswordResetEmail(user.getEmailAddress(), user.getFirstName(), resetCode);

        LOGGER.info("Password reset token generated for user: {}", email);
        return true;
    }

    /**
     * Validates a reset token.
     * 
     * @param token the reset token/code
     * @return the associated user if valid, empty otherwise
     */
    public Optional<User> validateToken(final String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        final Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token.trim());
        if (tokenOpt.isEmpty()) {
            return Optional.empty();
        }

        final PasswordResetToken resetToken = tokenOpt.get();
        if (!resetToken.isValid()) {
            return Optional.empty();
        }

        return Optional.of(resetToken.getUser());
    }

    /**
     * Resets the user's password using a valid token.
     * 
     * @param token       the reset token/code
     * @param newPassword the new password
     * @return true if password was reset successfully
     */
    @Transactional
    public boolean resetPassword(final String token, final String newPassword) {
        if (token == null || token.isBlank()) {
            return false;
        }
        if (newPassword == null || newPassword.length() < 6) {
            return false;
        }

        final Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token.trim());
        if (tokenOpt.isEmpty()) {
            LOGGER.warn("Password reset attempted with invalid token");
            return false;
        }

        final PasswordResetToken resetToken = tokenOpt.get();
        if (!resetToken.isValid()) {
            LOGGER.warn("Password reset attempted with expired/used token");
            return false;
        }

        // Update password
        final User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Mark token as used
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        LOGGER.info("Password reset successful for user: {}", user.getEmailAddress());
        return true;
    }

    /**
     * Generates a 6-digit numeric reset code.
     */
    private String generateResetCode() {
        int code = SECURE_RANDOM.nextInt(900000) + 100000; // 100000-999999
        return String.valueOf(code);
    }
}
