package com.library.loansystem.Services;

import com.library.loansystem.DTO.Response.LoanResponse;
import com.library.loansystem.Entities.Loan;
import com.library.loansystem.Repositories.LoanRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoanServiceImpl implements LoanService {

    private static final int MAX_LOAN_DAYS = 30;
    private static final int MIN_LOAN_DAYS = 1;

    private final LoanRepository loanRepository;

    public LoanServiceImpl(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Override
    public Boolean existsActiveLoanByBookId(Long bookId) {
        return loanRepository.existsByBookIdAndActiveTrue(bookId);
    }

    @Override
    public Boolean existsActiveLoanByUserId(Long userId){return loanRepository.existsByUserIdAndActiveTrue(userId);}

    @Override
    public LoanResponse createLoan(Long userId, Long bookId) {
        return null;
    }

    @Override
    public LoanResponse returnLoan(Long loanId) {
        return null;
    }

    @Override
    public Loan getLoanOrThrow(Long id) {
        return null;
    }

    @Override
    public List<LoanResponse> findAll() {
        return List.of();
    }

    @Override
    public List<LoanResponse> findActiveLoans() {
        return List.of();
    }

    @Override
    public List<LoanResponse> findByUser(Long userId) {
        return List.of();
    }
}
