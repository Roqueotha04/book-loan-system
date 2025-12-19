package com.library.loansystem.Repositories;

import com.library.loansystem.Entities.AuthorXBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorXBookRepository extends JpaRepository<AuthorXBook, Long> {
}
