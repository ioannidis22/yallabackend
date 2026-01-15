package com.yallauni.yalla.controller;



// Service for user logic
import com.yallauni.yalla.core.model.service.UserService;
// DTO for creating user
import com.yallauni.yalla.dto.user.UserCreateDTO;
// DTO for returning user data
import com.yallauni.yalla.dto.user.UserResponseDTO;

// Used for HTTP responses (already commented elsewhere)
import org.springframework.http.ResponseEntity;
// Spring REST controller annotations (already commented elsewhere)
import org.springframework.web.bind.annotation.*;

// Annotation for method-level security (already commented elsewhere)
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users") // All endpoints in this controller start with /api/users
public class UserController {
    private final UserService userService;

    
    public UserController(UserService userService) {
        // Inject the user service
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody UserCreateDTO userDto) {
        // Register a new user and return the response DTO
        UserResponseDTO response = userService.registerUser(userDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        // Find user by id and return as DTO, or 404 if not found
        Optional<UserResponseDTO> userDto = userService.findById(id);
        return userDto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<UserResponseDTO> getAllUsers() {
        // Return all users as a list of DTOs
        return userService.findAll();
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody UserCreateDTO userDto) {
        // Update an existing user with new data
        UserResponseDTO updated = userService.updateUser(id, userDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        // Delete user by id
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
