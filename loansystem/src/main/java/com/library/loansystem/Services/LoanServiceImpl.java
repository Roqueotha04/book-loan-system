package com.library.loansystem.Services;

import com.library.loansystem.DTO.Request.LoanRequest;
import com.library.loansystem.DTO.Response.LoanResponse;
import com.library.loansystem.Entities.Book;
import com.library.loansystem.Entities.BookCopy;
import com.library.loansystem.Entities.Enums.BookCopyState;
import com.library.loansystem.Entities.Loan;
import com.library.loansystem.Entities.User;
import com.library.loansystem.Exceptions.BusinessException;
import com.library.loansystem.Exceptions.ResourceNotFoundException;
import com.library.loansystem.Mapper.LoanMapper;
import com.library.loansystem.Repositories.LoanRepository;
import com.library.loansystem.Services.Validators.LoanValidator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class LoanServiceImpl implements LoanService {

    private final LoanValidator loanValidator;

    private final LoanRepository loanRepository;

    private final LoanMapper loanMapper;

    private final BookCopyService bookCopyService;

    private final UserService userService;

    private final BookService bookService;

    public LoanServiceImpl(LoanValidator loanValidator, LoanRepository loanRepository, LoanMapper loanMapper, BookCopyService bookCopyService, UserService userService, BookService bookService) {
        this.loanValidator = loanValidator;
        this.loanRepository = loanRepository;
        this.loanMapper = loanMapper;
        this.bookCopyService = bookCopyService;
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
        loanValidator.validateLoan(user, book, loanRequest.dueDate());
        BookCopy copy = bookCopyService.selectAvailableCopy(book.getId());
        bookCopyService.patchState(copy.getId(), BookCopyState.LOANED);

        Loan loan = new Loan(user, copy, loanRequest.dueDate());

        return loanMapper.toResponse(loanRepository.save(loan));
    }



    @Override
    public LoanResponse returnLoan(Long id) {
        Loan loan = getLoanOrThrow(id);
        if (!loan.getActive()) throw new BusinessException("Loan has been already returned");
        loan.setActive(false);
        loan.setEndDate(LocalDate.now());
        bookCopyService.patchState(loan.getBookCopy().getId(), BookCopyState.AVAILABLE);
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


}
