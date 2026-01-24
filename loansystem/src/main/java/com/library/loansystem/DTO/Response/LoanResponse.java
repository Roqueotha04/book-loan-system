package com.library.loansystem.DTO.Response;

import java.time.LocalDate;

public record LoanResponse(Long id,
                           String isbn,
                           Long bookId,
                           String bookName,
                           Long userId,
                           String username,
                           LocalDate startDate,
                           LocalDate dueDate,
                           LocalDate endDate) {
}
