package com.library.loansystem.Repositories;

import com.library.loansystem.Entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Optional<UserEntity> findByEmail(String email);
    List<UserEntity> findByUsernameContainingIgnoreCase(String username);
    @Query("""
        SELECT COUNT(l) > 0
        FROM UserEntity u
        JOIN u.loanList l
        WHERE u.id = :userId
        AND l.endDate = null
        """)
    boolean hasActiveLoans(Long userId);


}
