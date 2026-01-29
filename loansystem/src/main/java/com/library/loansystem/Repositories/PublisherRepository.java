package com.library.loansystem.Repositories;

import com.library.loansystem.Entities.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Long> {
   public List<Publisher> findByNameContainingIgnoreCase(String name);
   public Boolean existsByName(String name);
   @Query("SELECT COUNT(b) > 0 FROM Book b WHERE b.publisher.id = :publisherId")
   boolean existsBookByPublisherId(@Param("publisherId") Long publisherId);
}
