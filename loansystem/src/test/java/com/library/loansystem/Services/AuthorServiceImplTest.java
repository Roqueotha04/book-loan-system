package com.library.loansystem.Services;

import com.library.loansystem.DTO.Author.AuthorMapper;
import com.library.loansystem.DTO.Author.AuthorResponse;
import com.library.loansystem.DataProvider;
import com.library.loansystem.Entities.Author;
import com.library.loansystem.Repositories.AuthorRepository;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AuthorServiceImplTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private AuthorMapper authorMapper;

    @InjectMocks
    private AuthorServiceImpl authorService;

    @Test
    public void testFindAll (){
        //Given
        Author author = DataProvider.authorListMock().get(0);
        AuthorResponse authorResponse = DataProvider.authorResponseListMock().get(0);

        //When
        when(authorRepository.findAll()).thenReturn(List.of(author));
        when(authorMapper.toResponse(author)).thenReturn(authorResponse);
        List <AuthorResponse> result = authorService.findAll(); //returns authorResponse because of the service.


        //Then
        assertFalse (result.isEmpty());
        assertEquals("Mariano", result.get(0).getName());
        verify(this.authorRepository).findAll();
        verify(authorMapper).toResponse(author);
    }

    @Test
    public void testFindById (){
        Author author =  DataProvider.authorListMock().get(1);
        AuthorResponse authorResponse = DataProvider.authorResponseListMock().get(1);

        when(authorRepository.findById(1L)).thenReturn(Optional.ofNullable(author));
        assertNotNull(author);
        when(authorMapper.toResponse(author)).thenReturn(authorResponse);
        AuthorResponse result = authorService.findById(1L);

        assertNotNull(result);
        assertEquals(authorResponse.getId(), result.getId());
        assertEquals(authorResponse.getName(), result.getName());
        assertEquals(authorResponse.getLastName(), result.getLastName());
        assertEquals(authorResponse.getNationality(), result.getNationality());
    }
}
