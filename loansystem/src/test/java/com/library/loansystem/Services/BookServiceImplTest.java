package com.library.loansystem.Services;

import com.library.loansystem.DTO.Request.BookRequest;
import com.library.loansystem.DTO.Response.BookResponse;
import com.library.loansystem.DataProvider;
import com.library.loansystem.Entities.Author;
import com.library.loansystem.Entities.Book;
import com.library.loansystem.Entities.Enums.BookGenre;
import com.library.loansystem.Entities.Publisher;
import com.library.loansystem.Exceptions.BusinessException;
import com.library.loansystem.Exceptions.ResourceNotFoundException;
import com.library.loansystem.Mapper.AuthorMapper;
import com.library.loansystem.Mapper.BookMapper;
import com.library.loansystem.Repositories.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.auditing.AuditingHandler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private LoanService loanService;
    @Mock
    private PublisherService publisherService;
    @Mock
    private AuthorService authorService;

    private BookServiceImpl bookService;

    @BeforeEach
    void setUp() {
        AuthorMapper authorMapper = new AuthorMapper();
        BookMapper bookMapper = new BookMapper(authorMapper);

        bookService = new BookServiceImpl(
                bookMapper,
                bookRepository,
                publisherService,
                authorService
        );
    }

    @Test
    public void testFindAll() {
        List<Book> bookList = DataProvider.bookListMock();

        when(bookRepository.findAll()).thenReturn(bookList);

        List<BookResponse> result = bookService.findAll();

        assertEquals(bookList.size(), result.size());
        assertEquals(bookList.get(1).getName(), result.get(1).getName());
        verify(bookRepository).findAll();
    }

    @Test
    public void findById() {
        Book book = DataProvider.bookListMock().get(2);

        when(bookRepository.findById(2L)).thenReturn(Optional.of(book));

        BookResponse result = bookService.findById(2L);

        assertEquals(book.getName(), result.getName());
        verify(bookRepository).findById(2L);
    }

    @Test
    public void findByIsbn_ok(){
        Book book = DataProvider.bookListMock().get(2);

        when(bookRepository.findByIsbn("1234")).thenReturn(Optional.of(book));
        BookResponse result = bookService.findByIsbn("1234");

        assertEquals(book.getName(), result.getName());
        verify(bookRepository).findByIsbn("1234");
    }

    @Test
    public void findByIsbn_notFound(){

        when(bookRepository.findByIsbn("1234")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.findByIsbn("1234"));
        verify(bookRepository).findByIsbn("1234");
    }

    @Test
    public void save (){
        BookRequest book = new BookRequest("The Age of Extremes",BookGenre.NON_FICTION,"8789876298523", 1L, List.of(1L,2L));


        when(bookRepository.save(any(Book.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(publisherService.getPublisherOrThrow(1L))
                .thenReturn(new Publisher(1L, "Publisher 4"));
        when(authorService.getAuthorOrThrow(anyLong()))
                .thenAnswer(iteration -> new Author(1L,"Pepe", "Argento", "Argentinian"));

        BookResponse result = bookService.save(book);

        assertEquals(book.getName(),result.getName());
        verify(bookRepository).save(any(Book.class));
        verify(publisherService).getPublisherOrThrow(1L);
        verify(authorService, atLeastOnce()).getAuthorOrThrow(anyLong());
    }
    
    @Test
    public void testDelete_ok (){
        Book book = new Book("The Age of Extremes", BookGenre.NON_FICTION,"8789876298523",new Publisher(1L, "Publisher"));
        book.setId(2L);
        when(bookRepository.findById(2L))
                .thenReturn(Optional.of(book));

        when(loanService.existsActiveLoanByBookId(book.getId()))
                .thenReturn(false);

        bookService.delete(2L);

        verify(bookRepository).findById(2L);
        verify(loanService).existsActiveLoanByBookId(book.getId());
        verify(bookRepository).delete(any(Book.class));
    }

    @Test
    public void testDelete_BusinessException(){
        Book book = new Book("The Age of Extremes", BookGenre.NON_FICTION,"8789876298523",new Publisher(1L, "Publisher"));
        book.setId(2L);
        when(bookRepository.findById(book.getId()))
                .thenReturn(Optional.of(book));

        when(loanService.existsActiveLoanByBookId(book.getId()))
                .thenReturn(true);

      //The only call to the method should be in the assertThrows
        //bookService.delete(2L);

        assertThrows(BusinessException.class, () -> bookService.delete(book.getId()));

        verify(bookRepository).findById(book.getId());
        verify(loanService).existsActiveLoanByBookId(book.getId());
        verify(bookRepository, never()).delete(any(Book.class));
    }

    @Test
    public void testUpdate (){
        BookRequest bookRequest = new BookRequest("The Age of Extremes",BookGenre.NON_FICTION,"8789876298523", 1L, List.of(1L,2L));
        Book book = new Book("The Age of Extremes 2", BookGenre.NON_FICTION,"8789876298523",new Publisher(1L, "Publisher 2"));
        when(bookRepository.findById(2L))
                .thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class)))
                .thenAnswer(iteration -> iteration.getArgument(0));

        BookResponse result = bookService.update(2L, bookRequest);

        assertNotEquals("The Age of Extremes 2", result.getName());
        assertEquals("The Age of Extremes", result.getName());
        assertEquals(BookGenre.NON_FICTION, result.getGenre());
        assertEquals("8789876298523", result.getIsbn());
        verify(bookRepository).findById(2L);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    public void testChangeStatusFalse(){
        Book book = new Book("The Age of Extremes 2", BookGenre.NON_FICTION,"8789876298523",new Publisher(1L, "Publisher 2"));

        when(bookRepository.findById(2L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        BookResponse result = bookService.changeStatus(2L);
        assertEquals(false, result.getActive());
        verify(bookRepository).findById(2L);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    public void testChangeStatusTrue(){
        Book book = new Book("The Age of Extremes 2", BookGenre.NON_FICTION,"8789876298523",new Publisher(1L, "Publisher 2"));
        book.setActive(false);
        when(bookRepository.findById(2L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BookResponse result = bookService.changeStatus(2L);
        assertEquals(true, result.getActive());
        verify(bookRepository).findById(2L);
        verify(bookRepository).save(any(Book.class));
    }


    @Test
    public void testGetBookOrThrow (){
        Book book = new Book("The Age of Extremes 2", BookGenre.NON_FICTION,"8789876298523",new Publisher(1L, "Publisher 2"));
        when(bookRepository.findById(2L)).thenReturn(Optional.of(book));

        Book result = bookService.getBookOrThrow(2L);

        assertNotNull(result);
        assertEquals(book.getName(),result.getName());
        verify(bookRepository).findById(2L);
    }

    @Test
    public void testGetBookOrThrow_NotFound (){
        when(bookRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, ()->bookService.getBookOrThrow(2L));

        verify(bookRepository).findById(2L);
    }

}
