package com.library.loansystem.Services;

import com.library.loansystem.Exceptions.BusinessException;
import com.library.loansystem.Exceptions.ResourceNotFoundException;
import com.library.loansystem.Mapper.AuthorMapper;
import com.library.loansystem.DTO.Request.AuthorRequest;
import com.library.loansystem.DTO.Response.AuthorResponse;
import com.library.loansystem.DataProvider;
import com.library.loansystem.Entities.Author;
import com.library.loansystem.Repositories.AuthorRepository;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
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


    private final AuthorMapper authorMapper = new AuthorMapper();


    private AuthorServiceImpl authorService;

    @BeforeEach
    public void setUp() {
        authorService = new AuthorServiceImpl(authorRepository, authorMapper);
    }


    @Test
    public void testFindAll (){
        //Given
        Author author = DataProvider.authorListMock().get(0);
        //When
        when(authorRepository.findAll()).thenReturn(List.of(author));
        List <AuthorResponse> result = authorService.findAll();


        //Then
        assertFalse (result.isEmpty());
        assertEquals("Mariano", result.get(0).name());
        verify(this.authorRepository).findAll();
    }

    @Test
    public void testFindById (){
        Author author =  DataProvider.authorListMock().get(1);

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        AuthorResponse result = authorService.findById(1L);

        assertNotNull(result);
        assertEquals(author.getId(), result.id());
        assertEquals(author.getName(), result.name());
        assertEquals(author.getLastName(), result.lastName());
        assertEquals(author.getNationality(), result.nationality());
    }

    @Test
    public void testSave (){
        AuthorRequest authorRequest = new AuthorRequest("Paulo", "Cohelo", "Brazilian");
        when(authorRepository.save(any(Author.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AuthorResponse result = authorService.save(authorRequest);

        assertEquals(authorRequest.getName(), result.name());
        verify(authorRepository).save(any(Author.class));
    }

    @Test
    public void testDelete_ok (){
        when(authorRepository.findById(1L))
                .thenReturn(Optional.of(new Author()));

        authorService.deleteById(1L);

        verify(authorRepository).findById(1L);
        verify(authorRepository).delete(any(Author.class));
    }

    @Test
    public void testDelete_businessException (){
        when(authorRepository.findById(1L)).thenReturn(Optional.of(new Author()));
        when(authorRepository.existsBookByAuthorId(1L)).thenReturn(true);
        assertThrows(BusinessException.class, () -> authorService.deleteById(1L));


        verify(authorRepository).findById(1L);
        verify(authorRepository, never()).delete(any(Author.class));
    }

    @Test
    public void testUpdate(){
        Author author = new Author("Paulo", "Cohelo", "Brazilian");
        AuthorRequest authorRequest = new AuthorRequest("Paulo 2", "Cohelo", "Brazilian");
        when(authorRepository.findById(1L))
                .thenReturn(Optional.of(author));
        when(authorRepository.save(any(Author.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AuthorResponse result = authorService.update(1L, authorRequest);

        assertEquals(result.name(), authorRequest.getName());
        verify(authorRepository).findById(1L);
        verify(authorRepository).save(any(Author.class));
    }

    @Test
    public void testGetAuthorOrThrow_ok() {
        Author author = DataProvider.authorListMock().get(1);

        when(authorRepository.findById(1L))
                .thenReturn(Optional.of(author));

        Author result = authorService.getAuthorOrThrow(1L);

        assertNotNull(result);
        assertSame(author, result);
        verify(authorRepository).findById(1L);
    }

    @Test
    public void testGetAuthorOrThrow_notFound() {
        when(authorRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                authorService.getAuthorOrThrow(1L)
        );

        verify(authorRepository).findById(1L);
    }
}
