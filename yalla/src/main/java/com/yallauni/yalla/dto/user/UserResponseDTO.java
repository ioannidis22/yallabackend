package com.yallauni.yalla.dto.user;

public class UserResponseDTO {
    private Long id; // Unique ID of the user
    private String username; // Username of the user
    private String email; // Email address of the user
    // Add more fields if needed

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
}