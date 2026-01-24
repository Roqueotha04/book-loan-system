package com.library.loansystem.Repositories;

import com.library.loansystem.DTO.Response.BookResponse;
import com.library.loansystem.Entities.Book;
import com.library.loansystem.Entities.Enums.BookGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);
    public List<Book> findByNameContainingIgnoreCase(String name);
    public List<Book> findByGenre(BookGenre genre);
    public List<Book> findByAuthorXBooks_Author_Id(Long authorId);




}
