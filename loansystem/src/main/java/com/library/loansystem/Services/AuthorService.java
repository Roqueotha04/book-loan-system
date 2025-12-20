package com.library.loansystem.Services;

import com.library.loansystem.Entities.Author;

import java.util.List;

public interface AuthorService {
    public List<Author> findAll();
    public Author findById(Long id);
    public Author save (Author author);
    public void deleteById (Long id);
    public Author update (Long id, Author author);

}
