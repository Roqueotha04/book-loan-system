package com.library.loansystem.Services;

import com.library.loansystem.Mapper.BookMapper;
import com.library.loansystem.Repositories.BookRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BookServiceImplTest {

    @Mock
    private  BookMapper bookMapper;
    @Mock
    private  BookRepository bookRepository;
    @Mock
    private  LoanService loanService;
    @Mock
    private  PublisherService publisherService;
    @Mock
    private  AuthorService authorService;

    @InjectMocks
    private BookServiceImpl bookService;
}
