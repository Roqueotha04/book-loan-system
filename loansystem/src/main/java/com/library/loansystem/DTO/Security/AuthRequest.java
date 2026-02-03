package com.library.loansystem.DTO.Security;

import jakarta.validation.constraints.NotBlank;

public record AuthRequest(@NotBlank String username,
                          @NotBlank String password) {
}
