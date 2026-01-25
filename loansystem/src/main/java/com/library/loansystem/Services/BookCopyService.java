package com.library.loansystem.Services;


import com.library.loansystem.DTO.Request.BookCopyRequest;
import com.library.loansystem.DTO.Response.BookCopyResponse;
import com.library.loansystem.Entities.BookCopy;
import com.library.loansystem.Entities.Enums.BookCopyState;

import java.util.List;

public interface BookCopyService {
    public List <BookCopyResponse> findAllByBook(Long bookId);
    public BookCopyResponse save(BookCopyRequest bookCopyRequest);
    public BookCopyResponse patchState(Long id, BookCopyState bookCopyState);
    public Boolean existsLoanedCopyByBookId(Long bookId);
    public BookCopy selectAvailableCopyOrThrow(Long bookId);
    public BookCopy getBookCopyOrThrow (Long id);

}
