package com.library.loansystem.Services;

import com.library.loansystem.DTO.Response.LoanResponse;
import com.library.loansystem.Entities.Loan;
import com.library.loansystem.Mapper.LoanMapper;
import com.library.loansystem.Repositories.LoanRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoanServiceImpl implements LoanService {

    private static final int MAX_LOAN_DAYS = 30;
    private static final int MIN_LOAN_DAYS = 1;

    private final LoanRepository loanRepository;

    private final LoanMapper loanMapper;

    public LoanServiceImpl(LoanRepository loanRepository, LoanMapper loanMapper) {
        this.loanRepository = loanRepository;
        this.loanMapper = loanMapper;
    }

    @Override
    public List<LoanResponse> findAll() {
        return loanRepository.findAll().stream()
                .map(loanMapper::toResponse)
                .toList();
    }

    @Override
    public List<LoanResponse> findActiveLoans() {
        return loanRepository.findAll().stream()
                .filter(Loan::getActive)
                .map(loanMapper::toResponse)
                .toList();
    }

    @Override
    public List<LoanResponse> findByUser(Long userId) {
        return List.of();
    }

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
    public Boolean existsActiveLoanByBookId(Long bookId) {
        return loanRepository.existsByBookIdAndActiveTrue(bookId);
    }

    @Override
    public Boolean existsActiveLoanByUserId(Long userId){return loanRepository.existsByUserIdAndActiveTrue(userId);}
}
