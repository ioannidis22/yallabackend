package com.yallauni.yalla.dto.user;

import jakarta.validation.constraints.*;

public class UserCreateDTO {
    @NotBlank // The username for the new user
    @Size(min = 3, max = 50) // Username must be between 3 and 50 characters
    private String username;

    @NotBlank // The user's email address
    @Email // Must be a valid email format
    private String email;

    @NotBlank // The user's password
    @Size(min = 8, max = 100) // Password must be between 8 and 100 characters
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}