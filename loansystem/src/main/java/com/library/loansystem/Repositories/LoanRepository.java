package com.library.loansystem.Repositories;

import com.library.loansystem.Entities.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    /// EXISTS
    Boolean existsByBookCopyBookIdAndEndDateIsNull(Long bookId);
    Boolean existsByUserIdAndEndDateIsNull(Long userId);
    Boolean existsByUserIdAndBookCopyIdAndEndDateIsNull(Long userId, Long bookCopyId);
    Boolean existsByUserIdAndEndDateIsNullAndDueDateBefore(Long userId, LocalDate date);

    ///USER
    List<Loan> findByUserId(Long userId);
    List<Loan> findByUserIdAndEndDateIsNull(Long userId);
    List<Loan> findByUserIdAndEndDateIsNotNull(Long userId);

    @Query("SELECT l FROM Loan l WHERE l.user.id = :userId AND l.dueDate < :now AND l.endDate IS NULL")
    List<Loan> findOverdue(Long userId, LocalDate now);

    int countByUserIdAndEndDateIsNull(Long userId);

    ///BOOK
    List<Loan> findByBookCopyBookIsbn(String isbn);
    List<Loan> findByBookCopyBookIsbnAndEndDateIsNull(String isbn);
    List<Loan> findByBookCopyBookIsbnAndEndDateIsNotNull(String isbn);

    @Query("SELECT l FROM Loan l WHERE l.bookCopy.book.isbn = :isbn AND l.dueDate < :now AND l.endDate IS NULL")
    List<Loan> findOverdueByIsbn(String isbn, LocalDate now);

    /// DATE
    List<Loan> findByStartDateBetween(LocalDate startDate, LocalDate endDate);
    int countByStartDateBetween(LocalDate startDate, LocalDate endDate);

    ///FILTERS
    List<Loan> findByEndDateIsNullAndDueDateBefore(LocalDate date);
    List<Loan> findByEndDateIsNull();
    List<Loan> findByEndDateIsNotNull();



}