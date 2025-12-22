package com.library.loansystem.DTO.Author;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorRequest {
    String name;
    String lastName;
    String nationality;
}
