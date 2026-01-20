package com.library.loansystem.Services;

public interface LoanService {
    public Boolean existsActiveLoanByBookId(Long bookId);
    public Boolean existsActiveLoanByUserId(Long userId);
}
