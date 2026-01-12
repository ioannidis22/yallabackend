package com.yallauni.yalla.core.model.service;

import java.util.List;
import java.util.Optional;

import com.yallauni.yalla.core.model.User;

public interface UserService {
    User registerUser(User user);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    List<User> findAll();

    User updateUser(Long id, User user);

    void deleteUser(Long id);
}
