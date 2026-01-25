package com.library.loansystem.Mapper;

import com.library.loansystem.DTO.Response.LoanResponse;
import com.library.loansystem.DataProvider;
import com.library.loansystem.Entities.Book;
import com.library.loansystem.Entities.BookCopy;
import com.library.loansystem.Entities.Loan;
import com.library.loansystem.Entities.User;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoanMapperTest {

    private final LoanMapper loanMapper = new LoanMapper();

    @Test
    public void testToResponse() {
        User user = DataProvider.userListMock().get(0);
        BookCopy bookCopy = DataProvider.bookCopyListMock().get(0);

        LocalDate startDate = LocalDate.now();
        LocalDate dueDate = startDate.plusDays(14);

        Loan loan = new Loan(user, bookCopy, dueDate);

        LoanResponse result = loanMapper.toResponse(loan);

        assertEquals(loan.getId(), result.id());
        assertEquals(bookCopy.getId(), result.bookCopyId());
        assertEquals(bookCopy.getBook().getIsbn(), result.isbn());
        assertEquals(bookCopy.getBook().getName(), result.bookName());
        assertEquals(user.getId(), result.userId());
        assertEquals(user.getUsername(), result.username());
        assertEquals(loan.getStartDate(), result.startDate());
        assertEquals(loan.getDueDate(), result.dueDate());
        assertEquals(loan.getEndDate(), result.endDate());
    }
}
