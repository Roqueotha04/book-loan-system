package com.library.loansystem.Services;

import com.library.loansystem.DTO.Request.LoanRequest;
import com.library.loansystem.DTO.Response.LoanResponse;
import com.library.loansystem.Entities.Book;
import com.library.loansystem.Entities.Loan;
import com.library.loansystem.Entities.User;
import com.library.loansystem.Exceptions.BadRequestException;
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


}
