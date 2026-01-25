package com.library.loansystem.Repositories;

import com.library.loansystem.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    @Query("""
        SELECT COUNT(l) > 0
        FROM User u
        JOIN u.loanList l
        WHERE u.id = :userId
        AND l.active = true
        """)
    boolean hasActiveLoans(Long userId);
}
