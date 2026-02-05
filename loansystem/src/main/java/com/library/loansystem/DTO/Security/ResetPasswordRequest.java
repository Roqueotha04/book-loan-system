package com.library.loansystem.DTO.Security;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(@NotBlank Long userId, @NotBlank String currentPassword, @NotBlank String newPassword) {
}
