package com.yallauni.yalla.core.model.service.impl;

// User entity (already commented elsewhere)
import com.yallauni.yalla.core.model.User;
// Repository for user data (already commented elsewhere)
import com.yallauni.yalla.core.model.repository.UserRepository;
// User service interface
import com.yallauni.yalla.core.model.service.UserService;
// DTO for creating user (already commented elsewhere)
import com.yallauni.yalla.dto.user.UserCreateDTO;
// DTO for returning user data (already commented elsewhere)
import com.yallauni.yalla.dto.user.UserResponseDTO;

// Marks this class as a Spring service (already commented elsewhere)
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponseDTO registerUser(UserCreateDTO userDto) {
        // Map DTO to entity, set defaults, and save user
        User user = new User();
        user.setFirstName(userDto.getUsername());
        user.setEmailAddress(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        // Προσθέστε και άλλα πεδία αν χρειάζεται
        if (user.getUserType() == null) {
            user.setUserType(User.UserType.PASSENGER);
        }
        User saved = userRepository.save(user);
        return toDto(saved);
    }

    @Override
    public Optional<UserResponseDTO> findById(Long id) {
        // Find user by id and return as DTO
        return userRepository.findById(id).map(this::toDto);
    }

    @Override
    public Optional<UserResponseDTO> findByEmail(String email) {
        // Find user by email and return as DTO
        return userRepository.findByEmailAddress(email).map(this::toDto);
    }

    @Override
    public List<UserResponseDTO> findAll() {
        // Return all users as a list of DTOs
        return userRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public UserResponseDTO updateUser(Long id, UserCreateDTO userDto) {
        // Update user fields and save
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Update only provided fields
        if (userDto.getUsername() != null) {
            user.setFirstName(userDto.getUsername());
        }
        if (userDto.getEmail() != null) {
            user.setEmailAddress(userDto.getEmail());
        }
        if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
            user.setPassword(userDto.getPassword());
        }

        User updated = userRepository.save(user);
        return toDto(updated);
    }

    @Override
    public void deleteUser(Long id) {
        // Delete user by id
        userRepository.deleteById(id);
    }

    private UserResponseDTO toDto(User user) {
        // Convert User entity to UserResponseDTO
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getUserID());
        dto.setUsername(user.getFirstName());
        dto.setEmail(user.getEmailAddress());
        return dto;
    }
}
