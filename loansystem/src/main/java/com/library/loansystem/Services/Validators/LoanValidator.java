package com.library.loansystem.Services.Validators;

import com.library.loansystem.Entities.Book;
import com.library.loansystem.Entities.BookCopy;
import com.library.loansystem.Entities.User;
import com.library.loansystem.Exceptions.BadRequestException;
import com.library.loansystem.Exceptions.BusinessException;
import com.library.loansystem.Repositories.LoanRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class LoanValidator {
    private static final int MAX_LOAN_DAYS = 30;
    private static final int MIN_LOAN_DAYS = 1;
    private static final int MAX_LOANS_PER_USER = 3;

    private final LoanRepository loanRepository;

    public LoanValidator(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    public void validateLoan(User user, BookCopy bookCopy, LocalDate dueDate) {

        //Error 400
        if (dueDate.isBefore(LocalDate.now().plusDays(MIN_LOAN_DAYS)) || dueDate.isAfter(LocalDate.now().plusDays(MAX_LOAN_DAYS))) {
            throw new BadRequestException("Loan duration must be between 1 and 30 days");
        }

        if (!user.getActive())
            throw new BusinessException("User is inactive");

        if (!bookCopy.getBook().getActive())
            throw new BusinessException("Book is inactive");

        /// Add available copies validation

        if (loanRepository.countByUserIdAndActiveTrue(user.getId()) >= MAX_LOANS_PER_USER)
            throw new BusinessException("User reached maximum active loans");

        if (loanRepository.existsByUserIdAndBookCopyIdAndActiveTrue(user.getId(), bookCopy.getId()))
            throw new BusinessException("User already has this book on loan");

        if (loanRepository.existsByUserIdAndActiveTrueAndDueDateBefore(user.getId(), LocalDate.now())) {
            throw new BusinessException("User has overdue loans");
        }
    }
}
