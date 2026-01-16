package com.yallauni.yalla.controller;

import com.yallauni.yalla.core.model.User;
import com.yallauni.yalla.core.model.repository.UserRepository;
import com.yallauni.yalla.core.model.service.UserService;
import com.yallauni.yalla.dto.user.UserCreateDTO;
import com.yallauni.yalla.dto.user.UserResponseDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody UserCreateDTO userDto) {
        UserResponseDTO response = userService.registerUser(userDto);
        return ResponseEntity.ok(response);
    }

    // Get current user's own profile
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDTO> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        Optional<UserResponseDTO> userDto = userService.findByEmail(userDetails.getUsername());
        return userDto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get user by ID - only own profile or admin
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getUserById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByEmailAddress(userDetails.getUsername()).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        boolean isAdmin = currentUser.getUserType() == User.UserType.ADMIN;
        boolean isOwnProfile = currentUser.getUserID().equals(id);

        if (!isOwnProfile && !isAdmin) {
            return ResponseEntity.status(403).body("Access denied: You can only view your own profile");
        }

        Optional<UserResponseDTO> userDto = userService.findById(id);
        return userDto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get all users - admin only
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponseDTO> getAllUsers() {
        return userService.findAll();
    }

    // Update user - only own profile or admin
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserCreateDTO userDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByEmailAddress(userDetails.getUsername()).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        boolean isAdmin = currentUser.getUserType() == User.UserType.ADMIN;
        boolean isOwnProfile = currentUser.getUserID().equals(id);

        if (!isOwnProfile && !isAdmin) {
            return ResponseEntity.status(403).body("Access denied: You can only update your own profile");
        }

        UserResponseDTO updated = userService.updateUser(id, userDto);
        return ResponseEntity.ok(updated);
    }

    // Delete user - only own profile or admin
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByEmailAddress(userDetails.getUsername()).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        boolean isAdmin = currentUser.getUserType() == User.UserType.ADMIN;
        boolean isOwnProfile = currentUser.getUserID().equals(id);

        if (!isOwnProfile && !isAdmin) {
            return ResponseEntity.status(403).body("Access denied: You can only delete your own profile");
        }

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
