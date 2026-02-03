package com.library.loansystem.DTO.Security;

public record AuthResponse(String username,
                           String message,
                           String jwt,
                           boolean status) {
}
