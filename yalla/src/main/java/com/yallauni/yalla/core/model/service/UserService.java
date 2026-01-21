package com.yallauni.yalla.core.model.service;

import java.util.List;
import java.util.Optional;
import com.yallauni.yalla.dto.user.UserCreateDTO;
import com.yallauni.yalla.dto.user.UserResponseDTO;

/**
 * Service interface for user-related operations.
 * Defines business logic for user registration and management.
 * Implemented by UserServiceImpl.
 */
public interface UserService {
    // Register a new user
    UserResponseDTO registerUser(UserCreateDTO userDto);

    // Find user by id
    Optional<UserResponseDTO> findById(Long id);

    // Find user by email
    Optional<UserResponseDTO> findByEmail(String email);

    // Return all users
    List<UserResponseDTO> findAll();

    // Update user fields
    UserResponseDTO updateUser(Long id, UserCreateDTO userDto);

    // Delete user by id
    void deleteUser(Long id);
}
