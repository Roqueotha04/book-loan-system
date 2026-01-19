package com.library.loansystem.DTO.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(@Email String email, @NotBlank @Size(max = 50) String username, @NotBlank @Size(min = 8, max = 100)String password) {}
