package com.library.loansystem.DTO.Author;

import com.library.loansystem.DTO.Response.AuthorResponse;
import com.library.loansystem.Entities.Author;
import static org.junit.jupiter.api.Assertions.*;

import com.library.loansystem.Mapper.AuthorMapper;
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
        assertEquals(author.getId(), authorResponse.getId());
        assertEquals(author.getName(), authorResponse.getName());
        assertEquals(author.getLastName(), authorResponse.getLastName());
        assertEquals(author.getNationality(), authorResponse.getNationality());
    }
}
