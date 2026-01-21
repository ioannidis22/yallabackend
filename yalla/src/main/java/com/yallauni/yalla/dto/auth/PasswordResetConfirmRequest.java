package com.yallauni.yalla.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request to confirm password reset with token and new password.
 * Contains the reset code received via email and the new password.
 */
public record PasswordResetConfirmRequest(
                @NotBlank(message = "Reset code is required") // The code sent to the user for password reset
                String code,

                @NotBlank(message = "New password is required") // The new password to set
                @Size(min = 6, message = "Password must be at least 6 characters") String newPassword) {
}
