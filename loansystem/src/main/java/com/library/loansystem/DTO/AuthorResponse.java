package com.library.loansystem.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthorResponse {
    Long id;
    String name;
    String lastName;
    String nationality;
}
