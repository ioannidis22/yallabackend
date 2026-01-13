package com.yallauni.yalla.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request to initiate password reset.
 */
public record PasswordResetRequest(
        @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email) {
}
