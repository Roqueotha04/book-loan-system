package com.library.loansystem.Services;
import com.library.loansystem.DTO.Request.BookCopyRequest;
import com.library.loansystem.DTO.Response.BookCopyResponse;
import com.library.loansystem.DataProvider;
import com.library.loansystem.Entities.Book;
import com.library.loansystem.Entities.BookCopy;
import com.library.loansystem.Entities.Enums.BookCopyState;
import com.library.loansystem.Exceptions.BusinessException;
import com.library.loansystem.Exceptions.ResourceNotFoundException;
import com.library.loansystem.Mapper.BookCopyMapper;
import com.library.loansystem.Repositories.BookCopyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.xml.crypto.Data;
import java.util.List;
import java.util.Optional;

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
    public void testFindAllByBook_NullStatus() {
        List<BookCopy> bookCopyList = DataProvider.bookCopyListMock();
        String isbn = "123456789";

        when(bookCopyRepository.findByBookIsbn(isbn)).thenReturn(bookCopyList);

        List<BookCopyResponse> result = bookCopyService.findAllByBook(isbn, null);

        assertEquals(bookCopyList.size(), result.size());
        verify(bookCopyRepository).findByBookIsbn(isbn);
        verify(bookCopyRepository, never()).findByBookIsbnAndState(anyString(), any());
    }

    @Test
    public void testFindAllByBook_WithStatus() {
        List<BookCopy> bookCopyList = DataProvider.bookCopyListMock();
        String isbn = "123456789";
        BookCopyState state = BookCopyState.AVAILABLE;

        when(bookCopyRepository.findByBookIsbnAndState(isbn, state)).thenReturn(bookCopyList);

        List<BookCopyResponse> result = bookCopyService.findAllByBook(isbn, state);

        assertEquals(bookCopyList.size(), result.size());
        verify(bookCopyRepository).findByBookIsbnAndState(isbn, state);
        verify(bookCopyRepository, never()).findByBookIsbn(anyString());
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

    @Test
    public void testSave_bookNotFound(){
        BookCopyRequest bookCopyRequest = new BookCopyRequest(2L, BookCopyState.AVAILABLE);
        when(bookService.getBookOrThrow(2L)).thenThrow(new ResourceNotFoundException("Cannot found Book"));

        assertThrows(ResourceNotFoundException.class, ()-> bookCopyService.save(bookCopyRequest));
        verify(bookService).getBookOrThrow(2L);
        verify(bookCopyRepository, never()).save(any(BookCopy.class));
    }

    @Test
    public void testPatchState(){
        BookCopy bookCopy = DataProvider.bookCopyListMock().get(1);
        bookCopy.setState(BookCopyState.AVAILABLE);
        BookCopyState state = BookCopyState.LOST;
        when(bookCopyRepository.findById(2L)).thenReturn(Optional.of(bookCopy));
        when(bookCopyRepository.save(any(BookCopy.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BookCopyResponse result = bookCopyService.patchState(2L, state);

        assertEquals(state, result.bookCopyState());
        verify(bookCopyRepository).save(any(BookCopy.class));
        verify(bookCopyRepository).findById(2L);
    }

    @Test
    public void testDelete (){
        BookCopy bookCopy = DataProvider.bookCopyListMock().get(0);

        when(bookCopyRepository.findById(2L)).thenReturn(Optional.of(bookCopy));

        bookCopyService.delete(2L);
        verify(bookCopyRepository).findById(2L);
        verify(bookCopyRepository).delete(bookCopy);
    }

    @Test
    public void testDelete_isLoaned(){
        BookCopy bookCopy = DataProvider.bookCopyListMock().get(0);
        bookCopy.setState(BookCopyState.LOANED);

        when(bookCopyRepository.findById(2L)).thenReturn(Optional.of(bookCopy));

        assertThrows(BusinessException.class, () -> bookCopyService.delete(2L));

        verify(bookCopyRepository).findById(2L);
        verify(bookCopyRepository, never()).delete(any());
    }

    @Test
    public void testSelectAvailableCopyOrThrow_ok(){
        BookCopy bookCopy = DataProvider.bookCopyListMock().get(1);
        bookCopy.setId(1L);
        when(bookCopyRepository.findFirstByBookIsbnAndState("123456789", BookCopyState.AVAILABLE)).thenReturn(Optional.of(bookCopy));
        BookCopy result = bookCopyService.selectAvailableCopyOrThrow("123456789");

        assertEquals(bookCopy.getBook().getIsbn(), result.getBook().getIsbn());
        assertEquals(bookCopy.getBook().getName(), result.getBook().getName());
        assertEquals(bookCopy.getId(), result.getId());
        verify(bookCopyRepository).findFirstByBookIsbnAndState("123456789", BookCopyState.AVAILABLE);
    }

    @Test
    public void testSelectAvailableCopyOrThrow_notFound(){
        when(bookCopyRepository.findFirstByBookIsbnAndState("123456789", BookCopyState.AVAILABLE)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, ()-> bookCopyService.selectAvailableCopyOrThrow("123456789"));
        verify(bookCopyRepository).findFirstByBookIsbnAndState("123456789", BookCopyState.AVAILABLE);
    }

    @Test
    public void testGetBookCopyOrThrow_ok(){
        BookCopy bookCopy = DataProvider.bookCopyListMock().get(1);
        bookCopy.setId(1L);
        when(bookCopyRepository.findById(1L)).thenReturn(Optional.of(bookCopy));
        BookCopy result = bookCopyService.getBookCopyOrThrow(1L);

        assertEquals(bookCopy.getBook().getIsbn(), result.getBook().getIsbn());
        assertEquals(bookCopy.getBook().getName(), result.getBook().getName());
        assertEquals(bookCopy.getId(), result.getId());
        verify(bookCopyRepository).findById(1L);
    }

    @Test
    public void testGetBookCopyOrThrow_notFound(){
        when(bookCopyRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, ()-> bookCopyService.getBookCopyOrThrow(1L));
        verify(bookCopyRepository).findById(1L);
    }


}
