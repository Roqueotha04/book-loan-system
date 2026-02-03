package com.library.loansystem.DTO.Security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthRegisterRequest(@NotBlank String username,
                                  @NotBlank @Email String email,
                                  @NotBlank String password
                              ){
}
