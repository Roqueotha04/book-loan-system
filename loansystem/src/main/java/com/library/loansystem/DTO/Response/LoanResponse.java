package com.library.loansystem.DTO.Response;

import java.time.LocalDate;

public record LoanResponse(Long id,
                           LocalDate startDate,
                           LocalDate dueDate,
                           LocalDate endDate,
                           BookLoanSummary book,
                           UserLoanSummary user) {
}
