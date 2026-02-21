package com.library.loansystem.Mapper;

import com.library.loansystem.DTO.Response.BookCopyResponse;
import com.library.loansystem.DataProvider;
import com.library.loansystem.Entities.BookCopy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class BookCopyMapperTest {

    private final BookCopyMapper bookCopyMapper = new BookCopyMapper();

    @Test
    public void testToResponse() {
        BookCopy bookCopy = DataProvider.bookCopyListMock().get(0);
        bookCopy.setId(1L);

        BookCopyResponse result = bookCopyMapper.toResponse(bookCopy);

        assertEquals(bookCopy.getId(), result.id());
        assertEquals(bookCopy.getBook().getIsbn(), result.bookIsbn());
        assertEquals(bookCopy.getBook().getName(), result.bookName());
        assertEquals(bookCopy.getState(), result.bookCopyState());
    }
}


