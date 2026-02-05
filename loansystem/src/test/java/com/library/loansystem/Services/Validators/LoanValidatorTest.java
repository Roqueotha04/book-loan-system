package com.library.loansystem.Services.Validators;

import com.library.loansystem.DataProvider;
import com.library.loansystem.Entities.BookCopy;
import com.library.loansystem.Entities.Enums.BookCopyState;
import com.library.loansystem.Entities.UserEntity;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanValidatorTest {

    @Mock
    private LoanRepository loanRepository;

    private LoanValidator loanValidator;

    private UserEntity userEntity;
    private BookCopy bookCopy;
    private LocalDate validDueDate;

    @BeforeEach
    void setUp() {
        loanValidator = new LoanValidator(loanRepository);
        userEntity = DataProvider.userListMock().get(0);
        userEntity.setActive(true);
        bookCopy = DataProvider.bookCopyListMock().get(0);
        bookCopy.setState(BookCopyState.AVAILABLE);
        validDueDate = LocalDate.now().plusDays(10);
    }

    @Test
    void shouldNotThrowExceptionWhenLoanIsValid() {
        loanValidator.validateLoan(userEntity, bookCopy, validDueDate);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 31})
    void shouldThrowExceptionWhenDueDateIsOutOfRange(int days) {
        LocalDate invalidDueDate = LocalDate.now().plusDays(days);

        assertThrows(BadRequestException.class,
                () -> loanValidator.validateLoan(userEntity, bookCopy, invalidDueDate));
    }

    @Test
    void shouldThrowExceptionWhenUserIsInactive() {
        userEntity.setActive(false);

        assertThrows(BusinessException.class,
                () -> loanValidator.validateLoan(userEntity, bookCopy, validDueDate));
    }

    @Test
    void shouldThrowExceptionWhenBookIsInactive() {
        bookCopy.getBook().setActive(false);

        assertThrows(BusinessException.class,
                () -> loanValidator.validateLoan(userEntity, bookCopy, validDueDate));

        verify(loanRepository, never()).countByUserEntityIdAndEndDateIsNull(anyLong());
    }

    @Test
    void shouldThrowExceptionWhenUserReachedMaxLoans() {
        when(loanRepository.countByUserEntityIdAndEndDateIsNull(userEntity.getId()))
                .thenReturn(3);

        assertThrows(BusinessException.class,
                () -> loanValidator.validateLoan(userEntity, bookCopy, validDueDate));

        verify(loanRepository).countByUserEntityIdAndEndDateIsNull(userEntity.getId());
    }

    @Test
    void shouldThrowExceptionWhenUserAlreadyHasBookCopyOnLoan() {
        when(loanRepository.existsByUserEntityIdAndBookCopyBookIsbnAndEndDateIsNull(
                userEntity.getId(), bookCopy.getBook().getIsbn()))
                .thenReturn(true);

        assertThrows(BusinessException.class,
                () -> loanValidator.validateLoan(userEntity, bookCopy, validDueDate));

        verify(loanRepository).existsByUserEntityIdAndBookCopyBookIsbnAndEndDateIsNull(
                userEntity.getId(), bookCopy.getBook().getIsbn());
    }

    @Test
    void shouldThrowExceptionWhenUserHasOverdueLoans() {
        when(loanRepository.existsByUserEntityIdAndEndDateIsNullAndDueDateBefore(
                eq(userEntity.getId()), any(LocalDate.class)))
                .thenReturn(true);

        assertThrows(BusinessException.class,
                () -> loanValidator.validateLoan(userEntity, bookCopy, validDueDate));

        verify(loanRepository).existsByUserEntityIdAndEndDateIsNullAndDueDateBefore(
                eq(userEntity.getId()), any(LocalDate.class));
    }
}
