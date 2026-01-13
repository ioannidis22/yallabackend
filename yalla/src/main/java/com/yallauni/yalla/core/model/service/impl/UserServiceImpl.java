package com.yallauni.yalla.core.model.service.impl;

import com.yallauni.yalla.core.model.User;
import com.yallauni.yalla.core.model.repository.UserRepository;
import com.yallauni.yalla.core.model.service.UserService;
import com.yallauni.yalla.dto.user.UserCreateDTO;
import com.yallauni.yalla.dto.user.UserResponseDTO;

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
        return userRepository.findById(id).map(this::toDto);
    }

    @Override
    public Optional<UserResponseDTO> findByEmail(String email) {
        return userRepository.findByEmailAddress(email).map(this::toDto);
    }

    @Override
    public List<UserResponseDTO> findAll() {
        return userRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public UserResponseDTO updateUser(Long id, UserCreateDTO userDto) {
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
        userRepository.deleteById(id);
    }

    private UserResponseDTO toDto(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getUserID());
        dto.setUsername(user.getFirstName());
        dto.setEmail(user.getEmailAddress());
        return dto;
    }
}
