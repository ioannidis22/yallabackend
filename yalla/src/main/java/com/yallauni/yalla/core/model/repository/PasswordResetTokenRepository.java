package com.yallauni.yalla.core.model.repository;

// Password reset token entity
import com.yallauni.yalla.core.model.PasswordResetToken;
// User entity (already commented elsewhere)
import com.yallauni.yalla.core.model.User;
// Spring Data JPA base repository (already commented elsewhere)
import org.springframework.data.jpa.repository.JpaRepository;
// Annotation to mark this as a Spring repository
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    // Find a password reset token by its value
    Optional<PasswordResetToken> findByToken(String token);

    // Delete all tokens for a specific user
    void deleteByUser(User user);
}
