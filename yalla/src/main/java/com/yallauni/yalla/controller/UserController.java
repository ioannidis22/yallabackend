package com.yallauni.yalla.controller;


import com.yallauni.yalla.core.model.User;
import com.yallauni.yalla.core.model.service.UserService;
import com.yallauni.yalla.dto.user.UserCreateDTO;
import com.yallauni.yalla.dto.user.UserResponseDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody UserCreateDTO userDto) {
        // Map DTO to entity
        User user = new User();
        user.setFirstName(userDto.getUsername()); // Αντίστοιχο mapping, προσαρμόστε αν χρειάζεται
        user.setEmailAddress(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        // Προσθέστε και άλλα πεδία αν χρειάζεται
        User saved = userService.registerUser(user);
        // Map entity to response DTO
        UserResponseDTO response = new UserResponseDTO();
        response.setId(saved.getUserID());
        response.setUsername(saved.getFirstName()); // Αντίστοιχο mapping
        response.setEmail(saved.getEmailAddress());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            User u = user.get();
            UserResponseDTO dto = new UserResponseDTO();
            dto.setId(u.getUserID());
            dto.setUsername(u.getFirstName());
            dto.setEmail(u.getEmailAddress());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userService.findAll();
        return users.stream().map(u -> {
            UserResponseDTO dto = new UserResponseDTO();
            dto.setId(u.getUserID());
            dto.setUsername(u.getFirstName());
            dto.setEmail(u.getEmailAddress());
            return dto;
        }).toList();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody UserCreateDTO userDto) {
        User user = new User();
        user.setUserID(id);
        user.setFirstName(userDto.getUsername());
        user.setEmailAddress(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        User updated = userService.updateUser(id, user);
        UserResponseDTO response = new UserResponseDTO();
        response.setId(updated.getUserID());
        response.setUsername(updated.getFirstName());
        response.setEmail(updated.getEmailAddress());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
