package com.library.loansystem.DTO.Book;

import com.library.loansystem.DTO.Author.AuthorMapper;
import com.library.loansystem.DTO.Author.AuthorResponse;
import com.library.loansystem.DTO.PublisherResponse;
import com.library.loansystem.Entities.AuthorXBook;
import com.library.loansystem.Entities.Book;

import java.util.ArrayList;
import java.util.List;

public class BookMapper {

    private final AuthorMapper authorMapper;

    public BookMapper(AuthorMapper authorMapper) {
        this.authorMapper = authorMapper;
    }

    public BookResponse toResponse (Book book){
        BookResponse bookResponse = new BookResponse();
        bookResponse.setId(book.getId());
        bookResponse.setName(book.getName());
        bookResponse.setStock(book.getStock());
        bookResponse.setGenre(book.getGenre());
        bookResponse.setActive(book.getActive());
        bookResponse.setPublisher(new PublisherResponse(book.getPublisher().getId(),book.getPublisher().getName()));

        //Maps the AuthorxBook List to AuthorResponse objects
        List<AuthorResponse> authorResponseList = book.getAuthorXBooks().stream()
                .map(AuthorXBook::getAuthor)
                .map(authorMapper::toResponse)
                .toList();

        bookResponse.setAuthors(authorResponseList);
        return bookResponse;
    }

}
