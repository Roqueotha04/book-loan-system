package com.library.loansystem.Services;

import com.library.loansystem.DTO.Request.LoanRequest;
import com.library.loansystem.DTO.Response.LoanResponse;
import com.library.loansystem.Entities.BookCopy;
import com.library.loansystem.Entities.Enums.BookCopyState;
import com.library.loansystem.Entities.Enums.LoanStatus;
import com.library.loansystem.Entities.Loan;
import com.library.loansystem.Entities.UserEntity;
import com.library.loansystem.Exceptions.BusinessException;
import com.library.loansystem.Exceptions.ResourceNotFoundException;
import com.library.loansystem.Mapper.LoanMapper;
import com.library.loansystem.Repositories.LoanRepository;
import com.library.loansystem.Services.Validators.LoanValidator;
import com.library.loansystem.Services.Validators.UserValidator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class LoanServiceImpl implements LoanService {

    private final LoanValidator loanValidator;

    private final LoanRepository loanRepository;

    private final LoanMapper loanMapper;

    private final BookCopyService bookCopyService;

    private final UserValidator userValidator;

    public LoanServiceImpl(LoanValidator loanValidator, LoanRepository loanRepository, LoanMapper loanMapper, BookCopyService bookCopyService, UserValidator userValidator) {
        this.loanValidator = loanValidator;
        this.loanRepository = loanRepository;
        this.loanMapper = loanMapper;
        this.bookCopyService = bookCopyService;
        this.userValidator = userValidator;
    }

    public List<LoanResponse> findAll(LoanStatus status) {
        List<Loan> loans;

        if (status == null) {
            loans = loanRepository.findAll();
        } else {
            loans = switch (status) {
                case ACTIVE -> loanRepository.findByEndDateIsNull();
                case RETURNED -> loanRepository.findByEndDateIsNotNull();
                case OVERDUE -> loanRepository.findByEndDateIsNullAndDueDateBefore(LocalDate.now());
            };
        }

        return loans.stream().map(loanMapper::toResponse).toList();
    }

    @Override
    public List<LoanResponse> findByUser(Long userId, LoanStatus status) {
        List<Loan> loans;

        if (status == null) {
            loans=loanRepository.findByUserEntityId(userId);
        }else {
            loans=switch (status) {
                case ACTIVE -> loanRepository.findByUserEntityIdAndEndDateIsNull(userId);
                case RETURNED -> loanRepository.findByUserEntityIdAndEndDateIsNotNull(userId);
                case OVERDUE -> loanRepository.findOverdue(userId, LocalDate.now());
            };
        }
        return loans.stream().map(loanMapper::toResponse).toList();
    }

    @Override
    public List<LoanResponse> findByBook(String isbn, LoanStatus status) {
        List<Loan> loans;

        if (status == null) {
            loans = loanRepository.findByBookCopyBookIsbn(isbn);
        } else {
            loans = switch (status) {
                case ACTIVE -> loanRepository.findByBookCopyBookIsbnAndEndDateIsNull(isbn);
                case RETURNED -> loanRepository.findByBookCopyBookIsbnAndEndDateIsNotNull(isbn);
                case OVERDUE -> loanRepository.findOverdueByIsbn(isbn, LocalDate.now());
            };
        }
        return loans.stream().map(loanMapper::toResponse).toList();
    }

    @Override
    public LoanResponse findById(Long id) {
        return loanMapper.toResponse(getLoanOrThrow(id));
    }

    @Override
    public LoanResponse createLoan(LoanRequest loanRequest, Authentication auth) {
        UserEntity userEntity =  userValidator.validateUserRole(loanRequest.userId(), auth);

        BookCopy bookCopy = bookCopyService.selectAvailableCopyOrThrow(loanRequest.isbn());
        loanValidator.validateLoan(userEntity, bookCopy, loanRequest.dueDate());

        bookCopyService.patchState(bookCopy.getId(), BookCopyState.LOANED);
        Loan loan = new Loan(userEntity, bookCopy, loanRequest.dueDate());
        return loanMapper.toResponse(loanRepository.save(loan));
    }

    @Override
    public LoanResponse returnLoan(Long id) {


        Loan loan = getLoanOrThrow(id);
        if (loan.getEndDate() != null) throw new BusinessException("Loan has been already returned");
        loan.setEndDate(LocalDate.now());
        bookCopyService.patchState(loan.getBookCopy().getId(), BookCopyState.AVAILABLE);
        return loanMapper.toResponse(loanRepository.save(loan));
    }

    @Override
    public LoanResponse renewLoan(Long loanId, LocalDate newDate) {
        Loan loan = getLoanOrThrow(loanId);
        if (loan.getEndDate()!=null) throw new BusinessException("Cannot renew a finished loan");
        if (newDate.isBefore(loan.getDueDate())) throw new BusinessException("New date must not be before actual due date");
        if (loan.getDueDate().isBefore(LocalDate.now())) throw new BusinessException("Cannot renew an overdue loan. Please return the book first.");

        loan.setDueDate(newDate);
        return loanMapper.toResponse(loanRepository.save(loan));
    }

    @Override
    public List<LoanResponse> findByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new BusinessException("Start date must be before end date");
        }
        return loanRepository.findByStartDateBetween(startDate, endDate).stream()
                .map(loanMapper::toResponse)
                .toList();
    }

    @Override
    public int countByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new BusinessException("Start date must be before end date");
        }

        return loanRepository.countByStartDateBetween(startDate, endDate);
    }

    @Override
    public Loan getLoanOrThrow(Long id) {
        return loanRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Could not found Loan with id: " + id));
    }

    @Override
    public Boolean existsActiveLoanByBookId(Long bookId) {
        return loanRepository.existsByBookCopyBookIdAndEndDateIsNull(bookId);
    }

    @Override
    public Boolean existsActiveLoanByUserId(Long userId){return loanRepository.existsByUserEntityIdAndEndDateIsNull(userId);}


}
