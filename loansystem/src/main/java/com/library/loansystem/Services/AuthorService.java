package com.library.loansystem.Services;

import com.library.loansystem.DTO.AuthorRequest;
import com.library.loansystem.DTO.AuthorResponse;
import com.library.loansystem.Entities.Author;

import java.util.List;

public interface AuthorService {
    public List<AuthorResponse> findAll();
    public AuthorResponse findById(Long id);
    public AuthorResponse save (AuthorRequest author);
    public AuthorResponse update (Long id, AuthorRequest author);
    public void deleteById (Long id);


}
