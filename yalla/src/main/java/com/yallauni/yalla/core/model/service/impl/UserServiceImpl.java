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
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmailAddress(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setMobilePhoneNumber(userDto.getPhoneNumber());

        if (userDto.getUserType() != null) {
            user.setUserType(User.UserType.valueOf(userDto.getUserType().toUpperCase()));
        } else {
            user.setUserType(User.UserType.PASSENGER);
        }

        User saved = userRepository.save(user);
        return UserResponseDTO.fromEntity(saved);
    }

    @Override
    public Optional<UserResponseDTO> findById(Long id) {
        return userRepository.findById(id).map(UserResponseDTO::fromEntity);
    }

    @Override
    public Optional<UserResponseDTO> findByEmail(String email) {
        return userRepository.findByEmailAddress(email).map(UserResponseDTO::fromEntity);
    }

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

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
