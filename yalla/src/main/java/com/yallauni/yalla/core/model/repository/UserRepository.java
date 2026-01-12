package com.yallauni.yalla.core.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yallauni.yalla.core.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAddress(String emailAddress);
}
