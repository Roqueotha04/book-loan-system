package com.library.loansystem.Mapper;

import com.library.loansystem.DTO.Response.AuthorResponse;
import com.library.loansystem.Entities.Author;
import org.springframework.stereotype.Component;

@Component
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
