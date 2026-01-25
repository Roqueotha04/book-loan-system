package com.library.loansystem.Repositories;

import com.library.loansystem.Entities.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    public Boolean existsByBookCopyBookIdAndActiveTrue(Long bookId);
    public Boolean existsByUserIdAndActiveTrue(Long userId);
    public Boolean existsByUserIdAndBookCopyBookIdAndActiveTrue (Long userId, Long bookCopyId);
    public Boolean existsByUserIdAndActiveTrueAndDueDateBefore(Long userId, LocalDate date);
    List<Loan> findByUserIdAndActiveTrue(Long userId);
    List<Loan> findByBookCopyBookIdAndActiveTrue (Long bookCopyId);
    List<Loan> findByActiveTrueAndDueDateBefore(LocalDate date);
    List<Loan> findByActiveTrue();
    List<Loan> findByActiveFalse();
    public int countByUserIdAndActiveTrue (Long userId);

}
