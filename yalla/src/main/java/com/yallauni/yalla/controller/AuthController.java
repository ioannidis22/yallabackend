package com.yallauni.yalla.controller;

import com.yallauni.yalla.core.model.User;
import com.yallauni.yalla.core.model.repository.UserRepository;
import com.yallauni.yalla.core.security.JwtService;
import com.yallauni.yalla.core.service.PasswordResetService;
import com.yallauni.yalla.dto.auth.PasswordResetConfirmRequest;
import com.yallauni.yalla.dto.auth.PasswordResetRequest;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for authentication operations.
 * Handles user login, registration, and password reset features.
 * Issues JWT tokens for authenticated users.
 */
@RestController
@RequestMapping("/api/auth") // All endpoints in this controller start with /api/auth.
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        // Authenticate user and return JWT if succesful.
        try {
            String email = loginRequest.get("email");
            String password = loginRequest.get("password");
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));
            User user = userRepository.findByEmailAddress(email).orElse(null);

            // Check if user is banned
            if (user != null && user.isBanned()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "account_banned");
                error.put("message", "Your account has been banned");
                if (user.getBanReason() != null && !user.getBanReason().isBlank()) {
                    error.put("reason", user.getBanReason());
                }
                return ResponseEntity.status(403).body(error);
            }

            // Issue JWT with user's role
            String role = user != null && user.getUserType() != null ? user.getUserType().name() : "USER";
            String token = jwtService.issue(email, List.of(role));
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", user);
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "invalid_credentials");
            error.put("message", "Invalid email or password");
            return ResponseEntity.status(401).body(error);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        // Register a new user and return JWT
        if (userRepository.findByEmailAddress(user.getEmailAddress()).isPresent()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "email_in_use");
            error.put("message", "Email already in use");
            return ResponseEntity.badRequest().body(error);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        // Issue JWT with user's role
        String role = user.getUserType() != null ? user.getUserType().name() : "USER";
        String token = jwtService.issue(user.getEmailAddress(), List.of(role));
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", user);
        return ResponseEntity.ok(response);
    }

    /**
     * Request a password reset. Sends a 6-digit code to the user's email.
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody @Valid PasswordResetRequest request) {
        // Request a password reset (sends code to email)
        passwordResetService.requestPasswordReset(request.email());
        // Always return successful message.
        Map<String, String> response = new HashMap<>();
        response.put("message", "If the email exists, a reset code has been sent");
        return ResponseEntity.ok(response);
    }

    // Reset password using the 6-digit code.
     
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid PasswordResetConfirmRequest request) {
        // Reset password using the code
        boolean success = passwordResetService.resetPassword(request.code(), request.newPassword());
        if (success) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Password reset successful");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "invalid_code");
            error.put("message", "Invalid or expired reset code");
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Validate a reset code.
    @PostMapping("/validate-reset-code")
    public ResponseEntity<?> validateResetCode(@RequestBody Map<String, String> request) {
        // Check if the reset code is valid
        String code = request.get("code");
        boolean valid = passwordResetService.validateToken(code).isPresent();
        Map<String, Object> response = new HashMap<>();
        response.put("valid", valid);
        return ResponseEntity.ok(response);
    }
}
