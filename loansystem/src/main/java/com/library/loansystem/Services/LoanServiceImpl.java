package com.library.loansystem.Services;

import com.library.loansystem.Entities.Loan;
import com.library.loansystem.Repositories.LoanRepository;

public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;

    public LoanServiceImpl(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Override
    public Boolean existsActiveLoanByBookId(Long bookId) {
        return loanRepository.existsByBookIdAndActiveTrue(bookId);
    }
}
