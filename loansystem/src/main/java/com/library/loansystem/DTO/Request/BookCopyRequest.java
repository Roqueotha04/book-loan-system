package com.library.loansystem.DTO.Request;

import com.library.loansystem.Entities.Enums.BookCopyState;

public record BookCopyRequest(Long BookId, BookCopyState state) {
}
