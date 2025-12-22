package com.library.loansystem.Services;

import com.library.loansystem.DTO.Author.AuthorRequest;
import com.library.loansystem.DTO.Author.AuthorResponse;
import com.library.loansystem.Entities.Author;

import java.util.List;

public interface AuthorService {
    public List<AuthorResponse> findAll();
    public AuthorResponse findById(Long id);
    public AuthorResponse save (AuthorRequest author);
    public AuthorResponse update (Long id, AuthorRequest author);
    public void deleteById (Long id);
    public Author getAuthorOrThrow(Long id);
}
