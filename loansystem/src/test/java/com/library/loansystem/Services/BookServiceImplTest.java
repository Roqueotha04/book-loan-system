package com.library.loansystem.Services;

import com.library.loansystem.DTO.Request.BookRequest;
import com.library.loansystem.DTO.Response.BookResponse;
import com.library.loansystem.DataProvider;
import com.library.loansystem.Entities.Author;
import com.library.loansystem.Entities.Book;
import com.library.loansystem.Entities.Enums.BookCopyState;
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

        when(bookRepository.hasCopies(book.getId()))
                .thenReturn(false);

        bookService.delete(2L);

        verify(bookRepository).findById(2L);
        verify(bookRepository).hasCopies(book.getId());
        verify(bookRepository).delete(any(Book.class));
    }

    @Test
    public void testDelete_BusinessException(){
        Book book = new Book("The Age of Extremes", BookGenre.NON_FICTION,"8789876298523",new Publisher(1L, "Publisher"));
        book.setId(2L);
        when(bookRepository.findById(book.getId()))
                .thenReturn(Optional.of(book));

        when(bookRepository.hasCopies(book.getId()))
                .thenReturn(true);

        assertThrows(BusinessException.class, () -> bookService.delete(book.getId()));

        verify(bookRepository).findById(book.getId());
        verify(bookRepository).hasCopies(book.getId());
        verify(bookRepository, never()).delete(any(Book.class));
    }

    @Test
    public void testUpdate (){
        BookRequest bookRequest = new BookRequest("The Age of Extremes", BookGenre.NON_FICTION, "8789876298523", 1L, List.of(1L, 2L));

        Publisher publisher = new Publisher(1L, "Publisher 1");
        Author author = new Author(1L, "Author", "Test", "Nationality");
        Book book = new Book("The Age of Extremes 2", BookGenre.NON_FICTION, "8789876298523", new Publisher(2L, "Publisher 2"));

        when(bookRepository.findById(2L)).thenReturn(Optional.of(book));
        when(publisherService.getPublisherOrThrow(1L)).thenReturn(publisher);
        when(authorService.getAuthorOrThrow(anyLong())).thenReturn(author);
        when(bookRepository.save(any(Book.class))).thenAnswer(iteration -> iteration.getArgument(0));

        BookResponse result = bookService.update(2L, bookRequest);

        assertNotEquals("The Age of Extremes 2", result.getName());
        assertEquals("The Age of Extremes", result.getName());
        assertEquals(BookGenre.NON_FICTION, result.getGenre());
        assertEquals("8789876298523", result.getIsbn());
        assertNotEquals(2L, result.getPublisher().getId());
        assertEquals(2, result.getAuthors().size());

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
    public void testFindByNameContaining(){
        List<Book> bookList = DataProvider.bookListMock();
        String name= "The Lord of the Rings";
        when(bookRepository.findByNameContainingIgnoreCase(name)).thenReturn(bookList);

        List<BookResponse> result = bookService.findByNameContaining(name);
        assertEquals(bookList.size(), result.size());
        verify(bookRepository).findByNameContainingIgnoreCase(name);
    }

    @Test
    public void testFindByGenre(){
        List<Book> bookList = DataProvider.bookListMock();
        BookGenre genre = BookGenre.NON_FICTION;
        when(bookRepository.findByGenre(genre)). thenReturn(bookList);
        List<BookResponse> result = bookService.findByGenre(genre);

        assertEquals(bookList.size(), result.size());
        verify(bookRepository).findByGenre(genre);
    }

    @Test
    public void testFindByAuthor_ok(){
        List<Book> bookList = DataProvider.bookListMock();
        Author author = DataProvider.authorListMock().get(1);
        author.setId(2L);

        when(authorService.getAuthorOrThrow(author.getId())).thenReturn(author);
        when(bookRepository.findByAuthorXBooks_Author_Id(author.getId())).thenReturn(bookList);

        List<BookResponse> result = bookService.findByAuthor(author.getId());

        assertEquals(bookList.size(), result.size());
        verify(authorService).getAuthorOrThrow(author.getId());
        verify(bookRepository).findByAuthorXBooks_Author_Id(author.getId());
    }

    @Test
    public void testFindByAuthor_ResourceNotFound(){
       Long id= 2L;

        when(authorService.getAuthorOrThrow(2L)).thenThrow(new ResourceNotFoundException("Author not found"));

        assertThrows(ResourceNotFoundException.class, ()-> bookService.findByAuthor(2L));

        verify(authorService).getAuthorOrThrow(2L);
        verify(bookRepository, never()).findByAuthorXBooks_Author_Id(2L);
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
