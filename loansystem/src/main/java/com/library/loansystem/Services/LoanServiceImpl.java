package com.library.loansystem.Services;

import com.library.loansystem.DTO.Request.LoanRequest;
import com.library.loansystem.DTO.Response.LoanResponse;
import com.library.loansystem.Entities.Book;
import com.library.loansystem.Entities.Loan;
import com.library.loansystem.Entities.User;
import com.library.loansystem.Exceptions.BusinessException;
import com.library.loansystem.Exceptions.ResourceNotFoundException;
import com.library.loansystem.Mapper.LoanMapper;
import com.library.loansystem.Repositories.LoanRepository;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class LoanServiceImpl implements LoanService {

    private static final int MAX_LOAN_DAYS = 30;
    private static final int MIN_LOAN_DAYS = 1;
    private static final int MAX_LOANS_PER_USER = 3;

    private final LoanRepository loanRepository;

    private final LoanMapper loanMapper;

    private final UserService userService;

    private final BookService bookService;

    public LoanServiceImpl(LoanRepository loanRepository, LoanMapper loanMapper, UserService userService, BookService bookService) {
        this.loanRepository = loanRepository;
        this.loanMapper = loanMapper;
        this.userService = userService;
        this.bookService = bookService;
    }

    @Override
    public List<LoanResponse> findAll() {
        return loanRepository.findAll().stream()
                .map(loanMapper::toResponse)
                .toList();
    }

    @Override
    public List<LoanResponse> findActiveLoans() {
        return loanRepository.findByActiveTrue().stream()
                .map(loanMapper::toResponse)
                .toList();
    }

    @Override
    public List<LoanResponse> findReturnedLoans() {
        return loanRepository.findByActiveFalse().stream()
                .map(loanMapper::toResponse)
                .toList();
    }

    @Override
    public List<LoanResponse> findOverdueLoans() {
        return loanRepository.findByActiveTrueAndDueDateBefore(LocalDate.now()).stream()
                .map(loanMapper::toResponse)
                .toList();
    }

    @Override
    public List<LoanResponse> findByUser(Long userId) {
        return loanRepository.findByUserIdAndActiveTrue(userId).stream()
                .map(loanMapper::toResponse)
                .toList();
    }

    @Override
    public List<LoanResponse> findByBook(Long bookId) {
        return loanRepository.findByBookIdAndActiveTrue(bookId).stream()
                .map(loanMapper::toResponse)
                .toList();
    }

    @Override
    public LoanResponse findById(Long id) {
        return loanMapper.toResponse(getLoanOrThrow(id));
    }

    @Override
    public LoanResponse createLoan(LoanRequest loanRequest) {
        User user = userService.getUserOrThrow(loanRequest.userId());
        Book book = bookService.getBookOrThrow(loanRequest.bookId());

        validateLoan(user, book, loanRequest.dueDate());

        bookService.updateStock(book.getId(), book.getStock()-1);
        Loan loan = new Loan(user, book, loanRequest.dueDate());
        return loanMapper.toResponse(loanRepository.save(loan));
    }

    @Override
    public LoanResponse returnLoan(Long id) {
        Loan loan = getLoanOrThrow(id);
        if (!loan.getActive()) throw new BusinessException("Loan has been already returned");
        loan.setActive(false);
        loan.setEndDate(LocalDate.now());
        return loanMapper.toResponse(loanRepository.save(loan));
    }

    @Override
    public Loan getLoanOrThrow(Long id) {
        return loanRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Could not found Loan with id: " + id));
    }

    @Override
    public Boolean existsActiveLoanByBookId(Long bookId) {
        return loanRepository.existsByBookIdAndActiveTrue(bookId);
    }

    @Override
    public Boolean existsActiveLoanByUserId(Long userId){return loanRepository.existsByUserIdAndActiveTrue(userId);}

    private void validateLoan(User user, Book book, LocalDate dueDate) {

        if (!user.getActive())
            throw new BusinessException("User is inactive");

        if (!book.getActive())
            throw new BusinessException("Book is inactive");

        if (book.getStock() <= 0)
            throw new BusinessException("Book has no available stock");

        if (dueDate.isBefore(LocalDate.now().plusDays(MIN_LOAN_DAYS)) || dueDate.isAfter(LocalDate.now().plusDays(MAX_LOAN_DAYS))) {
            throw new BusinessException("Loan duration must be between 1 and 30 days");
        }

        if (loanRepository.countByUserIdAndActiveTrue(user.getId()) >= MAX_LOANS_PER_USER)
            throw new BusinessException("User reached maximum active loans");

        if (loanRepository.existsByUserIdAndBookIdAndActiveTrue(user.getId(), book.getId()))
            throw new BusinessException("User already has this book on loan");

        if (loanRepository.existsByUserIdAndActiveTrueAndDueDateBefore(user.getId(), LocalDate.now())) {
            throw new BusinessException("User has overdue loans");
        }
    }
}
