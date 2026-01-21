package com.yallauni.yalla.dto.admin;

import jakarta.validation.constraints.*;

/**
 * DTO for creating a new admin account.
 * Contains the essential information needed to register an administrator.
 */
public class AdminCreateDTO {
    /** Admin username (required)*/
    @NotBlank
    private String username;
    /** Admin password (required) */
    @NotBlank
    private String password;
    /** Admin email address (required, must be valid email format) */
    @NotBlank
    @Email
    private String email;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
