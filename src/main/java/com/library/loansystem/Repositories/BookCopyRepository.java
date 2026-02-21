package com.library.loansystem.Repositories;

import com.library.loansystem.Entities.Book;
import com.library.loansystem.Entities.BookCopy;
import com.library.loansystem.Entities.Enums.BookCopyState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {
    public Optional<BookCopy> findFirstByBookIsbnAndState (String isbn, BookCopyState state);
    public List<BookCopy> findByBookIsbnAndState(String Isbn, BookCopyState state);
    public List<BookCopy> findByBookIsbn(String Isbn);

    @Query("SELECT COUNT(bc) FROM BookCopy bc WHERE bc.book.isbn = :isbn AND bc.state = :state")
    int countByIsbnAndState(@Param("isbn") String isbn, @Param("state") BookCopyState state);
}
