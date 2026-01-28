package com.library.loansystem.Services;

import com.library.loansystem.DTO.Request.LoanRequest;
import com.library.loansystem.DTO.Response.LoanResponse;
import com.library.loansystem.Entities.Enums.LoanStatus;
import com.library.loansystem.Entities.Loan;

import java.util.List;

public interface LoanService {


    LoanResponse createLoan(LoanRequest loanRequest);

    LoanResponse returnLoan(Long loanId);

    List<LoanResponse> findAll(LoanStatus status);

    List<LoanResponse> findByUser(Long userId, LoanStatus status);

    List<LoanResponse> findByBook(String isbn, LoanStatus status);

    public LoanResponse findById(Long id);

    Loan getLoanOrThrow(Long id);

    public Boolean existsActiveLoanByBookId(Long bookId);

    public Boolean existsActiveLoanByUserId(Long userId);
}
