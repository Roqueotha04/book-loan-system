package com.library.loansystem.DTO.Author;

import com.library.loansystem.Entities.Author;

public class AuthorMapper {

    public AuthorResponse toResponse(Author author) {
        return new AuthorResponse(
                author.getId(),
                author.getName(),
                author.getLastName(),
                author.getNationality()
        );
    }

}
