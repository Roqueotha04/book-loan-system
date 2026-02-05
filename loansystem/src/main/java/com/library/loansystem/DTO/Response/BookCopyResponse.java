package com.library.loansystem.DTO.Response;

import com.library.loansystem.Entities.Enums.BookCopyState;

public record BookCopyResponse(Long id, String bookIsbn, String bookName, BookCopyState bookCopyState) {
}
