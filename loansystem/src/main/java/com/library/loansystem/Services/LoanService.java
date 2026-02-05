package com.library.loansystem.Services;

import com.library.loansystem.DTO.Request.LoanRequest;
import com.library.loansystem.DTO.Response.LoanResponse;
import com.library.loansystem.Entities.Enums.LoanStatus;
import com.library.loansystem.Entities.Loan;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;

public interface LoanService {

    LoanResponse createLoan(LoanRequest loanRequest, Authentication auth);

    LoanResponse returnLoan(Long loanId);

    LoanResponse renewLoan(Long loanId, LocalDate newDate, Authentication auth);

    List<LoanResponse> findByDateRange(LocalDate startDate, LocalDate endDate);

    public int countByDateRange (LocalDate startDate, LocalDate endDate);

    List<LoanResponse> findAll(LoanStatus status);

    List<LoanResponse> findByUser(Long userId, LoanStatus status, Authentication auth);

    List<LoanResponse> findByBook(String isbn, LoanStatus status);

    public LoanResponse findById(Long id);

    Loan getLoanOrThrow(Long id);

    public Boolean existsActiveLoanByBookId(Long bookId);

    public Boolean existsActiveLoanByUserId(Long userId);
}
