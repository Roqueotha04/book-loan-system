package com.library.loansystem.Repositories;

import com.library.loansystem.Entities.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    public Boolean existsByBookIdAndActiveTrue(Long bookId);
}
