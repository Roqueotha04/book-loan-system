package com.library.loansystem.Repositories;

import com.library.loansystem.Entities.Book;
import com.library.loansystem.Entities.BookCopy;
import com.library.loansystem.Entities.Enums.BookCopyState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {
    public Optional<BookCopy> findFirstByBookIdAndState (Long bookId, BookCopyState state);
    List<BookCopy> findByBookIsbnAndState(String Isbn, BookCopyState state);
    List<BookCopy> findByBookIsbn(String Isbn);
}
