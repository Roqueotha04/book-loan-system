package com.library.loansystem.Mapper;

import com.library.loansystem.DTO.Response.LoanResponse;
import com.library.loansystem.DataProvider;
import com.library.loansystem.Entities.BookCopy;
import com.library.loansystem.Entities.Loan;
import com.library.loansystem.Entities.UserEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoanMapperTest {

    private final LoanMapper loanMapper = new LoanMapper();

    @Test
    public void testToResponse() {
        UserEntity userEntity = DataProvider.userListMock().get(0);
        BookCopy bookCopy = DataProvider.bookCopyListMock().get(0);

        LocalDate startDate = LocalDate.now();
        LocalDate dueDate = startDate.plusDays(14);
        Loan loan = new Loan(userEntity, bookCopy, dueDate);

        LoanResponse result = loanMapper.toResponse(loan);

        assertEquals(loan.getId(), result.id());
        assertEquals(loan.getStartDate(), result.startDate());
        assertEquals(loan.getDueDate(), result.dueDate());
        assertEquals(loan.getEndDate(), result.endDate());
        assertEquals(bookCopy.getId(), result.book().bookCopyId());
        assertEquals(bookCopy.getBook().getIsbn(), result.book().isbn());
        assertEquals(bookCopy.getBook().getName(), result.book().bookName());
        assertEquals(userEntity.getId(), result.user().userId());
        assertEquals(userEntity.getUsername(), result.user().username());
    }
}
