package com.library.loansystem.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorRequest {
    @Size(max = 50)
    @NotBlank
    String name;

    @Size(max = 50)
    @NotBlank
    String lastName;

    @Size(max = 50)
    String nationality;
}
