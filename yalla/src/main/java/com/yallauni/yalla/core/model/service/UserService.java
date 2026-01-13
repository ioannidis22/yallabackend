package com.yallauni.yalla.core.model.service;

import java.util.List;
import java.util.Optional;

import com.yallauni.yalla.dto.user.UserCreateDTO;
import com.yallauni.yalla.dto.user.UserResponseDTO;

public interface UserService {
    UserResponseDTO registerUser(UserCreateDTO userDto);

    Optional<UserResponseDTO> findById(Long id);

    Optional<UserResponseDTO> findByEmail(String email);

    List<UserResponseDTO> findAll();

    UserResponseDTO updateUser(Long id, UserCreateDTO userDto);

    void deleteUser(Long id);
}
