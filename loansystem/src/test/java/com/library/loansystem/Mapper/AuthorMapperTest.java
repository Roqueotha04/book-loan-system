package com.library.loansystem.Mapper;

import com.library.loansystem.DTO.Response.AuthorResponse;
import com.library.loansystem.Entities.Author;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class AuthorMapperTest {

    AuthorMapper authorMapper = new AuthorMapper();

    @Test
    public void testToResponse(){
        //Given
        Author author = new Author("Jhon Ronald Reuel", "Tolkien", "British");

        //When
        AuthorResponse authorResponse = authorMapper.toResponse(author);

        //Then
        assertEquals(author.getId(), authorResponse.id());
        assertEquals(author.getName(), authorResponse.name());
        assertEquals(author.getLastName(), authorResponse.lastName());
        assertEquals(author.getNationality(), authorResponse.nationality());
    }
}
