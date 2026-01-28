package com.library.loansystem.Services;


import com.library.loansystem.DTO.Request.BookCopyRequest;
import com.library.loansystem.DTO.Response.BookCopyResponse;
import com.library.loansystem.Entities.BookCopy;
import com.library.loansystem.Entities.Enums.BookCopyState;

import java.util.List;

public interface BookCopyService {
    public List <BookCopyResponse> findAllByBook(String Isbn, BookCopyState state);
    public BookCopyResponse save(BookCopyRequest bookCopyRequest);
    public BookCopyResponse patchState(Long id, BookCopyState bookCopyState);
    public void delete(Long id);
    public BookCopy selectAvailableCopyOrThrow(String isbn);
    public BookCopy getBookCopyOrThrow (Long id);

}
