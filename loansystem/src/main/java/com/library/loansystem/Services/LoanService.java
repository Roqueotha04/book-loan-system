package com.library.loansystem.Services;

import com.library.loansystem.DTO.Response.LoanResponse;
import com.library.loansystem.Entities.Loan;

import java.util.List;

public interface LoanService {
    public Boolean existsActiveLoanByBookId(Long bookId);

    public Boolean existsActiveLoanByUserId(Long userId);

    LoanResponse createLoan(Long userId, Long bookId);

    LoanResponse returnLoan(Long loanId);

    Loan getLoanOrThrow(Long id);

    List<LoanResponse> findAll();

    List<LoanResponse> findActiveLoans();

    List<LoanResponse> findByUser(Long userId);

}
