package com.library.loansystem.Repositories;

import com.library.loansystem.Entities.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    public Boolean existsByBookCopyBookIdAndEndDateIsNull(Long bookId);
    public Boolean existsByUserIdAndEndDateIsNull(Long userId);
    public Boolean existsByUserIdAndBookCopyIdAndEndDateIsNull (Long userId, Long bookCopyId);
    public Boolean existsByUserIdAndEndDateIsNullAndDueDateBefore(Long userId, LocalDate date);
    List<Loan> findByUserIdAndEndDateIsNull(Long userId);
    List<Loan> findByBookCopyBookIsbnAndEndDateIsNull (String isbn);
    List<Loan> findByEndDateIsNullAndDueDateBefore(LocalDate date);
    List<Loan> findByEndDateIsNull();
    List<Loan> findByEndDateIsNotNull();
    public int countByUserIdAndEndDateIsNull (Long userId);

}