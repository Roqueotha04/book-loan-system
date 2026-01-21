package com.library.loansystem.Mapper;

import com.library.loansystem.DTO.Response.LoanResponse;
import com.library.loansystem.DataProvider;
import com.library.loansystem.Entities.Book;
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
        Book book = DataProvider.bookListMock().get(0);

        LocalDate startDate = LocalDate.now();
        LocalDate dueDate = startDate.plusDays(14);

        Loan loan = new Loan(user, book, dueDate);

        LoanResponse result = loanMapper.toResponse(loan);

        assertEquals(loan.getId(), result.id());
        assertEquals(book.getId(), result.bookId());
        assertEquals(book.getName(), result.bookName());
        assertEquals(user.getId(), result.userId());
        assertEquals(user.getUsername(), result.username());
        assertEquals(loan.getStartDate(), result.startDate());
        assertEquals(loan.getDueDate(), result.dueDate());
        assertEquals(loan.getEndDate(), result.endDate());
    }
}
