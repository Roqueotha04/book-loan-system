package com.library.loansystem.Repositories;

import com.library.loansystem.Entities.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    @Query("SELECT COUNT(axb) > 0 FROM Author a JOIN a.authorXBooks axb WHERE a.id = :authorId")
    boolean existsBookByAuthorId(@Param("authorId") Long authorId);
}
