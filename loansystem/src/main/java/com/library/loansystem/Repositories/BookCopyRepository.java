package com.library.loansystem.Repositories;

import com.library.loansystem.Entities.Book;
import com.library.loansystem.Entities.BookCopy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {
}
