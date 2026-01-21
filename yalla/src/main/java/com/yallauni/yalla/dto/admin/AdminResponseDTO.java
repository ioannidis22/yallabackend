package com.yallauni.yalla.dto.admin;

/**
 * DTO for returning admin information in API responses.
 * Used to expose only necessary admin fields without sensitive data like
 * password.
 */
public class AdminResponseDTO {
    // Unique identifier for the admin
    private Long id;
    // Admin's username for display
    private String username;
    // Admin's email address
    private String email;

    
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
