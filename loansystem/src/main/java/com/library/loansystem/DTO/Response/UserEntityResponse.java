package com.library.loansystem.DTO.Response;

public record UserEntityResponse(Long id, String email, String username, Boolean active) {
}
