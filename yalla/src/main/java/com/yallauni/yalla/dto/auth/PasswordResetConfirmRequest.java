package com.yallauni.yalla.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request to confirm password reset with token and new password.
 */
public record PasswordResetConfirmRequest(
        @NotBlank(message = "Reset code is required") String code,

        @NotBlank(message = "New password is required") @Size(min = 6, message = "Password must be at least 6 characters") String newPassword) {
}
