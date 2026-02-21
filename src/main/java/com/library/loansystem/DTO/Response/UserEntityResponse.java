package com.library.loansystem.DTO.Response;

import com.library.loansystem.Entities.Role;

import java.util.Set;

public record UserEntityResponse(Long id, String email, String username, Boolean active, Set<Role> roles) {
}
