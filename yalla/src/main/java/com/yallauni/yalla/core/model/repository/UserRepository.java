package com.yallauni.yalla.core.model.repository;

// Spring Data JPA base repository (already commented elsewhere)
import org.springframework.data.jpa.repository.JpaRepository;

// User entity (already commented elsewhere)
import com.yallauni.yalla.core.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Find a user by their email address
    Optional<User> findByEmailAddress(String emailAddress);
}
