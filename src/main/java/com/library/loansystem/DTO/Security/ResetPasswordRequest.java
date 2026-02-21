package com.library.loansystem.DTO.Security;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(@NotBlank String currentPassword, @NotBlank String newPassword) {
}
