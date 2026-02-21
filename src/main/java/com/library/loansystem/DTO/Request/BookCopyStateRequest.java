package com.library.loansystem.DTO.Request;

import com.library.loansystem.Entities.Enums.BookCopyState;
import jakarta.validation.constraints.NotNull;

public record BookCopyStateRequest(
        @NotNull(message = "The state is mandatory")
        BookCopyState state
) {}
