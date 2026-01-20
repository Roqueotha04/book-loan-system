package com.library.loansystem.DTO.Response;

public record UserResponse(Long id, String email, String username, Boolean active) {
}
