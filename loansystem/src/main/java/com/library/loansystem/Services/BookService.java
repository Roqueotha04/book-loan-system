package com.library.loansystem.Services;

import com.library.loansystem.DTO.Request.BookRequest;
import com.library.loansystem.DTO.Response.BookResponse;
import com.library.loansystem.Entities.Book;
import com.library.loansystem.Entities.BookCopy;
import com.library.loansystem.Entities.Enums.BookCopyState;

import java.util.List;

public interface BookService {
    public List<BookResponse> findAll();
    public BookResponse findById(Long id);
    public BookResponse save(BookRequest bookRequest);
    public BookResponse update (Long id, BookRequest bookRequest);
    public void delete (Long id);
    public BookResponse changeStatus (Long id);
    public BookResponse updateStock(Long id, int newStock);
    public BookResponse findByIsbn(String isbn);
    public Book getBookOrThrow (Long id);
    public List<BookResponse> findByNameContaining(String name);
    public List<BookResponse> findByGenre(String genre);
    public List<BookResponse> findByAuthor(Long authorId);
}
