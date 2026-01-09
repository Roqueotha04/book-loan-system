package com.library.loansystem.Services;

import com.library.loansystem.DTO.Response.BookResponse;
import com.library.loansystem.DataProvider;
import com.library.loansystem.Entities.Book;
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
import java.util.List;

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
    void setUp(){
        AuthorMapper authorMapper = new AuthorMapper();
        BookMapper bookMapper = new BookMapper(authorMapper);

        bookService =  new BookServiceImpl(
            bookMapper,
            bookRepository,
            loanService,
            publisherService,
            authorService
        );
    }

    @Test
    public void testFindAll(){
        List<Book> bookList = DataProvider.bookListMock();

        when (bookRepository.findAll()).thenReturn(bookList);

        List<BookResponse> result = bookService.findAll();

        assertEquals(bookList.size(), result.size());
        assertEquals(bookList.get(1).getName(), result.get(1).getName());
        verify(bookRepository).findAll();
    }
}
