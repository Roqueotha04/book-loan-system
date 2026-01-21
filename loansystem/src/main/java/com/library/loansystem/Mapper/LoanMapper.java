package com.library.loansystem.Mapper;

import com.library.loansystem.DTO.Response.LoanResponse;
import com.library.loansystem.Entities.Loan;
import org.springframework.stereotype.Component;

@Component
public class LoanMapper {
    public LoanResponse toResponse(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getBook().getId(),
                loan.getBook().getName(),
                loan.getUser().getId(),
                loan.getUser().getUsername(),
                loan.getStartDate(),
                loan.getDueDate(),
                loan.getEndDate()
        );
    }
}
