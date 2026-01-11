package com.yallauni.yalla.service;

import com.yallauni.yalla.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User registerUser(User user);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    List<User> findAll();

    User updateUser(Long id, User user);

    void deleteUser(Long id);
}
