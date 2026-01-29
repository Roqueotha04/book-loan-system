package com.library.loansystem.Services;

import com.library.loansystem.DTO.Request.LoanRequest;
import com.library.loansystem.DTO.Response.LoanResponse;
import com.library.loansystem.DataProvider;
import com.library.loansystem.Entities.Book;
import com.library.loansystem.Entities.BookCopy;
import com.library.loansystem.Entities.Enums.BookCopyState;
import com.library.loansystem.Entities.Enums.LoanStatus;
import com.library.loansystem.Entities.Loan;
import com.library.loansystem.Entities.User;
import com.library.loansystem.Exceptions.BusinessException;
import com.library.loansystem.Exceptions.ResourceNotFoundException;
import com.library.loansystem.Mapper.LoanMapper;
import com.library.loansystem.Repositories.LoanRepository;
import com.library.loansystem.Services.Validators.LoanValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cglib.core.Local;

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
        LoanRequest loanRequest = new LoanRequest(FIXED_DATE.plusDays(10), user.getId(), book.getIsbn());

        when(bookCopyService.selectAvailableCopyOrThrow(book.getIsbn())).thenReturn(bookCopy);
        when(userService.getUserOrThrow(1L)).thenReturn(user);
        when(loanRepository.save(any(Loan.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LoanResponse result = loanService.createLoan(loanRequest);

        assertEquals(loanRequest.dueDate(), result.dueDate());
        assertEquals(book.getIsbn(), result.book().isbn());
        assertEquals(bookCopy.getId(), result.book().bookCopyId());
        assertEquals(loanRequest.userId(), result.user().userId());

        verify(bookCopyService).selectAvailableCopyOrThrow(book.getIsbn());
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
                () -> loanService.createLoan(new LoanRequest(dueDate, 1L, "123456789")));

        verify(loanRepository, never()).save(any(Loan.class));
        verify(bookCopyService, never()).patchState(anyLong(), any(BookCopyState.class));
    }

    @Test
    public void testReturnLoan_ok() {
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
    public void testReturnLoan_BussinesException() {
        Loan loan = DataProvider.loanListMock().get(0);
        loan.setEndDate(LocalDate.of(2026, 1, 21));

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        assertThrows(BusinessException.class, ()-> loanService.returnLoan(1L));
        verify(loanRepository).findById(1L);
        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    public void testRenewLoan_ok() {
        Loan loan = DataProvider.loanListMock().get(0);
        loan.setId(1L);
        loan.setEndDate(null);
        loan.setDueDate(LocalDate.now().plusDays(5));

        LocalDate newDate = LocalDate.now().plusDays(10);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LoanResponse result = loanService.renewLoan(1L, newDate);

        assertNotNull(result);
        assertEquals(newDate, result.dueDate());
        assertEquals(loan.getUser().getId(), result.user().userId());

        verify(loanRepository).findById(1L);
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    public void testRenewLoan_finishedLoan() {
        Loan loan = DataProvider.loanListMock().get(0);
        loan.setId(1L);
        loan.setEndDate(FIXED_DATE);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        assertThrows(BusinessException.class,
                () -> loanService.renewLoan(1L, FIXED_DATE.plusDays(10)));

        verify(loanRepository).findById(1L);
        verify(loanRepository, never()).save(any());
    }

    @Test
    public void testRenewLoan_overdueLoan() {
        Loan loan = DataProvider.loanListMock().get(0);
        loan.setId(1L);
        loan.setEndDate(null);
        loan.setDueDate(LocalDate.now().minusDays(1));

        LocalDate newDate = LocalDate.now().plusDays(5);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        assertThrows(BusinessException.class,
                () -> loanService.renewLoan(1L, newDate));

        verify(loanRepository).findById(1L);
        verify(loanRepository, never()).save(any());
    }

    @Test
    public void testRenewLoan_notFound() {
        when(loanRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> loanService.renewLoan(1L, FIXED_DATE.plusDays(5)));

        verify(loanRepository).findById(1L);
        verify(loanRepository, never()).save(any());
    }

    @Test
    public void testFindByDateRange_ok() {
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate endDate = LocalDate.of(2026, 1, 31);

        List<Loan> loans = DataProvider.loanListMock();
        loans.forEach(l -> l.setStartDate(LocalDate.of(2026, 1, 10)));

        when(loanRepository.findByStartDateBetween(startDate, endDate))
                .thenReturn(loans);

        List<LoanResponse> result = loanService.findByDateRange(startDate, endDate);

        assertNotNull(result);
        assertEquals(loans.size(), result.size());
        assertEquals(loans.get(0).getUser().getId(), result.get(0).user().userId());

        verify(loanRepository).findByStartDateBetween(startDate, endDate);
    }

    @Test
    public void testFindByDateRange_invalidDates() {
        LocalDate startDate = LocalDate.of(2026, 2, 1);
        LocalDate endDate = LocalDate.of(2026, 1, 1);

        assertThrows(BusinessException.class,
                () -> loanService.findByDateRange(startDate, endDate));

        verify(loanRepository, never()).findByStartDateBetween(any(), any());
    }

    @Test
    public void testCountByDateRange_ok() {
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate endDate = LocalDate.of(2026, 1, 31);

        when(loanRepository.countByStartDateBetween(startDate, endDate))
                .thenReturn(3);

        int result = loanService.countByDateRange(startDate, endDate);

        assertEquals(3, result);
        verify(loanRepository).countByStartDateBetween(startDate, endDate);
    }

    @Test
    public void testCountByDateRange_invalidDates() {
        LocalDate startDate = LocalDate.of(2026, 3, 1);
        LocalDate endDate = LocalDate.of(2026, 2, 1);

        assertThrows(BusinessException.class, () -> loanService.countByDateRange(startDate, endDate));

        verify(loanRepository, never()).countByStartDateBetween(any(), any());
    }


    @Test
    public void testFindAll() {
        List<Loan> loanList = DataProvider.loanListMock();

        when(loanRepository.findAll()).thenReturn(loanList);

        List<LoanResponse> result = loanService.findAll(null);

        assertEquals(loanList.size(), result.size());
        assertEquals(loanList.get(1).getBookCopy().getId(), result.get(1).book().bookCopyId());
        assertEquals(loanList.get(1).getUser().getUsername(), result.get(1).user().username());
        verify(loanRepository).findAll();
    }

    @Test
    public void testFindAll_Active() {
        List<Loan> activeLoans = DataProvider.loanListMock();
        activeLoans.forEach(l -> l.setEndDate(null));

        when(loanRepository.findByEndDateIsNull()).thenReturn(activeLoans);

        List<LoanResponse> result = loanService.findAll(LoanStatus.ACTIVE);

        assertEquals(activeLoans.size(), result.size());
        assertNull(result.get(0).endDate());
        verify(loanRepository).findByEndDateIsNull();
    }

    @Test
    public void testFindAll_Returned() {
        List<Loan> returnedLoans = DataProvider.loanListMock();
        returnedLoans.forEach(l -> l.setEndDate(LocalDate.now()));

        when(loanRepository.findByEndDateIsNotNull()).thenReturn(returnedLoans);

        List<LoanResponse> result = loanService.findAll(LoanStatus.RETURNED);

        assertEquals(returnedLoans.size(), result.size());
        assertNotNull(result.get(0).endDate());
        verify(loanRepository).findByEndDateIsNotNull();
    }

    @Test
    public void testFindAll_Overdue() {
        List<Loan> overdueLoans = DataProvider.loanListMock();
        overdueLoans.forEach(l -> {
            l.setEndDate(null);
            l.setDueDate(LocalDate.now().minusDays(5));
        });

        when(loanRepository.findByEndDateIsNullAndDueDateBefore(any(LocalDate.class)))
                .thenReturn(overdueLoans);

        List<LoanResponse> result = loanService.findAll(LoanStatus.OVERDUE);

        assertEquals(overdueLoans.size(), result.size());
        verify(loanRepository).findByEndDateIsNullAndDueDateBefore(any(LocalDate.class));
    }

    @Test
    public void testFindByUser_NullStatus() {
        List<Loan> userLoans = DataProvider.loanListMock();
        Long userId = 1L;

        when(loanRepository.findByUserId(userId)).thenReturn(userLoans);

        List<LoanResponse> result = loanService.findByUser(userId, null);

        assertEquals(userLoans.size(), result.size());
        assertEquals(userLoans.get(0).getUser().getId(), result.get(0).user().userId());
        verify(loanRepository).findByUserId(userId);
    }

    @Test
    public void testFindByUser_ActiveStatus() {
        List<Loan> activeLoans = DataProvider.loanListMock();
        activeLoans.forEach(l -> l.setEndDate(null));
        Long userId = 1L;

        when(loanRepository.findByUserIdAndEndDateIsNull(userId)).thenReturn(activeLoans);

        List<LoanResponse> result = loanService.findByUser(userId, LoanStatus.ACTIVE);

        assertEquals(activeLoans.size(), result.size());
        assertNull(result.get(0).endDate());
        verify(loanRepository).findByUserIdAndEndDateIsNull(userId);
    }

    @Test
    public void testFindByUser_ReturnedStatus() {
        List<Loan> returnedLoans = DataProvider.loanListMock();
        returnedLoans.forEach(l -> l.setEndDate(LocalDate.now()));
        Long userId = 1L;

        when(loanRepository.findByUserIdAndEndDateIsNotNull(userId)).thenReturn(returnedLoans);

        List<LoanResponse> result = loanService.findByUser(userId, LoanStatus.RETURNED);

        assertEquals(returnedLoans.size(), result.size());
        assertNotNull(result.get(0).endDate());
        verify(loanRepository).findByUserIdAndEndDateIsNotNull(userId);
    }

    @Test
    public void testFindByUser_OverdueStatus() {
        List<Loan> overdueLoans = DataProvider.loanListMock();
        overdueLoans.forEach(l -> l.setEndDate(null));
        Long userId = 1L;

        when(loanRepository.findOverdue(eq(userId), any(LocalDate.class))).thenReturn(overdueLoans);

        List<LoanResponse> result = loanService.findByUser(userId, LoanStatus.OVERDUE);

        assertEquals(overdueLoans.size(), result.size());
        verify(loanRepository).findOverdue(eq(userId), any(LocalDate.class));
    }

    @Test
    public void testFindByBook_NullStatus() {
        List<Loan> bookLoans = DataProvider.loanListMock();
        String isbn = "123456789";

        when(loanRepository.findByBookCopyBookIsbn(isbn)).thenReturn(bookLoans);

        List<LoanResponse> result = loanService.findByBook(isbn, null);

        assertEquals(bookLoans.size(), result.size());
        assertEquals(bookLoans.get(0).getBookCopy().getBook().getIsbn(), result.get(0).book().isbn());
        verify(loanRepository).findByBookCopyBookIsbn(isbn);
    }

    @Test
    public void testFindByBook_ActiveStatus() {
        List<Loan> activeLoans = DataProvider.loanListMock();
        activeLoans.forEach(l -> l.setEndDate(null));
        String isbn = "123456789";

        when(loanRepository.findByBookCopyBookIsbnAndEndDateIsNull(isbn)).thenReturn(activeLoans);

        List<LoanResponse> result = loanService.findByBook(isbn, LoanStatus.ACTIVE);

        assertEquals(activeLoans.size(), result.size());
        assertNull(result.get(0).endDate());
        verify(loanRepository).findByBookCopyBookIsbnAndEndDateIsNull(isbn);
    }

    @Test
    public void testFindByBook_ReturnedStatus() {
        List<Loan> returnedLoans = DataProvider.loanListMock();
        returnedLoans.forEach(l -> l.setEndDate(LocalDate.now()));
        String isbn = "123456789";

        when(loanRepository.findByBookCopyBookIsbnAndEndDateIsNotNull(isbn)).thenReturn(returnedLoans);

        List<LoanResponse> result = loanService.findByBook(isbn, LoanStatus.RETURNED);

        assertEquals(returnedLoans.size(), result.size());
        assertNotNull(result.get(0).endDate());
        verify(loanRepository).findByBookCopyBookIsbnAndEndDateIsNotNull(isbn);
    }

    @Test
    public void testFindByBook_OverdueStatus() {
        List<Loan> overdueLoans = DataProvider.loanListMock();
        overdueLoans.forEach(l -> l.setEndDate(null));
        String isbn = "123456789";

        when(loanRepository.findOverdueByIsbn(eq(isbn), any(LocalDate.class))).thenReturn(overdueLoans);

        List<LoanResponse> result = loanService.findByBook(isbn, LoanStatus.OVERDUE);

        assertEquals(overdueLoans.size(), result.size());
        verify(loanRepository).findOverdueByIsbn(eq(isbn), any(LocalDate.class));
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

    @Test
    public void testFindById(){
        Loan loan = DataProvider.loanListMock().get(1);
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        LoanResponse result = loanService.findById(1L);
        assertEquals(loan.getStartDate(), result.startDate());
        assertEquals(loan.getUser().getUsername(), result.user().username());
        assertEquals(loan.getBookCopy().getBook().getIsbn(), result.book().isbn());
        verify(loanRepository).findById(1L);
    }

    @Test
    public void testGetLoanOrThrow_ok(){
        Loan loan = DataProvider.loanListMock().get(1);
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

         Loan result = loanService.getLoanOrThrow(1L);
         assertEquals(loan.getStartDate(), result.getStartDate());
         assertEquals(loan.getUser().getUsername(), result.getUser().getUsername());
         assertEquals(loan.getBookCopy().getBook().getName(), result.getBookCopy().getBook().getName());
         verify(loanRepository).findById(1L);
    }

    @Test
    public void testGetLoanOrThrow_notFound(){
        when(loanRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, ()-> loanService.getLoanOrThrow(1L));
        verify(loanRepository).findById(1L);
    }
}
