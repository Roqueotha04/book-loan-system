package com.library.loansystem.Services;
import com.library.loansystem.DTO.Request.BookCopyRequest;
import com.library.loansystem.DTO.Response.BookCopyResponse;
import com.library.loansystem.DataProvider;
import com.library.loansystem.Entities.Book;
import com.library.loansystem.Entities.BookCopy;
import com.library.loansystem.Entities.Enums.BookCopyState;
import com.library.loansystem.Mapper.BookCopyMapper;
import com.library.loansystem.Repositories.BookCopyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookCopyServiceImplTest {

    @Mock
    private BookCopyRepository bookCopyRepository;

    @Mock
    private BookService bookService;

    private BookCopyServiceImpl bookCopyService;

    @BeforeEach
    void setUp(){
        BookCopyMapper bookCopyMapper = new BookCopyMapper();
        bookCopyService = new BookCopyServiceImpl(bookCopyRepository, bookCopyMapper, bookService);
    }

    @Test
    public void testSave(){
        BookCopyRequest bookCopyRequest = new BookCopyRequest(2L, BookCopyState.AVAILABLE);
        Book book = DataProvider.bookListMock().get(2);
        when(bookService.getBookOrThrow(2L)).thenReturn(book);

        when(bookCopyRepository.save(any(BookCopy.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BookCopyResponse result = bookCopyService.save(bookCopyRequest);

        assertEquals(book.getName(), result.bookName());
        assertEquals(bookCopyRequest.state(), result.bookCopyState());
        verify(bookCopyRepository).save(any(BookCopy.class));
    }
}
