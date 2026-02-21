package com.library.loansystem.DTO.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserEntityUpdateRequest (@Email String email, @NotBlank @Size(max = 50) String username){
}
