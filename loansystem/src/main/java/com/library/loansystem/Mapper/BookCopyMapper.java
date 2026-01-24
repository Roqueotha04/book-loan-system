package com.library.loansystem.Mapper;

import com.library.loansystem.DTO.Response.BookCopyResponse;
import com.library.loansystem.Entities.BookCopy;
import org.springframework.stereotype.Component;

@Component
public class BookCopyMapper {
    public BookCopyResponse toResponse (BookCopy bookCopy){
        return new BookCopyResponse(bookCopy.getId(), bookCopy.getBook().getName(), bookCopy.getState());
    }
}
