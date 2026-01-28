package com.library.loansystem.Mapper;

import com.library.loansystem.DTO.Response.BookLoanSummary;
import com.library.loansystem.DTO.Response.LoanResponse;
import com.library.loansystem.DTO.Response.UserLoanSummary;
import com.library.loansystem.Entities.Loan;
import org.springframework.stereotype.Component;

@Component
public class LoanMapper {
    public LoanResponse toResponse(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getStartDate(),
                loan.getDueDate(),
                loan.getEndDate(),
                new BookLoanSummary(loan.getBookCopy().getId(),loan.getBookCopy().getBook().getIsbn(), loan.getBookCopy().getBook().getName()),
                new UserLoanSummary(loan.getUser().getId(), loan.getUser().getUsername())
        );
    }
}
