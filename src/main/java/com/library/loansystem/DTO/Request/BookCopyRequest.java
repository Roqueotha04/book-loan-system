package com.library.loansystem.DTO.Request;

import com.library.loansystem.Entities.Enums.BookCopyState;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record BookCopyRequest(@Valid @NotNull Long bookId,@NotNull BookCopyState state) {
}
