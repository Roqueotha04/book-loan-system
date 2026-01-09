package com.library.loansystem.Mapper;

import com.library.loansystem.DTO.Response.BookResponse;
import com.library.loansystem.DataProvider;
import com.library.loansystem.Entities.Book;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;

public class BookMapperTest {

    AuthorMapper authorMapper = new AuthorMapper();
    BookMapper bookMapper = new BookMapper(authorMapper);

    @Test
    public void testToResponse (){
        Book book = DataProvider.bookListMock().get(1);

        BookResponse result = bookMapper.toResponse(book);

        assertNotNull(result);
        assertEquals(book.getId(), result.getId());
        assertEquals(book.getName(), result.getName());
        assertEquals(book.getStock(), result.getStock());
        assertEquals(book.getGenre(), result.getGenre());
        assertEquals(book.getActive(), result.getActive());
        assertEquals(book.getPublisher().getId(), result.getPublisher().getId());
        assertEquals(book.getPublisher().getName(), result.getPublisher().getName());
    }
}
