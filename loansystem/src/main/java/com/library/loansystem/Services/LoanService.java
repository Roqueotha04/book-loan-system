package com.library.loansystem.Services;

import com.library.loansystem.DTO.Request.LoanRequest;
import com.library.loansystem.DTO.Response.LoanResponse;
import com.library.loansystem.Entities.Loan;

import java.util.List;

public interface LoanService {


    LoanResponse createLoan(LoanRequest loanRequest);

    LoanResponse returnLoan(Long loanId);

    List<LoanResponse> findAll();

    List<LoanResponse> findActiveLoans();

    List<LoanResponse> findReturnedLoans();

    List<LoanResponse> findOverdueLoans();

    List<LoanResponse> findByUser(Long userId);

    List<LoanResponse> findByBook(String isbn);

    public LoanResponse findById(Long id);

    Loan getLoanOrThrow(Long id);

    public Boolean existsActiveLoanByBookId(Long bookId);

    public Boolean existsActiveLoanByUserId(Long userId);
}
