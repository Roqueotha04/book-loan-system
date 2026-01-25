package com.library.loansystem.Services;

import com.library.loansystem.DTO.Request.LoanRequest;
import com.library.loansystem.DTO.Response.LoanResponse;
import com.library.loansystem.DataProvider;
import com.library.loansystem.Entities.Book;
import com.library.loansystem.Entities.Loan;
import com.library.loansystem.Entities.User;
import com.library.loansystem.Mapper.LoanMapper;
import com.library.loansystem.Repositories.LoanRepository;
import com.library.loansystem.Services.Validators.LoanValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.xml.crypto.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class LoanServiceImplTest {

    private static final LocalDate FIXED_DATE = LocalDate.of(2026, 1, 21);

    @Mock
    private LoanValidator loanValidator;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private UserService userService;

    private LoanServiceImpl loanService;

    @BeforeEach
    void setUp (){
        LoanMapper loanMapper = new LoanMapper();
        loanService = new LoanServiceImpl(loanValidator, loanRepository, loanMapper, userService);
    }

    @Test
    public void testCreateLoan(){
        User user = DataProvider.userListMock().get(0);
        user.setId(1L);
        Book book = DataProvider.bookListMock().get(0);
        book.setId(1L);
        LoanRequest loanRequest = new LoanRequest(FIXED_DATE.plusDays(10), user.getId(), book.getId());

        when(bookService.getBookOrThrow(1L)).thenReturn(book);
        when(userService.getUserOrThrow(1L)).thenReturn(user);
        when(loanRepository.save(any(Loan.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LoanResponse result = loanService.createLoan(loanRequest);
        assertEquals(loanRequest.dueDate(), result.dueDate());
        assertEquals(loanRequest.bookId(), result.bookId());
        assertEquals(loanRequest.userId(), result.userId());

        verify(bookService).getBookOrThrow(1L);
        verify(userService).getUserOrThrow(1L);
        verify(bookService).updateStock(book.getId(), book.getStock() - 1);
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    void shouldPropagateExceptionFromValidator() {
        User user = DataProvider.userListMock().get(1);
        Book book = DataProvider.bookListMock().get(1);
        LocalDate dueDate = LocalDate.now().plusDays(10);
        when(userService.getUserOrThrow(any())).thenReturn(user);
        when(bookService.getBookOrThrow(any())).thenReturn(book);

        doThrow(new RuntimeException())
                .when(loanValidator).validateLoan(any(), any(), any());

        assertThrows(RuntimeException.class,
                () -> loanService.createLoan(new LoanRequest(dueDate, 1L, 1L)));

        verify(loanRepository, never()).save(any(Loan.class));
        verify(bookService, never()).updateStock(anyLong(), anyInt());
    }

    @Test
    public void testReturnLoan(){
        Loan loan = DataProvider.loanListMock().get(0);

        when(loanRepository.findById(1L))
                .thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LoanResponse result = loanService.returnLoan(1L);
        assertNotNull(result.endDate());
        assertFalse(loan.getActive());
        assertEquals(loan.getUser().getId(), result.userId());
        assertEquals(loan.getBook().getId(), result.bookId());

        verify(loanRepository).findById(1L);
        verify(loanRepository).save(any(Loan.class));
    }


    @Test
    public void testFindAll(){
        List<Loan> loanList = DataProvider.loanListMock();

        when(loanRepository.findAll())
                .thenReturn(loanList);

        List<LoanResponse> result = loanService.findAll();

        assertEquals(loanList.size(), result.size());
        assertEquals(loanList.get(1).getBook().getName(), result.get(1).bookName());
        assertEquals(loanList.get(1).getUser().getUsername(), result.get(1).username());
        verify(loanRepository).findAll();
    }

    @Test
    public void testFindActiveLoans() {
        List<Loan> activeLoans = DataProvider.loanListMock();
        activeLoans.forEach(loan -> loan.setActive(true));

        when(loanRepository.findByActiveTrue()).thenReturn(activeLoans);
        List<LoanResponse> result = loanService.findActiveLoans();

        assertNotNull(result);
        assertEquals(activeLoans.size(), result.size());

        verify(loanRepository).findByActiveTrue();
    }

    @Test
    public void testFindReturnedLoans() {

        List<Loan> returnedLoans = DataProvider.loanListMock();
        returnedLoans.forEach(loan -> loan.setActive(false));

        when(loanRepository.findByActiveFalse()).thenReturn(returnedLoans);
        List<LoanResponse> result = loanService.findReturnedLoans();

        assertNotNull(result);
        assertEquals(returnedLoans.size(), result.size());
        verify(loanRepository).findByActiveFalse();
    }

    @Test
    public void testFindOverdueLoans() {
        List<Loan> overdueLoans = DataProvider.loanListMock();
        overdueLoans.forEach(loan -> {
            loan.setActive(true);
            loan.setDueDate(FIXED_DATE.minusDays(1));
        });

        when(loanRepository.findByActiveTrueAndDueDateBefore(any(LocalDate.class))).thenReturn(overdueLoans);
        List<LoanResponse> result = loanService.findOverdueLoans();

        assertNotNull(result);
        assertEquals(overdueLoans.size(), result.size());
        verify(loanRepository).findByActiveTrueAndDueDateBefore(any(LocalDate.class));
    }


    @Test
    public void testFindByUser() {
        List<Loan> userLoans = DataProvider.loanListMock();
        userLoans.forEach(loan -> loan.setActive(true));
        Long userId = 1L;

        when(loanRepository.findByUserIdAndActiveTrue(userId))
                .thenReturn(userLoans);
        List<LoanResponse> result = loanService.findByUser(userId);

        assertNotNull(result);
        assertEquals(userLoans.size(), result.size());
        verify(loanRepository).findByUserIdAndActiveTrue(userId);
    }

    @Test
    public void testFindByBook() {
        List<Loan> bookLoans = DataProvider.loanListMock();
        bookLoans.forEach(loan -> loan.setActive(true));
        Long bookId = 1L;

        when(loanRepository.findByBookCopyBookIdAndActiveTrue(bookId)).thenReturn(bookLoans);
        List<LoanResponse> result = loanService.findByBook(bookId);

        assertNotNull(result);
        assertEquals(bookLoans.size(), result.size());
        verify(loanRepository).findByBookCopyBookIdAndActiveTrue(bookId);
    }

    @Test
    public void testExistsActiveLoanByUserId(){
        when(loanRepository.existsByUserIdAndActiveTrue(1L))
                .thenReturn(true);

        Boolean result = loanService.existsActiveLoanByUserId(1L);

        assertTrue(result);
        verify(loanRepository).existsByUserIdAndActiveTrue(1L);
    }

    @Test
    public void testExistsActiveLoanByBookId(){
        when(loanRepository.existsByBookCopyBookIdAndActiveTrue(1L))
                .thenReturn(true);

        Boolean result = loanService.existsActiveLoanByBookId(1L);

        assertTrue(result);
        verify(loanRepository).existsByBookCopyBookIdAndActiveTrue(1L);
    }
}
