package com.library.loansystem.Repositories;

import com.library.loansystem.Entities.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    /// EXISTS
    Boolean existsByBookCopyBookIdAndEndDateIsNull(Long userEntityId);
    Boolean existsByUserEntityIdAndEndDateIsNull(Long userEntityId);
    boolean existsByUserEntityIdAndBookCopyBookIsbnAndEndDateIsNull(Long userId, String isbn);
    Boolean existsByUserEntityIdAndEndDateIsNullAndDueDateBefore(Long userEntityId, LocalDate date);

    ///USER
    List<Loan> findByUserEntityId(Long userEntityId);
    List<Loan> findByUserEntityIdAndEndDateIsNull(Long userEntityId);
    List<Loan> findByUserEntityIdAndEndDateIsNotNull(Long userEntityId);

    @Query("SELECT l FROM Loan l WHERE l.userEntity.id = :userId AND l.dueDate < :now AND l.endDate IS NULL")
    List<Loan> findOverdue(@Param("userId") Long userId, @Param("now") LocalDate now);

    int countByUserEntityIdAndEndDateIsNull(Long userId);

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