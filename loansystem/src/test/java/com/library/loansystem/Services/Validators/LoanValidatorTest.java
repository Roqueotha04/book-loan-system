package com.library.loansystem.Services.Validators;

import com.library.loansystem.Entities.Book;
import com.library.loansystem.Entities.User;
import com.library.loansystem.Exceptions.BadRequestException;
import com.library.loansystem.Exceptions.BusinessException;
import com.library.loansystem.Repositories.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Component
@ExtendWith(MockitoExtension.class)
public class LoanValidatorTest {

    @Mock
    private LoanRepository loanRepository;
    private LoanValidator loanValidator;

    private User user;
    private Book book;
    private LocalDate validDueDate;

    @BeforeEach
    void setUp(){
        loanValidator = new LoanValidator(loanRepository);

        user = validUser();
        book = validBook();
        validDueDate = validDueDate();
    }

    private User validUser() {
        User user = new User();
        user.setId(1L);
        user.setActive(true);
        return user;
    }

    private Book validBook() {
        Book book = new Book();
        book.setId(1L);
        book.setActive(true);
        book.setStock(5);
        return book;
    }

    private LocalDate validDueDate() {
        return LocalDate.now().plusDays(10);
    }

    @Test
    void shouldNotThrowExceptionWhenLoanIsValid() {
        loanValidator.validateLoan(user, book, validDueDate);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 31})
    void shouldThrowExceptionWhenDueDateIsOutOfRange(int days) {
        LocalDate invalidDueDate = LocalDate.now().plusDays(days);

        assertThrows(BadRequestException.class,
                () -> loanValidator.validateLoan(user, book, invalidDueDate));
    }

    @Test
    void shouldThrowExceptionWhenUserIsInactive() {
        user.setActive(false);

        assertThrows(BusinessException.class, () -> loanValidator.validateLoan(user, book, validDueDate));
    }

    @Test
    void shouldThrowExceptionWhenBookIsInactive() {
        book.setActive(false);

        assertThrows(BusinessException.class, () -> loanValidator.validateLoan(user, book, validDueDate));
    }

    @Test
    void shouldThrowExceptionWhenBookHasNoStock() {
        book.setStock(0);

        assertThrows(BusinessException.class, () -> loanValidator.validateLoan(user, book, validDueDate));
    }

    @Test
    void shouldThrowExceptionWhenUserReachedMaxLoans() {
        when(loanRepository.countByUserIdAndActiveTrue(user.getId()))
                .thenReturn(3);

        assertThrows(BusinessException.class, () -> loanValidator.validateLoan(user, book, validDueDate));

        verify(loanRepository).countByUserIdAndActiveTrue(user.getId());
    }

    @Test
    void shouldThrowExceptionWhenUserAlreadyHasBookOnLoan() {
        when(loanRepository.existsByUserIdAndBookIdAndActiveTrue(user.getId(), book.getId())).thenReturn(true);

        assertThrows(BusinessException.class, () -> loanValidator.validateLoan(user, book, validDueDate));

        verify(loanRepository).existsByUserIdAndBookIdAndActiveTrue(user.getId(), book.getId());
    }

    @Test
    void shouldThrowExceptionWhenUserHasOverdueLoans() {
        when(loanRepository.existsByUserIdAndActiveTrueAndDueDateBefore(eq(user.getId()), any()))
                .thenReturn(true);

        assertThrows(BusinessException.class, () -> loanValidator.validateLoan(user, book, validDueDate));

        verify(loanRepository).existsByUserIdAndActiveTrueAndDueDateBefore(eq(user.getId()), any());
    }
}
