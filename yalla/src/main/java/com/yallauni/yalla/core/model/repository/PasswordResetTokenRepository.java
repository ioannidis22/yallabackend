package com.yallauni.yalla.core.model.repository;

import com.yallauni.yalla.core.model.PasswordResetToken;
import com.yallauni.yalla.core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for PasswordResetToken database operations.
 * Extends JpaRepository to provide CRUD operations and custom queries.
 * Also used by PasswordResetService for token management.
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    // Find a password reset token by its value
    Optional<PasswordResetToken> findByToken(String token);

    // Delete all tokens for a specific user
    void deleteByUser(User user);
}
