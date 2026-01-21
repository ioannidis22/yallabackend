package com.yallauni.yalla.core.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.yallauni.yalla.core.model.User;

import java.util.Optional;

/**
 * Repository interface for User model database operations.
 * Extends JpaRepository to provide CRUD operations and custom queries.
 * Used by UserService and security components for user data access.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    // Find a user by their email address
    Optional<User> findByEmailAddress(String emailAddress);

    // Count users by type (DRIVER, PASSENGER, ADMIN)
    long countByUserType(User.UserType userType);

    // Find all banned users
    java.util.List<User> findByBannedTrue();

    // Count banned users
    long countByBannedTrue();
}
