package com.library.loansystem.Services;

import com.library.loansystem.DTO.Request.LoanRequest;
import com.library.loansystem.DTO.Response.LoanResponse;
import com.library.loansystem.DataProvider;
import com.library.loansystem.Entities.Book;
import com.library.loansystem.Entities.BookCopy;
import com.library.loansystem.Entities.Enums.BookCopyState;
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
    private BookCopyService bookCopyService;

    @Mock
    private UserService userService;

    private LoanServiceImpl loanService;

    @BeforeEach
    void setUp() {
        LoanMapper loanMapper = new LoanMapper();
        loanService = new LoanServiceImpl(loanValidator, loanRepository, loanMapper, bookCopyService, userService);
    }

    @Test
    public void testCreateLoan() {
        User user = DataProvider.userListMock().get(0);
        user.setId(1L);
        Book book = DataProvider.bookListMock().get(0);
        book.setId(1L);
        BookCopy bookCopy = DataProvider.bookCopyListMock().get(0);
        bookCopy.setId(1L);
        LoanRequest loanRequest = new LoanRequest(FIXED_DATE.plusDays(10), user.getId(), book.getId());

        when(bookCopyService.selectAvailableCopyOrThrow(1L)).thenReturn(bookCopy);
        when(userService.getUserOrThrow(1L)).thenReturn(user);
        when(loanRepository.save(any(Loan.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LoanResponse result = loanService.createLoan(loanRequest);

        assertEquals(loanRequest.dueDate(), result.dueDate());
        assertEquals(book.getIsbn(), result.book().isbn());
        assertEquals(bookCopy.getId(), result.book().bookCopyId());
        assertEquals(loanRequest.userId(), result.user().userId());

        verify(bookCopyService).selectAvailableCopyOrThrow(1L);
        verify(userService).getUserOrThrow(1L);
        verify(bookCopyService).patchState(1L, BookCopyState.LOANED);
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    void shouldPropagateExceptionFromValidator() {
        User user = DataProvider.userListMock().get(1);
        BookCopy bookCopy = DataProvider.bookCopyListMock().get(1);
        LocalDate dueDate = LocalDate.now().plusDays(10);
        when(userService.getUserOrThrow(any())).thenReturn(user);
        when(bookCopyService.selectAvailableCopyOrThrow(any())).thenReturn(bookCopy);

        doThrow(new RuntimeException())
                .when(loanValidator).validateLoan(any(), any(), any());

        assertThrows(RuntimeException.class,
                () -> loanService.createLoan(new LoanRequest(dueDate, 1L, 1L)));

        verify(loanRepository, never()).save(any(Loan.class));
        verify(bookCopyService, never()).patchState(anyLong(), any(BookCopyState.class));
    }

    @Test
    public void testReturnLoan() {
        Loan loan = DataProvider.loanListMock().get(0);

        when(loanRepository.findById(1L))
                .thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LoanResponse result = loanService.returnLoan(1L);
        assertNotNull(result.endDate());
        assertNotNull(loan.getEndDate());
        assertEquals(loan.getUser().getId(), result.user().userId());
        assertEquals(loan.getBookCopy().getId(), result.book().bookCopyId());

        verify(loanRepository).findById(1L);
        verify(loanRepository).save(any(Loan.class));
    }


    @Test
    public void testFindAll() {
        List<Loan> loanList = DataProvider.loanListMock();

        when(loanRepository.findAll())
                .thenReturn(loanList);

        List<LoanResponse> result = loanService.findAll();

        assertEquals(loanList.size(), result.size());
        assertEquals(loanList.get(1).getBookCopy().getId(), result.get(1).book().bookCopyId());
        assertEquals(loanList.get(1).getUser().getUsername(), result.get(1).user().username());
        verify(loanRepository).findAll();
    }

    @Test
    public void testFindActiveLoans() {
        List<Loan> activeLoans = DataProvider.loanListMock();
        activeLoans.forEach(loan -> loan.setEndDate(null));

        when(loanRepository.findByEndDateIsNull()).thenReturn(activeLoans);
        List<LoanResponse> result = loanService.findActiveLoans();

        assertNotNull(result);
        assertEquals(activeLoans.size(), result.size());

        verify(loanRepository).findByEndDateIsNull();
    }

    @Test
    public void testFindReturnedLoans() {

        List<Loan> returnedLoans = DataProvider.loanListMock();
        returnedLoans.forEach(loan -> loan.setEndDate(LocalDate.now()));

        when(loanRepository.findByEndDateIsNotNull()).thenReturn(returnedLoans);
        List<LoanResponse> result = loanService.findReturnedLoans();

        assertNotNull(result);
        assertEquals(returnedLoans.size(), result.size());
        verify(loanRepository).findByEndDateIsNotNull();
    }

    @Test
    public void testFindOverdueLoans() {
        List<Loan> overdueLoans = DataProvider.loanListMock();
        overdueLoans.forEach(loan -> {
            loan.setEndDate(null);
            loan.setDueDate(FIXED_DATE.minusDays(1));
        });

        when(loanRepository.findByEndDateIsNullAndDueDateBefore(any(LocalDate.class))).thenReturn(overdueLoans);
        List<LoanResponse> result = loanService.findOverdueLoans();

        assertNotNull(result);
        assertEquals(overdueLoans.size(), result.size());
        verify(loanRepository).findByEndDateIsNullAndDueDateBefore(any(LocalDate.class));
    }


    @Test
    public void testFindByUser() {
        List<Loan> userLoans = DataProvider.loanListMock();
        userLoans.forEach(loan -> loan.setEndDate(null));
        Long userId = 1L;

        when(loanRepository.findByUserIdAndEndDateIsNull(userId))
                .thenReturn(userLoans);
        List<LoanResponse> result = loanService.findByUser(userId);

        assertNotNull(result);
        assertEquals(userLoans.size(), result.size());
        verify(loanRepository).findByUserIdAndEndDateIsNull(userId);
    }

    @Test
    public void testFindByBook() {
        List<Loan> bookLoans = DataProvider.loanListMock();
        bookLoans.forEach(loan -> loan.setEndDate(null));
        String isbn = "isbn";

        when(loanRepository.findByBookCopyBookIsbnAndEndDateIsNull(isbn)).thenReturn(bookLoans);
        List<LoanResponse> result = loanService.findByBook(isbn);

        assertNotNull(result);
        assertEquals(bookLoans.size(), result.size());
        verify(loanRepository).findByBookCopyBookIsbnAndEndDateIsNull(isbn);
    }

    @Test
    public void testExistsActiveLoanByUserId(){
        when(loanRepository.existsByUserIdAndEndDateIsNull(1L))
                .thenReturn(true);

        Boolean result = loanService.existsActiveLoanByUserId(1L);

        assertTrue(result);
        verify(loanRepository).existsByUserIdAndEndDateIsNull(1L);
    }

    @Test
    public void testExistsActiveLoanByBookId(){
        when(loanRepository.existsByBookCopyBookIdAndEndDateIsNull(1L))
                .thenReturn(true);

        Boolean result = loanService.existsActiveLoanByBookId(1L);

        assertTrue(result);
        verify(loanRepository).existsByBookCopyBookIdAndEndDateIsNull(1L);
    }
}
