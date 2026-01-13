package com.yallauni.yalla.controller;

import com.yallauni.yalla.core.model.User;
import com.yallauni.yalla.core.model.repository.UserRepository;
import com.yallauni.yalla.core.security.JwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        try {
            String email = loginRequest.get("email");
            String password = loginRequest.get("password");
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));
            User user = userRepository.findByEmailAddress(email).orElse(null);
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
}
