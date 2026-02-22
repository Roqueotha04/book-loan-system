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
        assertEquals(book.getId(), result.id());
        assertEquals(book.getName(), result.name());
        assertEquals(book.getGenre(), result.genre());
        assertEquals(book.getActive(), result.active());
        assertEquals(book.getIsbn(), result.isbn());
        assertEquals(book.getPublisher().getId(), result.publisher().id());
        assertEquals(book.getPublisher().getName(), result.publisher().name());
    }
}
