package com.yallauni.yalla.core.model.service.impl;

import com.yallauni.yalla.core.model.User;
import com.yallauni.yalla.core.model.repository.UserRepository;
import com.yallauni.yalla.core.model.service.UserService;
import com.yallauni.yalla.dto.user.UserCreateDTO;
import com.yallauni.yalla.dto.user.UserResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of UserService.
 * Contains business logic for user management operations.
 */
@Service
public class UserServiceImpl implements UserService {
    // Repository for database operations on User model
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Registers a new user in the system.
     * 
     * @param userDto DTO containing user registration data
     * @return UserResponseDTO with the created user's data
     */
    @Override
    public UserResponseDTO registerUser(UserCreateDTO userDto) {
        // Create new User entity from DTO
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmailAddress(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setMobilePhoneNumber(userDto.getPhoneNumber());

        // Set user type, default to PASSENGER if not provided
        if (userDto.getUserType() != null) {
            user.setUserType(User.UserType.valueOf(userDto.getUserType().toUpperCase()));
        } else {
            user.setUserType(User.UserType.PASSENGER);
        }

        // Save to database and convert to response DTO
        User saved = userRepository.save(user);
        return UserResponseDTO.fromEntity(saved);
    }

    /**
     * Find a user by their unique ID.
     * 
     * @param id the user's ID
     * @return Optional containing UserResponseDTO if found, empty otherwise
     */
    @Override
    public Optional<UserResponseDTO> findById(Long id) {
        return userRepository.findById(id).map(UserResponseDTO::fromEntity);
    }

    /**
     * Find a user by their email address.
     * 
     * @param email the user's email
     * @return Optional containing UserResponseDTO if found, empty otherwise
     */
    @Override
    public Optional<UserResponseDTO> findByEmail(String email) {
        return userRepository.findByEmailAddress(email).map(UserResponseDTO::fromEntity);
    }

    /**
     * Retrieve all users in the system.
     * 
     * @return List of UserResponseDTOs for all users
     */
    @Override
    public List<UserResponseDTO> findAll() {
        return userRepository.findAll().stream().map(UserResponseDTO::fromEntity).toList();
    }

    @Override
    public UserResponseDTO updateUser(Long id, UserCreateDTO userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Update only provided fields
        if (userDto.getFirstName() != null) {
            user.setFirstName(userDto.getFirstName());
        }
        if (userDto.getLastName() != null) {
            user.setLastName(userDto.getLastName());
        }
        if (userDto.getEmail() != null) {
            user.setEmailAddress(userDto.getEmail());
        }
        if (userDto.getPhoneNumber() != null) {
            user.setMobilePhoneNumber(userDto.getPhoneNumber());
        }
        if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
            user.setPassword(userDto.getPassword());
        }

        User updated = userRepository.save(user);
        return UserResponseDTO.fromEntity(updated);
    }

    /**
     * Delete a user by their ID.
     * 
     * @param id the user's ID to delete
     */
    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
