package com.library.loansystem.DTO.Request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record LoanRequest(@NotNull @Future LocalDate dueDate, @NotNull Long userId, @NotNull String isbn) {
}
