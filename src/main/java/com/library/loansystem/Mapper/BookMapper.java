package com.library.loansystem.Mapper;

import com.library.loansystem.DTO.Response.BookResponse;
import com.library.loansystem.DTO.Response.AuthorResponse;
import com.library.loansystem.DTO.Response.PublisherResponse;
import com.library.loansystem.Entities.AuthorXBook;
import com.library.loansystem.Entities.Book;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookMapper {

    private final AuthorMapper authorMapper;

    public BookMapper(AuthorMapper authorMapper) {
        this.authorMapper = authorMapper;
    }

    public BookResponse toResponse (Book book){
        List<AuthorResponse> authorResponseList = book.getAuthorXBooks().stream()
                .map(AuthorXBook::getAuthor)
                .map(authorMapper::toResponse)
                .toList();

        return new BookResponse(book.getId(),
                book.getName(),
                book.getGenre(),
                book.getActive(),
                book.getIsbn(),
                new PublisherResponse(book.getPublisher().getId(),book.getPublisher().getName()),
                authorResponseList);
    }

}
