package com.library.loansystem.Services.Validators;

import com.library.loansystem.Entities.Book;
import com.library.loansystem.Entities.User;
import com.library.loansystem.Exceptions.BadRequestException;
import com.library.loansystem.Exceptions.BusinessException;
import com.library.loansystem.Repositories.LoanRepository;

import java.time.LocalDate;

public class LoanValidator {
    private static final int MAX_LOAN_DAYS = 30;
    private static final int MIN_LOAN_DAYS = 1;
    private static final int MAX_LOANS_PER_USER = 3;

    private final LoanRepository loanRepository;

    public LoanValidator(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    private void validateLoan(User user, Book book, LocalDate dueDate) {

        //Error 400
        if (dueDate.isBefore(LocalDate.now().plusDays(MIN_LOAN_DAYS)) || dueDate.isAfter(LocalDate.now().plusDays(MAX_LOAN_DAYS))) {
            throw new BadRequestException("Loan duration must be between 1 and 30 days");
        }

        if (!user.getActive())
            throw new BusinessException("User is inactive");

        if (!book.getActive())
            throw new BusinessException("Book is inactive");

        if (book.getStock() <= 0)
            throw new BusinessException("Book has no available stock");

        if (loanRepository.countByUserIdAndActiveTrue(user.getId()) >= MAX_LOANS_PER_USER)
            throw new BusinessException("User reached maximum active loans");

        if (loanRepository.existsByUserIdAndBookIdAndActiveTrue(user.getId(), book.getId()))
            throw new BusinessException("User already has this book on loan");

        if (loanRepository.existsByUserIdAndActiveTrueAndDueDateBefore(user.getId(), LocalDate.now())) {
            throw new BusinessException("User has overdue loans");
        }
    }
}
