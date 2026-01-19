package com.yallauni.yalla.core.model.repository;

// Spring Data JPA base repository (already commented elsewhere)
import org.springframework.data.jpa.repository.JpaRepository;

// User entity (already commented elsewhere)
import com.yallauni.yalla.core.model.User;

import java.util.Optional;

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
